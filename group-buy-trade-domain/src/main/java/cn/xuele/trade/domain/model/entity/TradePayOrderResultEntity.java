package cn.xuele.trade.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 发起支付准备结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradePayOrderResultEntity {

    /** 可支付交易订单。 */
    private TradeOrderEntity order;

    /** 订单所属队伍。 */
    private GroupBuyTeamEntity team;

    /** 本次支付请求号。 */
    private String paymentRequestNo;

    /** 支付有效期。 */
    private LocalDateTime payExpireTime;
}
