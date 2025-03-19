package com.eventhub.demo.mapper;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.dto.EventResponseDTO;
import com.eventhub.demo.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    Event toEntity(EventRequestDTO eventRequestDTO);

    EventResponseDTO toResponseDTO(Event event);

    List<Event> toEntityList(List<EventResponseDTO> eventResponseDTOList);

    List<EventResponseDTO> toResponseDTOList(List<Event> events);
}
