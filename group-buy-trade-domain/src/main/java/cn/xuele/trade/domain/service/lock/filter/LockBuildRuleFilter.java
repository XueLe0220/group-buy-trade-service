package cn.xuele.trade.domain.service.lock.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.LockTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;

import java.time.LocalDateTime;

import static cn.xuele.common.types.common.StringUtils.isBlank;

/**
 * 锁单聚合构建规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class LockBuildRuleFilter implements ILogicHandler<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> {

    @Override
    public TradeLockResultEntity apply(TradeLockCommandEntity requestParameter,
                                       TradeLockRuleFilterFactory.DynamicContext dynamicContext) {
        ActivityTrialEntity activityTrial = dynamicContext.getActivityTrial();
        LocalDateTime now = LocalDateTime.now();
        boolean newTeam = isBlank(requestParameter.getTeamId());

        GroupBuyTeamEntity team = newTeam ? buildNewTeam(requestParameter, activityTrial, now) : dynamicContext.getTeam();
        TradeOrderEntity order = buildTradeOrder(requestParameter, activityTrial, team, dynamicContext.getUserOrderCount(), now);

        GroupBuyLockAggregate aggregate = GroupBuyLockAggregate.builder()
                .lockType(newTeam ? LockTypeEnumVO.NEW_TEAM : LockTypeEnumVO.JOIN_TEAM)
                .team(team)
                .order(order)
                .takeLimitCount(activityTrial.getTakeLimitCount())
                .build();

        dynamicContext.setLockAggregate(aggregate);
        return TradeLockResultEntity.builder()
                .lockAggregate(aggregate)
                .build();
    }

    private GroupBuyTeamEntity buildNewTeam(TradeLockCommandEntity command, ActivityTrialEntity activityTrial, LocalDateTime now) {
        return GroupBuyTeamEntity.builder()
                .activityId(activityTrial.getActivityId())
                .activityName(activityTrial.getActivityName())
                .targetCount(activityTrial.getTargetCount())
                .lockCount(1)
                .completeCount(0)
                .validStartTime(now)
                .validEndTime(now.plusMinutes(activityTrial.getValidTime()))
                .status(GroupBuyTeamStatusEnumVO.PROGRESS)
                .notifyConfig(command.getNotifyConfig())
                .createTime(now)
                .updateTime(now)
                .build();
    }

    private TradeOrderEntity buildTradeOrder(TradeLockCommandEntity command,
                                             ActivityTrialEntity activityTrial,
                                             GroupBuyTeamEntity team,
                                             Integer userOrderCount,
                                             LocalDateTime now) {
        String bizId = activityTrial.getActivityId() + "_" + command.getUserId() + "_" + ((userOrderCount == null ? 0 : userOrderCount) + 1);
        return TradeOrderEntity.builder()
                .userId(command.getUserId())
                .outTradeNo(command.getOutTradeNo())
                .bizId(bizId)
                .teamId(team.getTeamId())
                .source(command.getSource())
                .channel(command.getChannel())
                .activityId(activityTrial.getActivityId())
                .activityName(activityTrial.getActivityName())
                .goodsId(activityTrial.getGoodsId())
                .goodsName(activityTrial.getGoodsName())
                .originalPrice(activityTrial.getOriginalPrice())
                .deductionPrice(activityTrial.getDeductionPrice())
                .payableAmount(activityTrial.getPayableAmount())
                .status(TradeOrderStatusEnumVO.CREATE)
                .createTime(now)
                .updateTime(now)
                .build();
    }
}
