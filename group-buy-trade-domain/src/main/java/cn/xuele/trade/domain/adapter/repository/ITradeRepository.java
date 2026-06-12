package cn.xuele.trade.domain.adapter.repository;

import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
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
     * 锁单落库。
     * 接收领域服务已经构建好的锁单聚合，执行队伍和个人订单的一致性写入。
     * 新团：创建队伍 + 创建个人订单。
     * 参团：更新队伍 lockCount + 创建个人订单。
     */
    TradeOrderEntity lockOrder(GroupBuyLockAggregate aggregate);

}
