package com.eventhub.demo.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User organizer;

    @NotBlank(message = "Title can't be blank")
    private String title;
    @NotBlank(message = "Please provide description")
    private String description;
    @NotBlank(message = "Location must be provided")
    private String location;
    @NotNull(message = "Start date must be set")
    @Future(message = "Event start date must be in future")
    private LocalDateTime startTime;
    @NotNull(message = "End date must be se")
    @Future(message = "Event end date must be in future")
    private LocalDateTime endTime;
    @NotNull
    private LocalDateTime createdAt;
}
