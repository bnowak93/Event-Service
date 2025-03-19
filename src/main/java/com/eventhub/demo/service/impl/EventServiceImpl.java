package com.eventhub.demo.service.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.mapper.EventMapper;
import com.eventhub.demo.model.Event;
import com.eventhub.demo.repository.EventRepository;
import com.eventhub.demo.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final EventMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAllEvents() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventResponseDTO> findEventById(Long id) {
        return repository.findById(id).map(mapper::toResponseDTO);
    }

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto) {
        Event entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());

        Event saved = repository.save(entity);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public Optional<EventResponseDTO> updateEvent(Long id, EventRequestDTO dto) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.title());
                    existing.setDescription(dto.description());
                    existing.setLocation(dto.location());
                    existing.setStartTime(dto.startTime());
                    existing.setEndTime(dto.endTime());

                    return repository.save(existing);
                })
                .map(mapper::toResponseDTO);
    }

    @Override
    public boolean deleteEvent(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
