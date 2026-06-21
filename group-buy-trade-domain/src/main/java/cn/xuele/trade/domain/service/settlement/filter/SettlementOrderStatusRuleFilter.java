package cn.xuele.trade.domain.service.settlement.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;

import java.util.Objects;

/**
 * 结算订单状态规则。
 * CREATE 订单继续结算，COMPLETE 订单按同流水同金额幂等返回，CLOSE 订单拒绝结算。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class SettlementOrderStatusRuleFilter implements ILogicHandler<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> {

    private final ITradeRepository tradeRepository;

    public SettlementOrderStatusRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeSettlementResultEntity apply(TradeSettlementCommandEntity requestParameter,
                                             TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = dynamicContext.getOrder();
        if (TradeOrderStatusEnumVO.CLOSE.equals(order.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单已关闭，不能结算");
        }
        if (!TradeOrderStatusEnumVO.COMPLETE.equals(order.getStatus())) {
            return null;
        }
        if (!Objects.equals(order.getPayNo(), requestParameter.getPayNo())
                || order.getPaidAmount() == null
                || order.getPaidAmount().compareTo(requestParameter.getPaidAmount()) != 0) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单已支付，但支付流水或金额不一致");
        }
        GroupBuyTeamEntity team = tradeRepository.queryTeamByTeamId(order.getTeamId());
        if (team == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍不存在");
        }
        return TradeSettlementResultEntity.builder()
                .order(order)
                .team(team)
                .idempotentHit(true)
                .build();
    }
}
