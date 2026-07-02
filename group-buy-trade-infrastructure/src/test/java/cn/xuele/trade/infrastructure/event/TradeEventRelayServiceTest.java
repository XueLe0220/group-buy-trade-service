package cn.xuele.trade.infrastructure.event;

import cn.xuele.trade.infrastructure.dao.ITradeEventOutboxDao;
import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeEventRelayServiceTest {

    @Test
    void marksEventSentWhenKafkaPublishSucceeds() {
        RecordingOutboxDao outboxDao = new RecordingOutboxDao(List.of(event("E001", 0)));
        RecordingPublisher publisher = new RecordingPublisher(false);
        TradeEventRelayService relayService = new TradeEventRelayService(outboxDao, publisher, 5, 50);

        TradeEventRelayResult result = relayService.relayOnce();

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getSuccess());
        assertEquals(0, result.getRetry());
        assertEquals(0, result.getFail());
        assertEquals(List.of("E001"), publisher.publishedEventIds);
        assertEquals(List.of("E001"), outboxDao.sentEventIds);
    }

    @Test
    void marksEventRetryWhenKafkaPublishFailsAndRetryLimitNotReached() {
        RecordingOutboxDao outboxDao = new RecordingOutboxDao(List.of(event("E002", 1)));
        RecordingPublisher publisher = new RecordingPublisher(true);
        TradeEventRelayService relayService = new TradeEventRelayService(outboxDao, publisher, 5, 50);

        TradeEventRelayResult result = relayService.relayOnce();

        assertEquals(1, result.getTotal());
        assertEquals(0, result.getSuccess());
        assertEquals(1, result.getRetry());
        assertEquals(0, result.getFail());
        assertEquals(List.of(new RetryUpdate("E002", 2)), outboxDao.retryUpdates);
    }

    @Test
    void marksEventFailedWhenKafkaPublishFailsAndRetryLimitReached() {
        RecordingOutboxDao outboxDao = new RecordingOutboxDao(List.of(event("E003", 4)));
        RecordingPublisher publisher = new RecordingPublisher(true);
        TradeEventRelayService relayService = new TradeEventRelayService(outboxDao, publisher, 5, 50);

        TradeEventRelayResult result = relayService.relayOnce();

        assertEquals(1, result.getTotal());
        assertEquals(0, result.getSuccess());
        assertEquals(0, result.getRetry());
        assertEquals(1, result.getFail());
        assertEquals(List.of("E003"), outboxDao.failedEventIds);
    }

    private static TradeEventOutbox event(String eventId, int retryCount) {
        return TradeEventOutbox.builder()
                .eventId(eventId)
                .eventType("TEAM_COMPLETED")
                .aggregateType("GROUP_BUY_TEAM")
                .aggregateId("T1001")
                .bizId("TEAM_COMPLETED:T1001")
                .eventVersion(1)
                .payloadJson("{\"teamId\":\"T1001\"}")
                .topic("group-buy.trade.events")
                .status(0)
                .retryCount(retryCount)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    private record RetryUpdate(String eventId, int retryCount) {
    }

    private static class RecordingPublisher implements ITradeEventPublisher {

        private final boolean fail;
        private final List<String> publishedEventIds = new ArrayList<>();

        private RecordingPublisher(boolean fail) {
            this.fail = fail;
        }

        @Override
        public void publish(TradeEventOutbox event) {
            if (fail) {
                throw new IllegalStateException("kafka unavailable");
            }
            publishedEventIds.add(event.getEventId());
        }
    }

    private static class RecordingOutboxDao implements ITradeEventOutboxDao {

        private final List<TradeEventOutbox> events;
        private final List<String> sentEventIds = new ArrayList<>();
        private final List<RetryUpdate> retryUpdates = new ArrayList<>();
        private final List<String> failedEventIds = new ArrayList<>();

        private RecordingOutboxDao(List<TradeEventOutbox> events) {
            this.events = new ArrayList<>(events);
        }

        @Override
        public int insert(TradeEventOutbox event) {
            events.add(event);
            return 1;
        }

        @Override
        public List<TradeEventOutbox> queryPendingEvents(int limit) {
            return events;
        }

        @Override
        public int markSent(String eventId, LocalDateTime sentTime) {
            sentEventIds.add(eventId);
            return 1;
        }

        @Override
        public int markRetry(String eventId, int retryCount, LocalDateTime nextRetryTime, String lastError) {
            retryUpdates.add(new RetryUpdate(eventId, retryCount));
            return 1;
        }

        @Override
        public int markFailed(String eventId, int retryCount, String lastError) {
            failedEventIds.add(eventId);
            return 1;
        }
    }
}
