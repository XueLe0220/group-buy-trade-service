package cn.xuele.trade.domain.service.lock;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.port.ITeamStockReservationPort;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.entity.TradeLockOrderResultEntity;
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
    private final ITeamStockReservationPort teamStockReservationPort;
    private final BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext,
            TradeLockResultEntity> tradeLockRuleFilter;

    public TradeLockOrderService(ITradeRepository tradeRepository, ITeamStockReservationPort teamStockReservationPort,
                                 BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext
                                         , TradeLockResultEntity> tradeLockRuleFilter) {
        this.tradeRepository = tradeRepository;
        this.teamStockReservationPort = teamStockReservationPort;
        this.tradeLockRuleFilter = tradeLockRuleFilter;
    }

    @Override
    public TradeOrderEntity lockTradeOrder(TradeLockCommandEntity command) throws Exception {
        TradeLockRuleFilterFactory.DynamicContext context = new TradeLockRuleFilterFactory.DynamicContext();

        try {
            TradeLockResultEntity ruleResult = tradeLockRuleFilter.apply(command, context);
            if (ruleResult == null) {
                throw new IllegalStateException("trade lock rule chain returned null");
            }
            if (ruleResult.isIdempotentHit()) {
                return ruleResult.getExistOrder();
            }
            GroupBuyLockAggregate lockAggregate = ruleResult.getLockAggregate();
            TradeLockOrderResultEntity lockOrderResult = tradeRepository.lockOrder(lockAggregate);
            if (context.isTeamStockReserved() && lockOrderResult.isIdempotentHit()) {
                recoverTeamStock(context);
            }
            return lockOrderResult.getOrder();

        } catch (Exception e) {
            if (context.isTeamStockReserved()) {
                recoverTeamStock(context);
            }
            TradeOrderEntity existOrder = queryExistOrderWhenIndexConflict(command, e);
            if (existOrder != null) {
                return existOrder;
            }
            throw e;
        }
    }

    private void recoverTeamStock(TradeLockRuleFilterFactory.DynamicContext context) {
        teamStockReservationPort.recover(
                context.getTeam().getTeamId(),
                context.getTeamStockRecoveryBizId(),
                context.getTeam().getValidEndTime()
        );
    }

    private TradeOrderEntity queryExistOrderWhenIndexConflict(TradeLockCommandEntity command, Exception e) {
        if (!(e instanceof AppException appException)
                || !ResponseCode.INDEX_EXCEPTION.getCode().equals(appException.getCode())) {
            return null;
        }
        return tradeRepository.queryOrderByUserIdAndOutTradeNo(command.getUserId(), command.getOutTradeNo());
    }
}
