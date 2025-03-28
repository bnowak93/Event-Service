package com.eventhub.demo.controller;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    private EventRequestDTO eventRequestDTO;
    private EventResponseDTO eventResponseDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        eventResponseDTO = new EventResponseDTO(
                1L,
                "Test Event",
                "Test Description that is long enough to pass validation",
                "Test Location",
                future,
                future.plusHours(2),
                null,
                null
        );

        eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "Test Description that is long enough to pass validation",
                "Test Location",
                future,
                future.plusHours(2),
                null
        );
    }

    @Test
    @WithMockUser
    void getAll_shouldReturnListOfEvents() throws Exception {
        // Given
        when(eventService.findAllEvents()).thenReturn(List.of(eventResponseDTO));

        // When & Then
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Event")));
    }

    @Test
    @WithMockUser
    void getAllPaginated_shouldReturnPageOfEvents() throws Exception {
        // Given
        when(eventService.findAllEvents(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(eventResponseDTO)));

        // When & Then
        mockMvc.perform(get("/api/events/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "10")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Test Event")));
    }
}