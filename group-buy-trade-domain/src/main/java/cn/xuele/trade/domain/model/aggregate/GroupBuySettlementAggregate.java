package cn.xuele.trade.domain.model.aggregate;

import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拼团交易结算聚合。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 16:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuySettlementAggregate {

    /** 支付回调命令。 */
    private TradeSettlementCommandEntity command;

    /** 待结算个人订单快照。 */
    private TradeOrderEntity order;

    /** 订单所属拼团队伍快照。 */
    private GroupBuyTeamEntity team;

}
