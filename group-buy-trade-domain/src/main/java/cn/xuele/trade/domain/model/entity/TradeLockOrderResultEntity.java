package cn.xuele.trade.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁单落库结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeLockOrderResultEntity {

    /** 锁单返回的交易订单。 */
    private TradeOrderEntity order;

    /** 本次请求是否真实创建了新订单。 */
    private boolean created;

    /** 本次请求是否命中了并发幂等订单。 */
    private boolean idempotentHit;
}
