package cn.xuele.trade.domain.service.lock.factory;

import cn.xuele.common.design.framework.link.LinkArmory;
import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.lock.filter.ActivityTrialRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.LockBuildRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.LockIdempotentRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.TeamAvailableRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.TeamStockReserveRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.UserTakeLimitRuleFilter;
import lombok.Data;

/**
 * 交易锁单规则链工厂。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class TradeLockRuleFilterFactory {

    public BusinessLinkedList<TradeLockCommandEntity, DynamicContext, TradeLockResultEntity> tradeLockRuleFilter(
            LockIdempotentRuleFilter lockIdempotentRuleFilter,
            ActivityTrialRuleFilter activityTrialRuleFilter,
            UserTakeLimitRuleFilter userTakeLimitRuleFilter,
            TeamAvailableRuleFilter teamAvailableRuleFilter,
            TeamStockReserveRuleFilter teamStockReserveRuleFilter,
            LockBuildRuleFilter lockBuildRuleFilter) {

        LinkArmory<TradeLockCommandEntity, DynamicContext, TradeLockResultEntity> linkArmory =
                new LinkArmory<>("交易锁单规则链",
                        lockIdempotentRuleFilter,
                        activityTrialRuleFilter,
                        userTakeLimitRuleFilter,
                        teamAvailableRuleFilter,
                        teamStockReserveRuleFilter,
                        lockBuildRuleFilter);

        return linkArmory.getLogicLink();
    }

    @Data
    public static class DynamicContext {

        /** activity-service 返回的锁单权威快照。 */
        private ActivityTrialEntity activityTrial;

        /** 用户在当前活动下已占用的限购次数。 */
        private Integer userOrderCount;

        /** 参团时加载到的队伍。 */
        private GroupBuyTeamEntity team;

        /** 最终要落库的锁单聚合。 */
        private GroupBuyLockAggregate lockAggregate;

        /** 本次是否完成 Redis 队伍名额预占。 */
        private boolean teamStockReserved;

        /** 本次 Redis 预占对应的恢复幂等号。 */
        private String teamStockRecoveryBizId;

    }
}
