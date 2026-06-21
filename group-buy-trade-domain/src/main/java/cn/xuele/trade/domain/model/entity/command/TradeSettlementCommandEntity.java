package cn.xuele.trade.domain.model.entity.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付成功结算命令
 * <p>
 * 表示外部支付系统已完成扣款后，通知 trade-service 推进订单和拼团队伍状态。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSettlementCommandEntity {

    /** 用户ID；用于防串单校验 */
    private String userId;

    /** 来源系统 */
    private String source;

    /** 渠道 */
    private String channel;

    /** 外部交易单号；用于定位待支付订单 */
    private String outTradeNo;

    /** 支付系统流水号；支付成功后由外部支付系统返回，用于支付回调幂等和防串单校验 */
    private String payNo;

    /** 实际支付金额；支付成功后由外部支付系统返回，必须与订单应付金额校验 */
    private BigDecimal paidAmount;

    /** 支付成功时间；支付成功后由外部支付系统返回 */
    private LocalDateTime payTime;
}
