package cn.xuele.trade.domain.service.settlement;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.service.ITradeSettlementOrderService;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;

/**
 * 交易结算领域服务实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    private final ITradeRepository tradeRepository;
    private final BusinessLinkedList<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> tradeSettlementRuleFilter;

    public TradeSettlementOrderService(
            ITradeRepository tradeRepository,
            BusinessLinkedList<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> tradeSettlementRuleFilter) {
        this.tradeRepository = tradeRepository;
        this.tradeSettlementRuleFilter = tradeSettlementRuleFilter;
    }

    @Override
    public TradeSettlementResultEntity settlementTradeOrder(TradeSettlementCommandEntity command) throws Exception {
        TradeSettlementRuleFilterFactory.DynamicContext context = new TradeSettlementRuleFilterFactory.DynamicContext();
        TradeSettlementResultEntity ruleResult = tradeSettlementRuleFilter.apply(command, context);
        if (ruleResult != null) {
            return ruleResult;
        }
        if (context.getSettlementAggregate() == null) {
            throw new IllegalStateException("trade settlement rule chain returned no aggregate");
        }
        return tradeRepository.settlementOrder(context.getSettlementAggregate());
    }
}
