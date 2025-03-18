package com.eventhub.demo.mapper;

import com.eventhub.demo.dto.EventDTO;
import com.eventhub.demo.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Event toEntity(EventDTO eventDTO);

    EventDTO toDTO(Event event);

    List<Event> toEntityList(List<EventDTO> eventDTOList);

    List<EventDTO> toDTOList(List<Event> events);
}
