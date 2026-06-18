package cn.xuele.trade.domain.service.lock.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.port.IActivityTrialPort;
import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;

/**
 * 活动试算规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class ActivityTrialRuleFilter implements ILogicHandler<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> {

    private final IActivityTrialPort activityTrialPort;

    public ActivityTrialRuleFilter(IActivityTrialPort activityTrialPort) {
        this.activityTrialPort = activityTrialPort;
    }

    @Override
    public TradeLockResultEntity apply(TradeLockCommandEntity requestParameter,
                                       TradeLockRuleFilterFactory.DynamicContext dynamicContext) {
        ActivityTrialEntity activityTrial = activityTrialPort.trial(requestParameter);
        if (activityTrial == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "活动试算结果为空");
        }
        if (!Boolean.TRUE.equals(activityTrial.getVisible()) || !Boolean.TRUE.equals(activityTrial.getEnable())) {
            throw new AppException(ResponseCode.TRADE_ACTIVITY_NOT_AVAILABLE);
        }
        dynamicContext.setActivityTrial(activityTrial);
        return null;
    }
}
