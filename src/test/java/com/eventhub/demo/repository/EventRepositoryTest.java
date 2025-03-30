package com.eventhub.demo.repository;

import com.eventhub.demo.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none" //Turning off validation for sake of creating entities in the past
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRepositoryTest {

    @Autowired
    private EventRepository repository;

    @Test
    void findByLocation_shouldReturnEvent() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        Event event1 = Event.builder()
                .title("Event in Alaska")
                .description("Description 1")
                .location("Middle of Alaska")
                .startTime(future)
                .endTime(future.plusHours(2))
                .createdAt(now)
                .build();

        Event event2 = Event.builder()
                .title("Another event in Mexico")
                .description("Description 2")
                .location("Mexico City")
                .startTime(future)
                .endTime(future.plusHours(2))
                .createdAt(now)
                .build();

        Event event3 = Event.builder()
                .title("Event in Mexico")
                .description("Description 3")
                .location("Mexico City")
                .startTime(future)
                .endTime(future.plusHours(2))
                .createdAt(now)
                .build();

        repository.saveAll(List.of(event1, event2, event3));

        // When
        List<Event> result = repository.findByLocation("Mexico City");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Event::getTitle)
                .containsExactlyInAnyOrder("Event in Mexico", "Another event in Mexico");
    }

    @Test
    void findByLocation_withPagination_shouldReturnPagedEvents() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        for (int i = 0; i < 10; i++) {
            Event event = Event.builder()
                    .title("Some event nr: " + i + "in Europe")
                    .description("Description " + i)
                    .location("Prague")
                    .startTime(future.plusDays(i))
                    .endTime(future.plusDays(i).plusHours(2))
                    .createdAt(now)
                    .build();
            repository.save(event);
        }

        // When
        Page<Event> result = repository.findByLocation(
                "Prague",
                PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "startTime"))
        );

        // Then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(2);

        //Verify sorting
        LocalDateTime previousTime = null;
        for (Event event : result.getContent()) {
            if (previousTime != null)
                assertThat(event.getStartTime()).isAfterOrEqualTo(previousTime);
            previousTime = event.getStartTime();
        }
    }

    @Test
    void findByStartTime_shouldReturnFutureEvents() {
        LocalDateTime now = LocalDateTime.now();

        Event pastEvent = Event.builder()
                .title("Past Event")
                .description("Description")
                .location("Some Location")
                .startTime(now.minusDays(1))
                .endTime(now.minusDays(1).plusHours(2))
                .createdAt(now.minusDays(2))
                .build();

        Event futureEvent1 = Event.builder()
                .title("Future event 1")
                .description("Description")
                .location("Location")
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(1).plusHours(2))
                .createdAt(now)
                .build();

        Event futureEvent2 = Event.builder()
                .title("Future event 2")
                .description("Description")
                .location("Location")
                .startTime(now.plusDays(2))
                .endTime(now.plusDays(2).plusHours(2))
                .createdAt(now)
                .build();

        repository.saveAll(List.of(pastEvent, futureEvent1, futureEvent2));

        // When
        Page<Event> result = repository.findByStartTimeAfter(
                now,
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Event::getTitle)
                .containsExactlyInAnyOrder("Future event 1", "Future event 2");
    }

}