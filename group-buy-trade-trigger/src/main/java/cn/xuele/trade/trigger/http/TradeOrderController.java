package cn.xuele.trade.trigger.http;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.common.types.response.Response;
import cn.xuele.trade.api.dto.LockTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.LockTradeOrderResponseDTO;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.service.ITradeLockOrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.xuele.trade.trigger.Support.toCommand;
import static cn.xuele.trade.trigger.Support.toResponse;
import static cn.xuele.trade.trigger.Support.validateLockRequest;

/**
 * 交易订单本地验证 HTTP Controller。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@RestController
@RequestMapping("/api/trade")
public class TradeOrderController {

    private final ITradeLockOrderService tradeLockOrderService;

    public TradeOrderController(ITradeLockOrderService tradeLockOrderService) {
        this.tradeLockOrderService = tradeLockOrderService;
    }

    @PostMapping("/lock")
    public Response<LockTradeOrderResponseDTO> lockTradeOrder(@RequestBody LockTradeOrderRequestDTO request) {
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
}
