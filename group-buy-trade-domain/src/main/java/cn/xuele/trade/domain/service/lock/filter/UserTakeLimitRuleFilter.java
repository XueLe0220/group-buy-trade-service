package cn.xuele.trade.domain.service.lock.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;

/**
 * 用户限购规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class UserTakeLimitRuleFilter implements ILogicHandler<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> {

    private final ITradeRepository tradeRepository;

    public UserTakeLimitRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeLockResultEntity apply(TradeLockCommandEntity requestParameter,
                                       TradeLockRuleFilterFactory.DynamicContext dynamicContext) {
        ActivityTrialEntity activityTrial = dynamicContext.getActivityTrial();
        Integer takeLimitCount = activityTrial.getTakeLimitCount();
        if (takeLimitCount == null || takeLimitCount <= 0) {
            dynamicContext.setUserOrderCount(0);
            return null;
        }

        Integer userOrderCount = tradeRepository.queryUserOrderCount(activityTrial.getActivityId(), requestParameter.getUserId());
        userOrderCount = userOrderCount == null ? 0 : userOrderCount;
        if (userOrderCount >= takeLimitCount) {
            throw new AppException(ResponseCode.TRADE_TAKE_LIMIT);
        }

        dynamicContext.setUserOrderCount(userOrderCount);
        return null;
    }
}
