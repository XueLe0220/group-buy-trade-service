package cn.xuele.trade.domain.service.lock.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;

/**
 * 锁单幂等规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class LockIdempotentRuleFilter implements ILogicHandler<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> {

    private final ITradeRepository tradeRepository;

    public LockIdempotentRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeLockResultEntity apply(TradeLockCommandEntity requestParameter,
                                       TradeLockRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity existOrder = tradeRepository.queryOrderByUserIdAndOutTradeNo(
                requestParameter.getUserId(),
                requestParameter.getOutTradeNo());
        if (existOrder != null) {
            return TradeLockResultEntity.builder()
                    .existOrder(existOrder)
                    .build();
        }
        return null;
    }
}
