package cn.xuele.trade.domain.model.valobj;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 退单状态组合类型。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
@Getter
@RequiredArgsConstructor
public enum RefundTypeEnumVO {

    UNPAID_UNLOCK("unpaid_unlock", "未支付退单，释放锁单名额"),

    PAID_UNFORMED("paid_unformed", "已支付未成团退款，回退锁单数和完成数"),

    PAID_FORMED("paid_formed", "已支付已成团退款，回退完成数并更新队伍状态");

    private final String code;
    private final String info;

    public static RefundTypeEnumVO getRefundType(GroupBuyTeamStatusEnumVO teamStatus,
                                                 TradeOrderStatusEnumVO orderStatus) {
        return Arrays.stream(values())
                .filter(item -> item.matches(teamStatus, orderStatus))
                .findFirst()
                .orElseThrow(() -> new AppException(ResponseCode.UN_ERROR.getCode(),
                        "不支持的退单状态组合: teamStatus=" + teamStatus + ", orderStatus=" + orderStatus));
    }

    public boolean matches(GroupBuyTeamStatusEnumVO teamStatus, TradeOrderStatusEnumVO orderStatus) {
        return switch (this) {
            case UNPAID_UNLOCK -> GroupBuyTeamStatusEnumVO.PROGRESS.equals(teamStatus)
                    && TradeOrderStatusEnumVO.CREATE.equals(orderStatus);
            case PAID_UNFORMED -> GroupBuyTeamStatusEnumVO.PROGRESS.equals(teamStatus)
                    && TradeOrderStatusEnumVO.COMPLETE.equals(orderStatus);
            case PAID_FORMED -> (GroupBuyTeamStatusEnumVO.COMPLETE.equals(teamStatus)
                    || GroupBuyTeamStatusEnumVO.PARTIAL_REFUND.equals(teamStatus))
                    && TradeOrderStatusEnumVO.COMPLETE.equals(orderStatus);
        };
    }
}
