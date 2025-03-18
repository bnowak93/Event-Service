package com.eventhub.demo.service;

import com.eventhub.demo.dto.EventDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {

    public List<EventDTO> findAllEvents();

    public Optional<EventDTO> findEventById(Long id);

    public EventDTO createEvent(EventDTO dto);

    public Optional<EventDTO> updateEvent(Long id, EventDTO dto);

    public boolean deleteEvent(Long id);
}
