package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易锁单响应。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockTradeOrderResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 内部订单ID。 */
    private String orderId;

    /** 拼团队伍ID。 */
    private String teamId;

    /** 原价快照。 */
    private BigDecimal originalPrice;

    /** 优惠金额快照。 */
    private BigDecimal deductionPrice;

    /** 支付金额快照。 */
    private BigDecimal payPrice;

    /** 交易订单状态。 */
    private Integer tradeOrderStatus;
}
