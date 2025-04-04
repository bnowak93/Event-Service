package com.eventhub.demo.service.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.event_kafka.EventCreated;
import com.eventhub.demo.event_kafka.EventDeleted;
import com.eventhub.demo.event_kafka.EventUpdated;
import com.eventhub.demo.event_kafka.KafkaEventFactory;
import com.eventhub.demo.exception.ErrorMessages;
import com.eventhub.demo.exception.ResourceNotFoundException;
import com.eventhub.demo.mapper.EventMapper;
import com.eventhub.demo.model.Event;
import com.eventhub.demo.monitoring.EventServiceMetrics;
import com.eventhub.demo.repository.EventRepository;
import com.eventhub.demo.service.EventService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final EventMapper mapper;
    private final UserServiceMock userService;
    private final EventServiceMetrics metrics;
    private final OutboxService outboxService;

    @Autowired
    public EventServiceImpl(EventRepository repository,
                            @Qualifier("eventMapperImpl") EventMapper mapper,
                            UserServiceMock userService,
                            EventServiceMetrics metrics,
                            OutboxService outboxService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
        this.metrics = metrics;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAllEvents() {
        log.debug("Finding all events");
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Override
    public Page<EventResponseDTO> findAllEvents(Pageable pageable) {
        log.debug("Finding events with pagination {}", pageable);
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO findEventById(Long id) {
        log.debug("Finding event with id {}", id);
        return repository.findById(id).map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.EVENT_NOT_FOUND, id)
                ));
    }

    @Override
    public EventResponseDTO createEvent(@NotNull EventRequestDTO dto) {
        log.debug("Creating new event {}", dto);
        Instant start = Instant.now();
        Event entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());

        Event saved = repository.save(entity);

        // Record metrics
        long duration = Duration.between(start, Instant.now()).toMillis();
        metrics.recordEventCreationTime(duration);
        metrics.recordEventCreated();

        EventCreated eventCreated = KafkaEventFactory.createEventCreated(saved);

        //save to outbox (within same transaction)
        outboxService.saveToOutbox(
                "EVENT",
                saved.getId().toString(),
                "EVENT_CREATED",
                eventCreated
        );

        // Publish event
//        eventPublisher.publishEventCreated(new EventCreatedEvent(
//                saved.getId(),
//                saved.getTitle(),
//                saved.getDescription(),
//                saved.getLocation(),
//                saved.getStartTime(),
//                saved.getEndTime(),
//                saved.getOrganizerId(),
//                saved.getCreatedAt()
//        ));

        log.info("Created event with id: {}", saved.getId());
        return mapper.toResponseDTO(saved);
    }

    @Override
    public EventResponseDTO updateEvent(@NotNull Long id, @NotNull EventRequestDTO dto) {
        log.debug("Updating event with id: {}", id);
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.EVENT_NOT_FOUND, id)
                ));

        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setLocation(dto.location());
        event.setStartTime(dto.startTime());
        event.setEndTime(dto.endTime());

        Event updated = repository.save(event);

        // Record metrics
        metrics.recordEventUpdated();

        // Create event for outbox
        EventUpdated eventUpdated = KafkaEventFactory.createEventUpdated(updated);

        // Save to outbox
        outboxService.saveToOutbox(
                "EVENT",
                updated.getId().toString(),
                "EVENT_UPDATED",
                eventUpdated
        );

//        // Publish event
//        kafkaEventPublisher.publishEventUpdated(new EventUpdated(
//                updated.getId(),
//                updated.getTitle(),
//                updated.getDescription(),
//                updated.getLocation(),
//                updated.getStartTime(),
//                updated.getEndTime(),
//                updated.getOrganizerId(),
//                LocalDateTime.now()
//        ));

        log.info("Updated event with ID: {}", id);

        return mapper.toResponseDTO(updated);
    }

    @Override
    public boolean deleteEvent(Long id) {
        log.debug("Deleting event with id {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.EVENT_NOT_FOUND, id)
            );
        }
        repository.deleteById(id);

        // Record metrics
        metrics.recordEventDeleted();

        // Create event for outbox
        EventDeleted eventDeleted = KafkaEventFactory.createEventDeleted(id);

        // Save to outbox
        outboxService.saveToOutbox(
                "EVENT",
                id.toString(),
                "EVENT_DELETED",
                eventDeleted
        );

//         Publish event
//        kafkaEventPublisher.publishEventDeleted(new EventDeleted(
//                id,
//                LocalDateTime.now()
//        ));

        log.info("Deleted event with ID: {}", id);
        return true;
    }

}

