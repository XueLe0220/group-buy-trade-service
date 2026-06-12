package cn.xuele.trade.domain.adapter.port;

import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;

/**
 * 活动试算防腐端口。
 * trade-domain 通过该端口获取锁单前的活动权威快照，不直接依赖 activity-service 的 RPC DTO 或 Dubbo 实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 16:01
 */
public interface IActivityTrialPort {

    /**
     * 锁单前活动试算。
     * 入参中的 activityId 只是用户期望参与的候选活动，
     * 最终可落单的活动、商品、价格、成团人数和有效期，以返回的 ActivityTrialEntity 为准。
     */
    ActivityTrialEntity trial(TradeLockCommandEntity command);
}
