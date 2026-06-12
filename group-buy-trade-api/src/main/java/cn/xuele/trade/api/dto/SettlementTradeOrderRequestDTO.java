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
 * 支付成功结算请求。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementTradeOrderRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private String userId;

    /** 来源。 */
    private String source;

    /** 渠道。 */
    private String channel;

    /** 外部交易单号。 */
    private String outTradeNo;

    /** 支付系统流水号。 */
    private String payNo;

    /** 实付金额。 */
    private BigDecimal payAmount;

    /** 支付完成时间。 */
    private LocalDateTime payTime;
}
