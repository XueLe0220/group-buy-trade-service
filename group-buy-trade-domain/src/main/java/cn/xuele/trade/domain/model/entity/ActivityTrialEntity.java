package cn.xuele.trade.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动试算结果实体。
 * 表示 activity-service 在锁单前重新校验活动、商品、价格和用户参与资格后返回的权威快照。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTrialEntity {

    /** activity-service 最终确认的活动ID */
    private Long activityId;

    /** activity-service 最终确认的活动名称 */
    private String activityName;

    private String goodsId;
    private String goodsName;

    private BigDecimal originalPrice;
    private BigDecimal deductionPrice;
    private BigDecimal payPrice;

    /** 成团目标人数 */
    private Integer targetCount;

    /** 拼团有效时间，单位分钟 */
    private Integer validTime;

    /** 单人参与次数限制 */
    private Integer takeLimitCount;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /** 当前用户是否可见 */
    private Boolean visible;

    /** 当前用户是否允许参与 */
    private Boolean enable;
}
