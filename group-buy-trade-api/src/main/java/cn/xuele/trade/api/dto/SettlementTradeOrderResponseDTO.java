package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 交易结算响应。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementTradeOrderResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private String userId;

    /** 内部订单ID。 */
    private String orderId;

    /** 拼团队伍ID。 */
    private String teamId;

    /** 活动ID快照。 */
    private Long activityId;

    /** 外部交易单号。 */
    private String outTradeNo;

    /** 交易订单状态。 */
    private Integer tradeOrderStatus;

    /** 拼团队伍状态。 */
    private Integer teamStatus;
}
