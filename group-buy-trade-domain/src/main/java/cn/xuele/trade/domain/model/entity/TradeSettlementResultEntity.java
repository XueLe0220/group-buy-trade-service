package cn.xuele.trade.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结算规则链和领域服务返回结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSettlementResultEntity {

    /** 已完成结算的订单；重复支付回调幂等返回时使用。 */
    private TradeOrderEntity order;

    /** 订单所属队伍。 */
    private GroupBuyTeamEntity team;

    /** 本次是否为重复回调幂等命中。 */
    private boolean idempotentHit;

}
