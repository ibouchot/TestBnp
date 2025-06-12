package com.example.demo.services.transaction.impl;

import com.example.demo.dto.EventDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.model.Event;
import com.example.demo.model.Transaction;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.services.event.EventService;
import com.example.demo.services.transaction.TransactionService;
import com.example.demo.validators.ObjectsValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final ObjectsValidator<TransactionDto> validator;
    private final TransactionRepository repository;
    private final EventRepository eventRepository;

    public TransactionServiceImpl(ObjectsValidator<TransactionDto> validator, TransactionRepository repository, EventService eventService, EventRepository eventRepository) {
        this.validator = validator;
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    public TransactionDto save(TransactionDto dto, int eventId) {
        validator.validate(dto);
        Transaction transaction = TransactionDto.toEntity(dto);
        Optional<Event> eventDto = eventRepository.findById(eventId);
        eventDto.ifPresent(transaction::setEvent);
        Transaction savedTransaction = repository.save(transaction);
        return TransactionDto.fromEntity(savedTransaction);
    }

    @Override
    public List<TransactionDto> findAll() {
        return repository.findAll()
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto findById(Integer id) {
        return repository.findById(id)
                .map(TransactionDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No transaction found with ID: " + id));
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public TransactionDto save(TransactionDto dto) {
        return null;
    }
}
