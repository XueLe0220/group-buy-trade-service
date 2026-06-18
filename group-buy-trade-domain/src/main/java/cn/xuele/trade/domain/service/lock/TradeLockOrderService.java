package cn.xuele.trade.domain.service.lock;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.ITradeLockOrderService;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;

/**
 * 交易锁单领域服务实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class TradeLockOrderService implements ITradeLockOrderService {

    private final ITradeRepository tradeRepository;
    private final BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> tradeLockRuleFilter;

    public TradeLockOrderService(ITradeRepository tradeRepository,
                                 BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> tradeLockRuleFilter) {
        this.tradeRepository = tradeRepository;
        this.tradeLockRuleFilter = tradeLockRuleFilter;
    }

    @Override
    public TradeOrderEntity lockTradeOrder(TradeLockCommandEntity command) throws Exception {
        TradeLockResultEntity ruleResult = tradeLockRuleFilter.apply(command, new TradeLockRuleFilterFactory.DynamicContext());
        if (ruleResult == null) {
            throw new IllegalStateException("trade lock rule chain returned null");
        }
        if (ruleResult.isIdempotentHit()) {
            return ruleResult.getExistOrder();
        }
        return tradeRepository.lockOrder(ruleResult.getLockAggregate());
    }
}
