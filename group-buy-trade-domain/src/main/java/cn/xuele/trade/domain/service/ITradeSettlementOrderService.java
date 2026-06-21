package cn.xuele.trade.domain.service;

import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;

/**
 * 交易结算领域服务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public interface ITradeSettlementOrderService {

    /**
     * 支付成功后结算交易订单。
     *
     * @param command 结算命令
     * @return 结算结果
     * @throws Exception 结算异常
     */
    TradeSettlementResultEntity settlementTradeOrder(TradeSettlementCommandEntity command) throws Exception;

}
