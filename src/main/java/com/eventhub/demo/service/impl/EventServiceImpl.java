package com.eventhub.demo.service.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.exception.ErrorMessages;
import com.eventhub.demo.exception.InvalidEventDateException;
import com.eventhub.demo.exception.ResourceNotFoundException;
import com.eventhub.demo.mapper.EventMapper;
import com.eventhub.demo.model.Event;
import com.eventhub.demo.repository.EventRepository;
import com.eventhub.demo.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final EventMapper mapper;
    private final UserServiceMock userService;

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAllEvents() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Override
    public Page<EventResponseDTO> findAllEvents(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO findEventById(Long id) {
        return repository.findById(id).map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.EVENT_NOT_FOUND, id)
                ));
    }

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto) {
        if (dto.startTime().isBefore(LocalDateTime.now())) {
            throw new InvalidEventDateException(ErrorMessages.EVENT_START_IN_PAST);
        }

        if (dto.endTime().isBefore(dto.startTime())) {
            throw new InvalidEventDateException(ErrorMessages.EVENT_END_BEFORE_START);
        }

        Event entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());

        Event saved = repository.save(entity);
        log.info("Created event with id: {}", saved.getId());
        return mapper.toResponseDTO(saved);
    }

    @Override
    public EventResponseDTO updateEvent(Long id, EventRequestDTO dto) {
        if (dto.startTime().isBefore(LocalDateTime.now())) {
            throw new InvalidEventDateException(ErrorMessages.EVENT_START_IN_PAST);
        }

        if (dto.endTime().isBefore(dto.startTime())) {
            throw new InvalidEventDateException(ErrorMessages.EVENT_END_BEFORE_START);
        }

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
        log.info("Updated event with ID: {}", id);

        return mapper.toResponseDTO(updated);
    }

    @Override
    public boolean deleteEvent(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.EVENT_NOT_FOUND, id)
            );
        }
        repository.deleteById(id);
        log.info("Deleted event with ID: {}", id);
        return true;
    }
}
