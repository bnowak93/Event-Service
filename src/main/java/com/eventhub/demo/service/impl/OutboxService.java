package com.eventhub.demo.service.impl;

import com.eventhub.demo.event_kafka.EventCreated;
import com.eventhub.demo.event_kafka.EventDeleted;
import com.eventhub.demo.event_kafka.KafkaEventPublisher;
import com.eventhub.demo.event_kafka.EventUpdated;
import com.eventhub.demo.model.OutboxEvent;
import com.eventhub.demo.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveToOutbox(String aggregateType, String aggregateId, String eventType, Object payload) {
        try {
            String eventJson = objectMapper.writeValueAsString(payload);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(eventJson)
                    .createdAt(LocalDateTime.now())
                    .build();
            outboxRepository.save(outboxEvent);
            log.debug("Saved {} event for {} to outbox", eventType, aggregateId);
        } catch (Exception e) {
            log.error("Error saving to outbox: {}", e.getMessage(), e);
            // Re-throw as runtime exception to rollback transaction
            throw new RuntimeException("Failed to save event to outbox", e);
        }
    }

    @Scheduled(fixedRate = 5000)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByProcessedAtIsNotNullOrderByCreatedAt();
        log.info("Found {} pending outbox events to process", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                processEvent(event);
                event.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Error processing outbox event id {}:{}", event.getId(), e.getMessage());
            }
        }
    }

    private void processEvent(OutboxEvent event) throws Exception {
        log.info("Processing outbox event: {} for {}", event.getEventType(), event.getAggregateId());

        switch (event.getEventType()) {
            case "EVENT_CREATED:":
                kafkaEventPublisher.publishEventCreated(
                        objectMapper.readValue(event.getPayload(), EventCreated.class)
                );
                break;
            case "EVENT_UPDATED:":
                kafkaEventPublisher.publishEventUpdated(
                        objectMapper.readValue(event.getPayload(), EventUpdated.class)
                );
                break;
            case "EVENT_DELETED:":
                kafkaEventPublisher.publishEventDeleted(
                        objectMapper.readValue(event.getPayload(), EventDeleted.class)
                );
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
