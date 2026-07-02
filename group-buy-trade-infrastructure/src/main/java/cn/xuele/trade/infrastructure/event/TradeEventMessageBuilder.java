package cn.xuele.trade.infrastructure.event;

import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Kafka 交易事件消息构建器。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Component
public class TradeEventMessageBuilder {

    private static final String PRODUCER = "trade-service";

    private final ObjectMapper objectMapper;

    public TradeEventMessageBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String build(TradeEventOutbox event) {
        try {
            JsonNode payload = objectMapper.readTree(event.getPayloadJson());
            ObjectNode envelope = objectMapper.createObjectNode();
            envelope.put("eventId", event.getEventId());
            envelope.put("eventType", event.getEventType());
            envelope.put("eventVersion", event.getEventVersion());
            envelope.put("producer", PRODUCER);
            envelope.put("aggregateType", event.getAggregateType());
            envelope.put("aggregateId", event.getAggregateId());
            envelope.put("bizId", event.getBizId());
            envelope.put("occurredAt", event.getOccurredAt().toString());
            envelope.set("payload", payload);
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("build trade event message failed, eventId=" + event.getEventId(), e);
        }
    }
}
