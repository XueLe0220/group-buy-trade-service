package cn.xuele.trade.domain.model.valobj;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 退款触发类型
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:38
 */
@Getter
@RequiredArgsConstructor
public enum RefundTriggerTypeEnumVO {

    USER_APPLY("用户主动退款"),
    TIMEOUT_UNPAID("超时未支付关闭"),
    BIZ_CANCEL("业务取消")
    ;
    private final String info;

}
