package cn.xuele.trade.domain.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易结果通知配置
 * <p>
 * 用于描述成团、退款等交易结果如何通知下游。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyConfigVO {

    private NotifyTypeEnumVO notifyType;

    /** MQ topic / exchange / routing key，后续可细化 */
    private String notifyMQ;

    /** HTTP 回调地址 */
    private String notifyUrl;
}
