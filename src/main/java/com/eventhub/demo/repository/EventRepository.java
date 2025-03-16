package com.eventhub.demo.repository;

import com.eventhub.demo.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByLocation(String location);
}
