package cn.xuele.trade.domain.model.entity.command;

import cn.xuele.trade.domain.model.valobj.NotifyConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁单请求实体
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeLockCommandEntity {

    /** 用户ID */
    private String userId;

    /** 来源系统，例如 app、mini_program、backend */
    private String source;

    /** 渠道，例如 Xiaomi、Huawei、WeChat */
    private String channel;

    /** 用户期望参与的活动ID；仅作为试算候选条件，最终活动快照以 activity-service 返回为准 */
    private Long activityId;

    /** 商品ID；用于向 activity-service 发起试算 */
    private String goodsId;

    /** 目标团ID；为空表示开新团，不为空表示参团 */
    private String teamId;

    /** 外部交易单号；订单级幂等键 */
    private String outTradeNo;

    /** 业务动作幂等号；用于防止重复锁单创建 */
    private String bizId;

    /** 通知配置；成团、退款等结果通知下游 */
    private NotifyConfigVO notifyConfig;
}