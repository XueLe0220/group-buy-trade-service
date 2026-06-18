package cn.xuele.trade.trigger.rpc;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.common.types.response.Response;
import cn.xuele.trade.api.ITradeOrderService;
import cn.xuele.trade.api.dto.LockTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.LockTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.RefundTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.RefundTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.SettlementTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.SettlementTradeOrderResponseDTO;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.service.ITradeLockOrderService;
import org.apache.dubbo.config.annotation.DubboService;

import static cn.xuele.trade.trigger.Support.toCommand;
import static cn.xuele.trade.trigger.Support.toResponse;
import static cn.xuele.trade.trigger.Support.validateLockRequest;

/**
 * 交易订单 Dubbo Provider。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@DubboService
public class TradeOrderProvider implements ITradeOrderService {

    private final ITradeLockOrderService tradeLockOrderService;

    public TradeOrderProvider(ITradeLockOrderService tradeLockOrderService) {
        this.tradeLockOrderService = tradeLockOrderService;
    }

    @Override
    public Response<LockTradeOrderResponseDTO> lockTradeOrder(LockTradeOrderRequestDTO request) {
        try {
            validateLockRequest(request);
            TradeOrderEntity order = tradeLockOrderService.lockTradeOrder(toCommand(request));
            return Response.success(toResponse(order));
        } catch (AppException e) {
            return Response.failure(e.getCode(), e.getInfo());
        } catch (Exception e) {
            return Response.failure(ResponseCode.UN_ERROR);
        }
    }

    @Override
    public Response<SettlementTradeOrderResponseDTO> settlementTradeOrder(SettlementTradeOrderRequestDTO request) {
        return Response.failure(ResponseCode.UN_ERROR.getCode(), "结算链路尚未在阶段5锁单闭环中实现");
    }

    @Override
    public Response<RefundTradeOrderResponseDTO> refundTradeOrder(RefundTradeOrderRequestDTO request) {
        return Response.failure(ResponseCode.UN_ERROR.getCode(), "退款链路尚未在阶段5锁单闭环中实现");
    }
}
