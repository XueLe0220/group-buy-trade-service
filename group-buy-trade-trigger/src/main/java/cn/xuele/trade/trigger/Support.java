package cn.xuele.trade.trigger;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.api.dto.LockTradeOrderRequestDTO;
import cn.xuele.trade.api.dto.LockTradeOrderResponseDTO;
import cn.xuele.trade.api.dto.NotifyConfigDTO;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.model.valobj.NotifyConfigVO;
import cn.xuele.trade.domain.model.valobj.NotifyTypeEnumVO;

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
                .payPrice(order.getPayPrice())
                .tradeOrderStatus(order.getStatus() == null ? null : order.getStatus().getStatus())
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
