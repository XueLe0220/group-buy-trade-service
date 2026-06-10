package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 交易退单请求。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundTradeOrderRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private String userId;

    /** 来源。 */
    private String source;

    /** 渠道。 */
    private String channel;

    /** 原外部交易单号。 */
    private String outTradeNo;

    /** 退款请求号。 */
    private String refundRequestNo;

    /** 退款原因。 */
    private String refundReason;
}
