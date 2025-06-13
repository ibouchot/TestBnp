package com.example.demo.services.transaction.impl;

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

import java.util.ArrayList;
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
    public TransactionDto save(TransactionDto dto) {
        validator.validate(dto);
        Transaction transaction = TransactionDto.toEntity(dto);
        Optional<Event> eventDto = eventRepository.findById(dto.getEvent().getId());
        eventDto.ifPresent(transaction::setEvent);
        Transaction savedTransaction = repository.save(transaction);
        return TransactionDto.fromEntity(savedTransaction);
    }

    @Override
    public List<TransactionDto> saveAll(List<TransactionDto> dtos) {
        List<Transaction> transactions = new ArrayList<>();

        for (TransactionDto dto : dtos) {
            validator.validate(dto);
            Transaction transaction = TransactionDto.toEntity(dto);
            Optional<Event> eventOpt = Optional.empty();
            if (dto.getEvent() != null && dto.getEvent().getId() != null) {
                eventOpt = eventRepository.findById(dto.getEvent().getId());
            }
            eventOpt.ifPresent(transaction::setEvent);
            transactions.add(transaction);
        }

        List<Transaction> savedTransactions = repository.saveAll(transactions);

        return savedTransactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> findAll() {
        return repository.findAll()
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> findTransactionByGroupId(String groupId) {
        return repository.findTransactionByGroupId(groupId)
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto findByPrimaryId(String primaryId) {
        return repository.findByPrimaryId(primaryId)
                .map(TransactionDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No transaction found with primary ID: " + primaryId));
    }

    @Override
    public List<TransactionDto> findAllByCorrectIsFalse() {
        return repository.findAllByCorrectIsFalse()
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
    public Long count() {
        return repository.count();
    }

    @Override
    public void delete(Integer id) {

    }
}
