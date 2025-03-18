package com.eventhub.demo.controller;

import com.eventhub.demo.dto.EventDTO;
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
    public ResponseEntity<List<EventDTO>> getAll() {
        List<EventDTO> events = service.findAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@NotNull @PathVariable Long id) {

        return service.findEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = service.createEvent(eventDTO);
        return ResponseEntity.created(URI.create("/api/events/" + createdEvent.id())).body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@NotNull @PathVariable Long id, @Valid @RequestBody EventDTO updatedEvent) {
        return service.updateEvent(id, updatedEvent)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@NotNull @PathVariable Long id) {
        boolean deleted = service.deleteEvent(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
