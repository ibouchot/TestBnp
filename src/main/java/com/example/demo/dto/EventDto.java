package com.example.demo.dto;

import com.example.demo.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Integer id;
    private String stepCode;
    private Integer stepRank;
    private Integer eventRank;
    private String eventType;

    public EventDto(String eventType) {
    }

    public static EventDto fromEntity(Event event) {
        if (event == null) return null;
        return new EventDto(
                event.getEventId(),
                event.getStepCode(),
                event.getStepRank(),
                event.getEventRank(),
                event.getEventType()
        );
    }

    public static Event toEntity(EventDto dto) {
        if (dto == null) return null;
        Event event = new Event();
        event.setEventId(dto.getId());
        event.setStepCode(dto.getStepCode());
        event.setStepRank(dto.getStepRank());
        event.setEventRank(dto.getEventRank());
        event.setEventType(dto.getEventType());
        return event;
    }
}
