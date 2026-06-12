package cn.xuele.trade.domain.model.entity.command;

import cn.xuele.trade.domain.model.valobj.RefundTriggerTypeEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退单请求实体
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRefundCommandEntity {

    /** 用户ID；用于定位和防串单校验 */
    private String userId;

    /** 来源系统 */
    private String source;

    /** 渠道 */
    private String channel;

    /** 外部交易单号；用于定位要退款/关闭的订单 */
    private String outTradeNo;

    /** 退款请求号；退款动作级幂等键 */
    private String refundNo;

    /** 退款原因 */
    private String refundReason;

    /** 退款触发类型 */
    private RefundTriggerTypeEnumVO refundTriggerType;
}
