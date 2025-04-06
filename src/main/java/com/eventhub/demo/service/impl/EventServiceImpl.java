package com.eventhub.demo.service.impl;

import com.eventhub.demo.client.UserServiceClient;
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
    private final UserServiceClient userServiceClient;
    private final EventServiceMetrics metrics;
    private final OutboxService outboxService;

    @Autowired
    public EventServiceImpl(EventRepository repository,
                            @Qualifier("eventMapperImpl") EventMapper mapper,
                            UserServiceClient userServiceClient,
                            EventServiceMetrics metrics,
                            OutboxService outboxService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.userServiceClient = userServiceClient;
        this.metrics = metrics;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAllEvents() {
        log.debug("Finding all events");
        List<Event> events = repository.findAll();
        List<EventResponseDTO> dtos = mapper.toResponseDTOList(events);

        // Enhance DTOs with organizer names from User Service
        dtos.forEach(this::enrichWithOrganizerInfo);

        return dtos;
    }

    @Override
    public Page<EventResponseDTO> findAllEvents(Pageable pageable) {
        log.debug("Finding events with pagination {}", pageable);
        return repository.findAll(pageable)
                .map(event -> {
                    EventResponseDTO dto = mapper.toResponseDTO(event);
                    return enrichWithOrganizerInfo(dto);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO findEventById(Long id) {
        log.debug("Finding event with id {}", id);
        EventResponseDTO dto = repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.EVENT_NOT_FOUND, id)
                ));
        return enrichWithOrganizerInfo(dto);
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

        log.info("Created event with id: {}", saved.getId());
        EventResponseDTO responseDTO = mapper.toResponseDTO(saved);
        return enrichWithOrganizerInfo(responseDTO);
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

        log.info("Updated event with ID: {}", id);

        EventResponseDTO responseDTO = mapper.toResponseDTO(updated);

        return enrichWithOrganizerInfo(responseDTO);
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

        log.info("Deleted event with ID: {}", id);
        return true;
    }

    /**
     * Enriches the event DTO with organizer information from the User Service
     *
     * @param dto event response DTO
     * @return response DTO with organizer username if found else return dto without changes
     */
    private EventResponseDTO enrichWithOrganizerInfo(EventResponseDTO dto) {
        if (dto.organizerId() != null) {
            String organizerName = userServiceClient.getUserName(dto.organizerId());

            return new EventResponseDTO(
                    dto.id(),
                    dto.title(),
                    dto.description(),
                    dto.location(),
                    dto.startTime(),
                    dto.endTime(),
                    dto.organizerId(),
                    organizerName
            );
        }
        return dto;
    }

}

