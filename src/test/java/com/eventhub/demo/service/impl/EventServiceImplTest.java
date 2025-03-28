package com.eventhub.demo.service.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.exception.ResourceNotFoundException;
import com.eventhub.demo.mapper.EventMapper;
import com.eventhub.demo.model.Event;
import com.eventhub.demo.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper mapper;

    @Mock
    private UserServiceMock userService;

    @InjectMocks
    private EventServiceImpl service;

    private Event event;
    private EventRequestDTO eventRequestDTO;
    private EventResponseDTO eventResponseDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        event = Event.builder()
                .id(1L)
                .title("Title")
                .description("Desc")
                .location("Test location")
                .startTime(future)
                .endTime(future.plusHours(2))
                .createdAt(now)
                .build();

        eventRequestDTO = new EventRequestDTO(
                "Title",
                "Desc",
                "Test location",
                future,
                future.plusHours(2),
                null
        );

        eventResponseDTO = new EventResponseDTO(
                1L,
                "Title",
                "Desc",
                "Test location",
                future,
                future.plusHours(2),
                null,
                null
        );
    }

    @Test
    void findAllEvents_shouldReturnListOfEvents() {
        // Given
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(mapper.toResponseDTOList(List.of(event))).thenReturn(List.of(eventResponseDTO));

        // When
        List<EventResponseDTO> response = service.findAllEvents();

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).title()).isEqualTo("Title");
        verify(eventRepository, times(1)).findAll();
        verify(mapper, times(1)).toResponseDTOList(List.of(event));
    }

    @Test
    void findAllEventsPaginated_shouldReturnPageOfEvents() {
        // Given
        Page<Event> eventPage = new PageImpl<>(List.of(event));
        Pageable pageable = PageRequest.of(0, 10);

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);
        when(mapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        Page<EventResponseDTO> result = service.findAllEvents(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(eventRepository, times(1)).findAll(pageable);
    }

    @Test
    void findEventById_whenEventExists_shouldReturnEvent() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(mapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        EventResponseDTO result = service.findEventById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void findEventById_whenEventDoesNotExist_shouldThrowException() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.findEventById(999L));
        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    void createEvent_shouldReturnCreatedEvent() {
        // Given
        when(mapper.toEntity(eventRequestDTO)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(mapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        EventResponseDTO result = service.createEvent(eventRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Title");
        verify(mapper, times(1)).toEntity(eventRequestDTO);
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(mapper, times(1)).toResponseDTO(event);
    }

    @Test
    void updateEvent_whenEventExists_shouldReturnUpdatedEvent() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(mapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        EventResponseDTO result = service.updateEvent(1L, eventRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Title");
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_whenEventDoesNotExist_shouldThrowException() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.updateEvent(999L, eventRequestDTO));
        verify(eventRepository, times(1)).findById(999L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_whenEventExists_shouldReturnTrue() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        // When
        boolean result = service.deleteEvent(1L);

        // Then
        assertThat(result).isTrue();
        verify(eventRepository, times(1)).existsById(1L);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEvent_whenEventDoesNotExist_shouldThrowException() {
        // Given
        when(eventRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.deleteEvent(999L));
        verify(eventRepository, times(1)).existsById(999L);
        verify(eventRepository, never()).deleteById(999L);
    }
}