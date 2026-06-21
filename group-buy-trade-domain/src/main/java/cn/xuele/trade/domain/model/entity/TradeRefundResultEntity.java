package cn.xuele.trade.domain.model.entity;

import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退单规则链和领域服务返回结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRefundResultEntity {

    /** 已关闭或已退单的个人订单。 */
    private TradeOrderEntity order;

    /** 订单所属队伍。 */
    private GroupBuyTeamEntity team;

    /** 本次退单采用的状态组合类型。 */
    private RefundTypeEnumVO refundType;

    /** 本次是否命中重复退单幂等。 */
    private boolean idempotentHit;

}
