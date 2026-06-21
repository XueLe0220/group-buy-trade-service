package cn.xuele.trade.domain.service;

import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;

/**
 * 交易退单领域服务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public interface ITradeRefundOrderService {

    /**
     * 执行交易退单。
     */
    TradeRefundResultEntity refundTradeOrder(TradeRefundCommandEntity command) throws Exception;

}
