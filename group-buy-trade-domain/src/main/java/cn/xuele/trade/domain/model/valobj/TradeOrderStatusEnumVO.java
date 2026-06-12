package cn.xuele.trade.domain.model.valobj;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 个人订单状态 enum
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:00
 */
@Getter
@RequiredArgsConstructor
public enum TradeOrderStatusEnumVO {
    CREATE(0, "待支付"),

    COMPLETE(1, "已支付"),

    CLOSE(2, "已关闭");

    private final int status;
    private final String info;

}
