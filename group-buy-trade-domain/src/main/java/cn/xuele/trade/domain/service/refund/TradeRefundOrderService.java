package cn.xuele.trade.domain.service.refund;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.port.ITeamStockReservationPort;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import cn.xuele.trade.domain.service.ITradeRefundOrderService;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;

/**
 * 交易退单领域服务实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class TradeRefundOrderService implements ITradeRefundOrderService {

    private final ITradeRepository tradeRepository;
    private final ITeamStockReservationPort teamStockReservationPort;
    private final BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter;

    public TradeRefundOrderService(
            ITradeRepository tradeRepository,
            ITeamStockReservationPort teamStockReservationPort,
            BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter) {
        this.tradeRepository = tradeRepository;
        this.teamStockReservationPort = teamStockReservationPort;
        this.tradeRefundRuleFilter = tradeRefundRuleFilter;
    }

    @Override
    public TradeRefundResultEntity refundTradeOrder(TradeRefundCommandEntity command) throws Exception {
        TradeRefundRuleFilterFactory.DynamicContext context = new TradeRefundRuleFilterFactory.DynamicContext();
        TradeRefundResultEntity ruleResult = tradeRefundRuleFilter.apply(command, context);
        if (ruleResult != null) {
            recoverTeamStockWhenNeeded(ruleResult);
            return ruleResult;
        }
        if (context.getRefundAggregate() == null) {
            throw new IllegalStateException("trade refund rule chain returned no aggregate");
        }
        TradeRefundResultEntity refundResult = tradeRepository.refundOrder(context.getRefundAggregate());
        recoverTeamStockWhenNeeded(refundResult);
        return refundResult;
    }

    private void recoverTeamStockWhenNeeded(TradeRefundResultEntity result) {
        if (result == null || result.getOrder() == null || result.getTeam() == null) {
            return;
        }
        if (!needRecoverTeamStock(result)) {
            return;
        }
        teamStockReservationPort.recover(
                result.getOrder().getTeamId(),
                "refund-recover:" + result.getOrder().getOrderId(),
                result.getTeam().getValidEndTime()
        );
    }

    private boolean needRecoverTeamStock(TradeRefundResultEntity result) {
        RefundTypeEnumVO refundType = result.getRefundType();
        if (RefundTypeEnumVO.UNPAID.equals(refundType) || RefundTypeEnumVO.PAID_UNFORMED.equals(refundType)) {
            return true;
        }
        return result.isIdempotentHit() && GroupBuyTeamStatusEnumVO.PROGRESS.equals(result.getTeam().getStatus());
    }
}
