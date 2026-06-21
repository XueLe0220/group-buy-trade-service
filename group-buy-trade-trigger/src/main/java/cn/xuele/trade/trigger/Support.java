package cn.xuele.trade.trigger;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.api.dto.LockTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.LockTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.NotifyConfigDTO;
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
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.model.entity.command.TradePayCommandEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.model.valobj.NotifyConfigVO;
import cn.xuele.trade.domain.model.valobj.NotifyTypeEnumVO;

import java.math.BigDecimal;

import static cn.xuele.common.types.common.StringUtils.isBlank;

/**
 * trigger 层 DTO 转换支持。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public final class Support {

    private Support() {
    }

    public static TradeLockCommandEntity toCommand(LockTradeOrderRequestDTO request) {
        NotifyConfigDTO notifyConfig = request.getNotifyConfig();
        return TradeLockCommandEntity.builder()
                .userId(request.getUserId())
                .source(request.getSource())
                .channel(request.getChannel())
                .activityId(request.getActivityId())
                .goodsId(request.getGoodsId())
                .teamId(request.getTeamId())
                .outTradeNo(request.getOutTradeNo())
                .notifyConfig(notifyConfig == null ? null : NotifyConfigVO.builder()
                        .notifyType(parseNotifyType(notifyConfig.getNotifyType()))
                        .notifyMQ(notifyConfig.getNotifyMQ())
                        .notifyUrl(notifyConfig.getNotifyUrl())
                        .build())
                .build();
    }

    public static LockTradeOrderResponseDTO toResponse(TradeOrderEntity order) {
        return LockTradeOrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .teamId(order.getTeamId())
                .originalPrice(order.getOriginalPrice())
                .deductionPrice(order.getDeductionPrice())
                .payableAmount(order.getPayableAmount())
                .tradeOrderStatus(order.getStatus() == null ? null : order.getStatus().getStatus())
                .build();
    }

    public static TradeSettlementCommandEntity toCommand(SettlementTradeOrderRequestDTO request) {
        return TradeSettlementCommandEntity.builder()
                .userId(request.getUserId())
                .source(request.getSource())
                .channel(request.getChannel())
                .outTradeNo(request.getOutTradeNo())
                .payNo(request.getPayNo())
                .paidAmount(request.getPaidAmount())
                .payTime(request.getPayTime())
                .build();
    }

    public static TradePayCommandEntity toCommand(PrepareTradePayOrderRequestDTO request) {
        return TradePayCommandEntity.builder()
                .userId(request.getUserId())
                .source(request.getSource())
                .channel(request.getChannel())
                .outTradeNo(request.getOutTradeNo())
                .build();
    }

    public static TradeRefundCommandEntity toCommand(RefundTradeOrderRequestDTO request) {
        return TradeRefundCommandEntity.builder()
                .userId(request.getUserId())
                .source(request.getSource())
                .channel(request.getChannel())
                .outTradeNo(request.getOutTradeNo())
                .refundNo(request.getRefundRequestNo())
                .refundReason(request.getRefundReason())
                .build();
    }

    public static PrepareTradePayOrderResponseDTO toResponse(TradePayOrderResultEntity result) {
        TradeOrderEntity order = result.getOrder();
        return PrepareTradePayOrderResponseDTO.builder()
                .userId(order.getUserId())
                .orderId(order.getOrderId())
                .teamId(order.getTeamId())
                .outTradeNo(order.getOutTradeNo())
                .paymentRequestNo(result.getPaymentRequestNo())
                .payableAmount(order.getPayableAmount())
                .payExpireTime(result.getPayExpireTime())
                .tradeOrderStatus(order.getStatus() == null ? null : order.getStatus().getStatus())
                .teamStatus(result.getTeam() == null || result.getTeam().getStatus() == null ? null : result.getTeam().getStatus().getStatus())
                .build();
    }

    public static SettlementTradeOrderResponseDTO toResponse(TradeSettlementResultEntity result) {
        TradeOrderEntity order = result.getOrder();
        return SettlementTradeOrderResponseDTO.builder()
                .userId(order.getUserId())
                .orderId(order.getOrderId())
                .teamId(order.getTeamId())
                .activityId(order.getActivityId())
                .outTradeNo(order.getOutTradeNo())
                .tradeOrderStatus(order.getStatus() == null ? null : order.getStatus().getStatus())
                .teamStatus(result.getTeam() == null || result.getTeam().getStatus() == null ? null : result.getTeam().getStatus().getStatus())
                .build();
    }

    public static RefundTradeOrderResponseDTO toResponse(TradeRefundResultEntity result) {
        TradeOrderEntity order = result.getOrder();
        return RefundTradeOrderResponseDTO.builder()
                .userId(order.getUserId())
                .orderId(order.getOrderId())
                .teamId(order.getTeamId())
                .outTradeNo(order.getOutTradeNo())
                .refundStatus(result.isIdempotentHit() ? "REPEAT" : "SUCCESS")
                .refundInfo(result.getRefundType() == null ? "重复退单，已幂等返回" : result.getRefundType().getInfo())
                .build();
    }

    public static void validateLockRequest(LockTradeOrderRequestDTO request) {
        if (request == null
                || isBlank(request.getUserId())
                || isBlank(request.getGoodsId())
                || isBlank(request.getSource())
                || isBlank(request.getChannel())
                || isBlank(request.getOutTradeNo())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }

        NotifyConfigDTO notifyConfig = request.getNotifyConfig();
        if (notifyConfig != null
                && "HTTP".equalsIgnoreCase(notifyConfig.getNotifyType())
                && isBlank(notifyConfig.getNotifyUrl())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }
    }

    public static void validateSettlementRequest(SettlementTradeOrderRequestDTO request) {
        if (request == null
                || isBlank(request.getUserId())
                || isBlank(request.getSource())
                || isBlank(request.getChannel())
                || isBlank(request.getOutTradeNo())
                || isBlank(request.getPayNo())
                || request.getPayTime() == null
                || request.getPaidAmount() == null
                || request.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }
    }

    public static void validatePreparePayRequest(PrepareTradePayOrderRequestDTO request) {
        if (request == null
                || isBlank(request.getUserId())
                || isBlank(request.getSource())
                || isBlank(request.getChannel())
                || isBlank(request.getOutTradeNo())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }
    }

    public static void validateRefundRequest(RefundTradeOrderRequestDTO request) {
        if (request == null
                || isBlank(request.getUserId())
                || isBlank(request.getSource())
                || isBlank(request.getChannel())
                || isBlank(request.getOutTradeNo())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }
    }

    private static NotifyTypeEnumVO parseNotifyType(String notifyType) {
        if (isBlank(notifyType)) {
            return null;
        }
        try {
            return NotifyTypeEnumVO.valueOf(notifyType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }
    }
}
