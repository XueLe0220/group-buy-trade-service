package cn.xuele.trade.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 交易通知配置。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 通知方式，如 MQ、HTTP。 */
    private String notifyType;

    /** MQ 路由标识。 */
    private String notifyMQ;

    /** HTTP 回调地址。 */
    private String notifyUrl;
}
