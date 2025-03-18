package com.eventhub.demo.service.impl;

import com.eventhub.demo.dto.EventDTO;
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
    public List<EventDTO> findAllEvents() {
        return mapper.toDTOList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventDTO> findEventById(Long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    @Override
    public EventDTO createEvent(EventDTO dto) {
        Event entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());

        Event saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public Optional<EventDTO> updateEvent(Long id, EventDTO dto) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.title());
                    existing.setDescription(dto.description());
                    existing.setLocation(existing.getLocation());
                    existing.setLocation(existing.getLocation());
                    existing.setStartTime(existing.getStartTime());
                    existing.setEndTime(existing.getEndTime());

                    return repository.save(existing);
                })
                .map(mapper::toDTO);
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
