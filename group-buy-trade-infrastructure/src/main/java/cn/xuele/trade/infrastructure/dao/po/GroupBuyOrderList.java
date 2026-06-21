package cn.xuele.trade.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户交易订单 PO。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyOrderList {

    private Long id;
    private String userId;
    private String teamId;
    private String orderId;
    private Long activityId;
    private String activityName;
    private String goodsId;
    private String goodsName;
    private String source;
    private String channel;
    private BigDecimal originalPrice;
    private BigDecimal deductionPrice;
    private BigDecimal payableAmount;
    private Integer status;
    private String outTradeNo;
    private String bizId;
    private String paymentRequestNo;
    private LocalDateTime paymentRequestTime;
    private String payNo;
    private BigDecimal paidAmount;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
