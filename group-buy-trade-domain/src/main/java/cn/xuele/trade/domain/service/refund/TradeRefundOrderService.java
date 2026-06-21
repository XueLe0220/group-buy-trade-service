package cn.xuele.trade.domain.service.refund;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
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
    private final BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter;

    public TradeRefundOrderService(
            ITradeRepository tradeRepository,
            BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter) {
        this.tradeRepository = tradeRepository;
        this.tradeRefundRuleFilter = tradeRefundRuleFilter;
    }

    @Override
    public TradeRefundResultEntity refundTradeOrder(TradeRefundCommandEntity command) throws Exception {
        TradeRefundRuleFilterFactory.DynamicContext context = new TradeRefundRuleFilterFactory.DynamicContext();
        TradeRefundResultEntity ruleResult = tradeRefundRuleFilter.apply(command, context);
        if (ruleResult != null) {
            return ruleResult;
        }
        if (context.getRefundAggregate() == null) {
            throw new IllegalStateException("trade refund rule chain returned no aggregate");
        }
        return tradeRepository.refundOrder(context.getRefundAggregate());
    }
}
