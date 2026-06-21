package cn.xuele.trade.domain.model.entity.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发起支付命令。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradePayCommandEntity {

    /** 用户ID。 */
    private String userId;

    /** 来源系统。 */
    private String source;

    /** 渠道。 */
    private String channel;

    /** 外部交易单号。 */
    private String outTradeNo;
}
