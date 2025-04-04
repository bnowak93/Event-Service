package com.eventhub.demo.event_kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.event-created}")
    private String eventCreatedTopic;

    @Value("${app.kafka.topics.event-updated}")
    private String eventUpdatedTopic;

    @Value("${app.kafka.topics.event-deleted}")
    private String eventDeletedTopic;

    public void publishEventCreated(EventCreated event) {
        String key = event.eventId().toString();
        log.info("Publishing event created: {}", key);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(eventCreatedTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event created message=[{}] with offset=[{}]",
                         event,
                         result.getRecordMetadata().offset()
                );
            } else {
                log.error("Unable to send event created message=[{}] due to : {}",
                          event,
                          ex.getMessage()
                );
            }
        });
    }

    public void publishEventUpdated(EventUpdated event) {
        String key = event.eventId().toString();
        log.info("Publishing event updated: {}", key);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(eventUpdatedTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event updated message=[{}] with offset=[{}]",
                         event,
                         result.getRecordMetadata().offset()
                );
            } else {
                log.error("Unable to send event updated message=[{}] due to : [{}]",
                          event,
                          ex.getMessage()
                );
            }
        });
    }

    public void publishEventDeleted(EventDeleted event) {
        String key = event.eventId().toString();
        log.info("Publishing event deleted: {}", key);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(eventDeletedTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event deleted message=[{}] with offset=[{}]",
                         event,
                         result.getRecordMetadata().offset()
                );
            } else {
                log.error("Unable to send event deleted message=[{}] due to : [{}]",
                          event,
                          ex.getMessage()
                );
            }
        });
    }
}
