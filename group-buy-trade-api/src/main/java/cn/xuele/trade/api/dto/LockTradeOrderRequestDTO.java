package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 交易锁单请求。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockTradeOrderRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private String userId;

    /** 拼团队伍ID，为空表示开新团。 */
    private String teamId;

    /** 商品ID。 */
    private String goodsId;

    /** 来源。 */
    private String source;

    /** 渠道。 */
    private String channel;

    /** 外部交易单号。 */
    private String outTradeNo;

    /** 成团通知配置。 */
    private NotifyConfigDTO notifyConfig;
}
