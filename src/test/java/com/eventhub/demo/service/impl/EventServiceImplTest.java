package com.eventhub.demo.service.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.mapper.EventMapper;
import com.eventhub.demo.model.Event;
import com.eventhub.demo.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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

        eventRequestDTO = new EventRequestDTO("Title", "Desc", "Test location", future, future.plusHours(2), null);

        eventResponseDTO = new EventResponseDTO(1L, "Title", "Desc", "Test location", future, future.plusHours(2), null, null);
    }
}