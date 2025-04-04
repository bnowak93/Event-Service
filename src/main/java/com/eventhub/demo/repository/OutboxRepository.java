package com.eventhub.demo.repository;

import com.eventhub.demo.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByProcessedAtIsNotNullOrderByCreatedAt();

    List<OutboxEvent> findByProcessedAtLessThanAndCreatedAtGreaterThan(
            LocalDateTime processedBefore, LocalDateTime createdAfter);
}
