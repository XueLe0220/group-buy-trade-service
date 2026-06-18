package cn.xuele.trade.domain.model.entity;

import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁单规则链结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeLockResultEntity {

    /** 已存在订单，幂等返回时使用。 */
    private TradeOrderEntity existOrder;

    /** 新锁单聚合，需要进入仓储事务落库时使用。 */
    private GroupBuyLockAggregate lockAggregate;

    public boolean isIdempotentHit() {
        return existOrder != null;
    }
}
