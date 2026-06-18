package cn.xuele.trade.domain.adapter.repository;

import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;

/**
 * 交易仓储端口。
 * 由 domain 定义交易数据读写能力，具体 MySQL、Redis、事务和并发控制由 infrastructure 实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 16:04
 */
public interface ITradeRepository {

    /**
     * 根据用户和外部交易单号查询已有交易单。
     * 用于锁单入口幂等判断，存在则直接返回原锁单结果。
     */
    TradeOrderEntity queryOrderByUserIdAndOutTradeNo(String userId, String outTradeNo);

    /**
     * 查询用户在指定活动下已占用限购次数的订单数。
     * 统计待支付和已支付订单，已关闭订单不占用限购名额。
     */
    Integer queryUserOrderCount(Long activityId, String userId);

    /**
     * 查询拼团队伍。
     */
    GroupBuyTeamEntity queryTeamByTeamId(String teamId);

    /**
     * 锁单落库。
     * 接收领域服务已经构建好的锁单聚合，执行队伍和个人订单的一致性写入。
     * 新团：创建队伍 + 创建个人订单。
     * 参团：更新队伍 lockCount + 创建个人订单。
     */
    TradeOrderEntity lockOrder(GroupBuyLockAggregate aggregate);

}
