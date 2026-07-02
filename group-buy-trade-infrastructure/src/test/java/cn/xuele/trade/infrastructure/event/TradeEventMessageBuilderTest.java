package cn.xuele.trade.infrastructure.event;

import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeEventMessageBuilderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void buildsEventEnvelopeWithPayloadObject() throws Exception {
        TradeEventMessageBuilder messageBuilder = new TradeEventMessageBuilder(objectMapper);
        LocalDateTime occurredAt = LocalDateTime.of(2026, 6, 24, 12, 30, 15);
        TradeEventOutbox event = TradeEventOutbox.builder()
                .eventId("E001")
                .eventType("TEAM_COMPLETED")
                .aggregateType("GROUP_BUY_TEAM")
                .aggregateId("T1001")
                .bizId("TEAM_COMPLETED:T1001")
                .eventVersion(1)
                .payloadJson("{\"teamId\":\"T1001\",\"outTradeNoList\":[\"OT001\",\"OT002\"]}")
                .topic("group-buy.trade.events")
                .occurredAt(occurredAt)
                .build();

        String message = messageBuilder.build(event);

        JsonNode root = objectMapper.readTree(message);
        assertEquals("E001", root.get("eventId").asText());
        assertEquals("TEAM_COMPLETED", root.get("eventType").asText());
        assertEquals(1, root.get("eventVersion").asInt());
        assertEquals("trade-service", root.get("producer").asText());
        assertEquals("GROUP_BUY_TEAM", root.get("aggregateType").asText());
        assertEquals("T1001", root.get("aggregateId").asText());
        assertEquals("TEAM_COMPLETED:T1001", root.get("bizId").asText());
        assertEquals("2026-06-24T12:30:15", root.get("occurredAt").asText());
        assertEquals("T1001", root.get("payload").get("teamId").asText());
        assertEquals("OT002", root.get("payload").get("outTradeNoList").get(1).asText());
    }
}
