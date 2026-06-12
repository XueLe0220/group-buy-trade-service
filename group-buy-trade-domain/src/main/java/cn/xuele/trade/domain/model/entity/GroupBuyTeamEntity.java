package cn.xuele.trade.domain.model.entity;

import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.NotifyConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 拼团队伍实体
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 14:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyTeamEntity {

    private String teamId;

    /** 活动快照 */
    private Long activityId;
    private String activityName;

    /** 队伍目标成团人数 */
    private Integer targetCount;

    /** 已锁单人数；锁单成功即增加，占坑但不代表已支付 */
    private Integer lockCount;

    /** 已支付完成人数；支付结算成功后增加，用于判断是否成团 */
    private Integer completeCount;

    /** 队伍有效期开始时间 */
    private LocalDateTime validStartTime;

    /** 队伍有效期结束时间；超过后未成团应进入失败或关闭流程 */
    private LocalDateTime validEndTime;

    /** 队伍状态 */
    private GroupBuyTeamStatusEnumVO status;

    /** 成团/退款通知配置 */
    private NotifyConfigVO notifyConfig;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
