package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 交易退单响应。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundTradeOrderResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private String userId;

    /** 内部订单ID。 */
    private String orderId;

    /** 拼团队伍ID。 */
    private String teamId;

    /** 原外部交易单号。 */
    private String outTradeNo;

    /** 退款状态。 */
    private String refundStatus;

    /** 退款说明。 */
    private String refundInfo;
}
