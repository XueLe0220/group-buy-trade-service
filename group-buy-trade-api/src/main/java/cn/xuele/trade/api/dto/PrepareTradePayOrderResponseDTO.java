package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发起支付准备响应。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepareTradePayOrderResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private String userId;

    /** 内部订单ID。 */
    private String orderId;

    /** 拼团队伍ID。 */
    private String teamId;

    /** 外部交易单号。 */
    private String outTradeNo;

    /** 本次支付请求号。 */
    private String paymentRequestNo;

    /** 应付金额。 */
    private BigDecimal payableAmount;

    /** 支付有效期。 */
    private LocalDateTime payExpireTime;

    /** 交易订单状态。 */
    private Integer tradeOrderStatus;

    /** 拼团队伍状态。 */
    private Integer teamStatus;
}
