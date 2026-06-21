package cn.xuele.trade.api;

import cn.xuele.common.types.response.Response;
import cn.xuele.trade.api.dto.LockTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.LockTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.PrepareTradePayOrderRequestDTO;
import cn.xuele.trade.api.dto.PrepareTradePayOrderResponseDTO;
import cn.xuele.trade.api.dto.RefundTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.RefundTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.SettlementTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.SettlementTradeOrderResponseDTO;

/**
 * 交易订单 RPC 契约。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
public interface ITradeOrderService {

    /**
     * 锁定交易订单。
     *
     * @param request 锁单请求
     * @return 锁单结果
     */
    Response<LockTradeOrderResponseDTO> lockTradeOrder(LockTradeOrderRequestDTO request);

    /**
     * 发起支付准备。
     *
     * @param request 发起支付准备请求
     * @return 发起支付准备结果
     */
    Response<PrepareTradePayOrderResponseDTO> prepareTradePayOrder(PrepareTradePayOrderRequestDTO request);

    /**
     * 结算交易订单。
     *
     * @param request 结算请求
     * @return 结算结果
     */
    Response<SettlementTradeOrderResponseDTO> settlementTradeOrder(SettlementTradeOrderRequestDTO request);

    /**
     * 退单交易订单。
     *
     * @param request 退单请求
     * @return 退单结果
     */
    Response<RefundTradeOrderResponseDTO> refundTradeOrder(RefundTradeOrderRequestDTO request);
}
