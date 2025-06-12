package com.example.demo.services.event.impl;

import com.example.demo.dto.EventDto;
import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;
import com.example.demo.services.event.EventService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.validators.ObjectsValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final ObjectsValidator<EventDto> validator;
    private final EventRepository repository;

    public EventServiceImpl(ObjectsValidator<EventDto> validator, EventRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }

    @Override
    public EventDto save(EventDto dto) {
        validator.validate(dto);
        Event event = EventDto.toEntity(dto);
        Event savedEvent = repository.save(event);
        return EventDto.fromEntity(savedEvent);
    }

    @Override
    public List<EventDto> findAll() {
        return repository.findAll()
                .stream()
                .map(EventDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto findById(Integer id) {
        return repository.findById(id)
                .map(EventDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No event found with ID: " + id));
    }

    @Override
    public EventDto findByEventType(String type) {
        return repository.findByEventType(type)
                .map(EventDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No event found with type: " + type));
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public void delete(Integer id) {

    }
}
