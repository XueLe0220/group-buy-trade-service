package cn.xuele.trade.infrastructure.event;

import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;

/**
 * 交易事件发布器。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
public interface ITradeEventPublisher {

    void publish(TradeEventOutbox event);
}
