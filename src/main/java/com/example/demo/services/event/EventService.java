package com.example.demo.services.event;

import com.example.demo.dto.EventDto;
import com.example.demo.services.AbstractService;

public interface EventService extends AbstractService<EventDto> {
    EventDto save(EventDto dto);

    EventDto findByEventType(String type);

    Long count();
}
