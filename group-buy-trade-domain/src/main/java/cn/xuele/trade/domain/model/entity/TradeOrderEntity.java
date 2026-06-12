package cn.xuele.trade.domain.model.entity;

import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易订单实体
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 14:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOrderEntity {

    private String userId;

    /** trade-service 内部订单号 */
    private String orderId;

    /** 外部交易单号；贯穿锁单、支付结算、退款的订单级幂等键 */
    private String outTradeNo;

    /** 业务动作幂等号；用于防止同一用户同一业务动作重复创建交易单 */
    private String bizId;

    /** 所属拼团队伍 */
    private String teamId;

    /** 渠道来源 */
    private String source;
    private String channel;

    /** 活动快照 */
    private Long activityId;
    private String activityName;

    /** 商品快照 */
    private String goodsId;
    private String goodsName;

    /** 价格快照 */
    private BigDecimal originalPrice;
    private BigDecimal deductionPrice;
    private BigDecimal payPrice;

    /** 支付信息 */
    private LocalDateTime payTime;
    /** 支付系统流水号；用于防止支付回调串单或重复流水异常 */
    private String payNo;
    /** 实际支付金额；支付回调金额必须与订单应付金额校验 */
    private BigDecimal payAmount;

    /** 订单状态 */
    private TradeOrderStatusEnumVO status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}