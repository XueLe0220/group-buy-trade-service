package cn.xuele.trade.domain.adapter.repository;

import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.aggregate.GroupBuyRefundAggregate;
import cn.xuele.trade.domain.model.aggregate.GroupBuySettlementAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeLockOrderResultEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradePayOrderResultEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;

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
     * 根据支付流水号查询交易单。
     * 用于防止同一支付流水绑定多个交易订单。
     */
    TradeOrderEntity queryOrderByPayNo(String payNo);

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
    TradeLockOrderResultEntity lockOrder(GroupBuyLockAggregate aggregate);

    /**
     * 发起支付准备。
     * 校验后的 CREATE 订单生成或复用支付请求号，订单状态仍保持待支付。
     */
    TradePayOrderResultEntity preparePayOrder(TradeOrderEntity order, GroupBuyTeamEntity team);

    /**
     * 支付结算落库。
     * 在本地事务内完成个人订单支付信息写入、订单状态推进、队伍完成数推进和撞线成团。
     * 首次撞线成团时，需要在同一事务边界内记录交易事实事件。
     */
    TradeSettlementResultEntity settlementOrder(GroupBuySettlementAggregate aggregate);

    /**
     * 退单落库。
     * 在本地事务内完成个人订单关闭和队伍锁单数、完成数、状态回退。
     */
    TradeRefundResultEntity refundOrder(GroupBuyRefundAggregate aggregate);

}
