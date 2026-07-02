package cn.xuele.trade.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交易事件 outbox PO。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEventOutbox {

    private Long id;

    private String eventId;

    private String eventType;

    private String aggregateType;

    private String aggregateId;

    private String bizId;

    private Integer eventVersion;

    private String payloadJson;

    private String topic;

    private Integer status;

    private Integer retryCount;

    private LocalDateTime nextRetryTime;

    private String lastError;

    private LocalDateTime occurredAt;

    private LocalDateTime sentTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
