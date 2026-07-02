package cn.xuele.trade.infrastructure.event;

import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 交易事件发布器。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Component
public class KafkaTradeEventPublisher implements ITradeEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TradeEventMessageBuilder messageBuilder;

    public KafkaTradeEventPublisher(KafkaTemplate<String, String> kafkaTemplate, TradeEventMessageBuilder messageBuilder) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void publish(TradeEventOutbox event) {
        String message = messageBuilder.build(event);
        kafkaTemplate.send(event.getTopic(), event.getAggregateId(), message).join();
    }
}
