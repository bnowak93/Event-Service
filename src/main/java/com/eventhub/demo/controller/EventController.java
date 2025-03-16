package com.eventhub.demo.controller;

import com.eventhub.demo.dto.EventDTO;
import com.eventhub.demo.model.Event;
import com.eventhub.demo.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        List<EventDTO> events = eventRepository.findAll().stream()
                .map(event -> new EventDTO(
                        event.getTitle(),
                        event.getDescription(),
                        event.getLocation(),
                        event.getStartTime(),
                        event.getEndTime()))
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> ResponseEntity.ok(new EventDTO(
                        event.getTitle(),
                        event.getDescription(),
                        event.getLocation(),
                        event.getStartTime(),
                        event.getEndTime()
                ))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO eventDTO) {
        Event event = Event.builder()
                .title(eventDTO.title())
                .description(eventDTO.description())
                .location(eventDTO.location())
                .startTime(eventDTO.startTime())
                .endTime(eventDTO.endTime())
                .createdAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @RequestBody EventDTO updatedEvent) {
        return eventRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedEvent.title());
                    existing.setDescription(updatedEvent.description());
                    existing.setLocation(updatedEvent.location());
                    existing.setStartTime(updatedEvent.startTime());
                    existing.setEndTime(updatedEvent.endTime());
                    eventRepository.save(existing);
                    return ResponseEntity.ok(updatedEvent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
