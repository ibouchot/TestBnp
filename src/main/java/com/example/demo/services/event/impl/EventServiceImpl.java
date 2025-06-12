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

        return dto;
    }

    @Override
    public List<EventDto> findAll() {
        return List.of();
    }

    @Override
    public EventDto findById(Integer id) {

        return null;
    }

    @Override
    public EventDto findByEventType(String type) {

        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}
