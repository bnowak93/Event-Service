package com.eventhub.demo.controller;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        return ResponseEntity.ok(service.findAllEvents());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<EventResponseDTO>> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = direction.equals("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<EventResponseDTO> events = service.findAllEvents(pageable);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        EventResponseDTO createdEvent = service.createEvent(eventRequestDTO);
        return ResponseEntity.created(URI.create("/api/events/" + createdEvent.id()))
                .body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EventRequestDTO updatedEvent) {
        return ResponseEntity.ok(service.updateEvent(id, updatedEvent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@NotNull @PathVariable Long id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
