package com.eventhub.demo.service;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    List<EventResponseDTO> findAllEvents();

    Page<EventResponseDTO> findAllEvents(Pageable pageable);

    EventResponseDTO findEventById(Long id);

    EventResponseDTO createEvent(EventRequestDTO dto);

    EventResponseDTO updateEvent(Long id, EventRequestDTO dto);

    boolean deleteEvent(Long id);
}
