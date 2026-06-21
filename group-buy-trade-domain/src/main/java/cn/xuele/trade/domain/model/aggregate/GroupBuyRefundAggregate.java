package cn.xuele.trade.domain.model.aggregate;

import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拼团交易退单聚合。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 16:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyRefundAggregate {

    /** 退单命令。 */
    private TradeRefundCommandEntity command;

    /** 待退单个人订单快照。 */
    private TradeOrderEntity order;

    /** 订单所属拼团队伍快照。 */
    private GroupBuyTeamEntity team;

    /** 退单状态组合类型。 */
    private RefundTypeEnumVO refundType;

    /** 退单后队伍应推进到的状态。 */
    private GroupBuyTeamStatusEnumVO targetTeamStatus;

}
