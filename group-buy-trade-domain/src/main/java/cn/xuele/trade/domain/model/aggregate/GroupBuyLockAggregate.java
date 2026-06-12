package cn.xuele.trade.domain.model.aggregate;


import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.valobj.LockTypeEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁单聚合。
 * 表示一次锁单事务中需要保持一致的队伍变更和个人订单创建结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 16:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyLockAggregate {

    /** 锁单类型：开新团或加入已有团 */
    private LockTypeEnumVO lockType;

    /** 本次锁单涉及的拼团队伍；开团时为新队伍，参团时为待更新队伍 */
    private GroupBuyTeamEntity team;

    /** 本次锁单要创建的用户待支付订单 */
    private TradeOrderEntity order;
}