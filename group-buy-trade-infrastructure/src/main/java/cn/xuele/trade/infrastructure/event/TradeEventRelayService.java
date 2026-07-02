package cn.xuele.trade.infrastructure.event;

import cn.xuele.trade.infrastructure.dao.ITradeEventOutboxDao;
import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易事件 outbox relay 服务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Service
public class TradeEventRelayService {

    private final ITradeEventOutboxDao outboxDao;
    private final ITradeEventPublisher publisher;
    private final int maxRetryCount;
    private final int batchSize;

    public TradeEventRelayService(ITradeEventOutboxDao outboxDao,
                                  ITradeEventPublisher publisher,
                                  @Value("${trade.event.relay.max-retry-count:5}") int maxRetryCount,
                                  @Value("${trade.event.relay.batch-size:50}") int batchSize) {
        this.outboxDao = outboxDao;
        this.publisher = publisher;
        this.maxRetryCount = maxRetryCount;
        this.batchSize = batchSize;
    }

    public TradeEventRelayResult relayOnce() {
        List<TradeEventOutbox> events = outboxDao.queryPendingEvents(batchSize);
        int success = 0;
        int retry = 0;
        int fail = 0;

        for (TradeEventOutbox event : events) {
            try {
                publisher.publish(event);
                outboxDao.markSent(event.getEventId(), LocalDateTime.now());
                success++;
            } catch (Exception e) {
                int nextRetryCount = currentRetryCount(event) + 1;
                String lastError = trimError(e);
                if (nextRetryCount >= maxRetryCount) {
                    outboxDao.markFailed(event.getEventId(), nextRetryCount, lastError);
                    fail++;
                } else {
                    outboxDao.markRetry(event.getEventId(), nextRetryCount, nextRetryTime(nextRetryCount), lastError);
                    retry++;
                }
            }
        }

        return new TradeEventRelayResult(events.size(), success, retry, fail);
    }

    private int currentRetryCount(TradeEventOutbox event) {
        return event.getRetryCount() == null ? 0 : event.getRetryCount();
    }

    private LocalDateTime nextRetryTime(int retryCount) {
        return LocalDateTime.now().plusMinutes(Math.min(retryCount, 5));
    }

    private String trimError(Exception e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            message = e.getClass().getSimpleName();
        }
        return message.length() > 512 ? message.substring(0, 512) : message;
    }
}
