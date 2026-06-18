package cn.xuele.trade.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼团队伍订单 PO。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyOrder {

    private Long id;
    private String teamId;
    private Long activityId;
    private String activityName;
    private String source;
    private String channel;
    private BigDecimal originalPrice;
    private BigDecimal deductionPrice;
    private BigDecimal payPrice;
    private Integer targetCount;
    private Integer lockCount;
    private Integer completeCount;
    private Integer status;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private String notifyType;
    private String notifyMQ;
    private String notifyUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
