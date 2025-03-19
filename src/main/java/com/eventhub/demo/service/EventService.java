package com.eventhub.demo.service;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {

    public List<EventResponseDTO> findAllEvents();
    public Optional<EventResponseDTO> findEventById(Long id);
    public EventResponseDTO createEvent(EventRequestDTO dto);
    public Optional<EventResponseDTO> updateEvent(Long id, EventRequestDTO dto);
    public boolean deleteEvent(Long id);
}
