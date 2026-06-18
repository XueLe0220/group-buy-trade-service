package cn.xuele.trade.domain.service;

import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;

/**
 * 交易锁单领域服务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public interface ITradeLockOrderService {

    /**
     * 锁定交易订单。
     *
     * @param command 锁单命令
     * @return 锁单后的待支付订单
     */
    TradeOrderEntity lockTradeOrder(TradeLockCommandEntity command) throws Exception;

}
