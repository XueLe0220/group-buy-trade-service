package cn.xuele.trade.trigger.rpc;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.common.types.response.Response;
import cn.xuele.trade.api.ITradeOrderService;
import cn.xuele.trade.api.dto.LockTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.LockTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.PrepareTradePayOrderRequestDTO;
import cn.xuele.trade.api.dto.PrepareTradePayOrderResponseDTO;
import cn.xuele.trade.api.dto.RefundTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.RefundTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.SettlementTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.SettlementTradeOrderResponseDTO;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradePayOrderResultEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.service.ITradeLockOrderService;
import cn.xuele.trade.domain.service.ITradePayOrderService;
import cn.xuele.trade.domain.service.ITradeRefundOrderService;
import cn.xuele.trade.domain.service.ITradeSettlementOrderService;
import org.apache.dubbo.config.annotation.DubboService;

import static cn.xuele.trade.trigger.Support.toCommand;
import static cn.xuele.trade.trigger.Support.toResponse;
import static cn.xuele.trade.trigger.Support.validateLockRequest;
import static cn.xuele.trade.trigger.Support.validatePreparePayRequest;
import static cn.xuele.trade.trigger.Support.validateRefundRequest;
import static cn.xuele.trade.trigger.Support.validateSettlementRequest;

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
    private final ITradePayOrderService tradePayOrderService;
    private final ITradeSettlementOrderService tradeSettlementOrderService;
    private final ITradeRefundOrderService tradeRefundOrderService;

    public TradeOrderProvider(ITradeLockOrderService tradeLockOrderService,
                              ITradePayOrderService tradePayOrderService,
                              ITradeSettlementOrderService tradeSettlementOrderService,
                              ITradeRefundOrderService tradeRefundOrderService) {
        this.tradeLockOrderService = tradeLockOrderService;
        this.tradePayOrderService = tradePayOrderService;
        this.tradeSettlementOrderService = tradeSettlementOrderService;
        this.tradeRefundOrderService = tradeRefundOrderService;
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
        try {
            validateSettlementRequest(request);
            TradeSettlementResultEntity result = tradeSettlementOrderService.settlementTradeOrder(toCommand(request));
            return Response.success(toResponse(result));
        } catch (AppException e) {
            return Response.failure(e.getCode(), e.getInfo());
        } catch (Exception e) {
            return Response.failure(ResponseCode.UN_ERROR);
        }
    }

    @Override
    public Response<PrepareTradePayOrderResponseDTO> prepareTradePayOrder(PrepareTradePayOrderRequestDTO request) {
        try {
            validatePreparePayRequest(request);
            TradePayOrderResultEntity result = tradePayOrderService.prepareTradePayOrder(toCommand(request));
            return Response.success(toResponse(result));
        } catch (AppException e) {
            return Response.failure(e.getCode(), e.getInfo());
        } catch (Exception e) {
            return Response.failure(ResponseCode.UN_ERROR);
        }
    }

    @Override
    public Response<RefundTradeOrderResponseDTO> refundTradeOrder(RefundTradeOrderRequestDTO request) {
        try {
            validateRefundRequest(request);
            TradeRefundResultEntity result = tradeRefundOrderService.refundTradeOrder(toCommand(request));
            return Response.success(toResponse(result));
        } catch (AppException e) {
            return Response.failure(e.getCode(), e.getInfo());
        } catch (Exception e) {
            return Response.failure(ResponseCode.UN_ERROR);
        }
    }
}
