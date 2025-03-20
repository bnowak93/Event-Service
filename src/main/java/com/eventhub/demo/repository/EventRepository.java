package com.eventhub.demo.repository;

import com.eventhub.demo.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByLocation(String location);

    Page<Event> findByLocation(String location, Pageable pageable);

    Page<Event> findByStartTimeAfter(LocalDateTime date, Pageable pageable);
}
