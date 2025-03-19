package com.eventhub.demo.controller;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService service;

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAll() {
        List<EventResponseDTO> events = service.findAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        EventResponseDTO event = service.findEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        EventResponseDTO createdEvent = service.createEvent(eventRequestDTO);
        return ResponseEntity.created(URI.create("/api/events/" + createdEvent.id())).body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> update(@NotNull @PathVariable Long id, @Valid @RequestBody EventRequestDTO updatedEvent) {

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@NotNull @PathVariable Long id) {
        boolean deleted = service.deleteEvent(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
