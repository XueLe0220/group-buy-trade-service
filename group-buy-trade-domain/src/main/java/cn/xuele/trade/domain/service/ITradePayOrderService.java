package cn.xuele.trade.domain.service;

import cn.xuele.trade.domain.model.entity.TradePayOrderResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradePayCommandEntity;

/**
 * 交易发起支付领域服务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public interface ITradePayOrderService {

    /**
     * 发起支付准备。
     *
     * @param command 发起支付命令
     * @return 发起支付准备结果
     */
    TradePayOrderResultEntity prepareTradePayOrder(TradePayCommandEntity command);
}
