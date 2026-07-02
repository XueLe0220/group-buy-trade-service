package cn.xuele.trade.domain.model.entity;

import cn.xuele.trade.domain.model.valobj.TradeEventTypeEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交易事实事件。
 * <p>
 * 表达 trade-service 已经发生的业务事实，不表达通知执行任务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEventEntity {

    private String eventId;

    private TradeEventTypeEnumVO eventType;

    private String aggregateType;

    private String aggregateId;

    private String bizId;

    private Integer eventVersion;

    private String payloadJson;

    private String topic;

    private Integer status;

    private Integer retryCount;

    private LocalDateTime occurredAt;
}
