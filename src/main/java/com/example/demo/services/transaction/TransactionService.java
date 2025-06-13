package com.example.demo.services.transaction;

import com.example.demo.dto.EventDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.model.Transaction;
import com.example.demo.services.AbstractService;

import java.util.List;
import java.util.Optional;

public interface TransactionService extends AbstractService<TransactionDto> {
    TransactionDto save(TransactionDto dto);

    List<TransactionDto> saveAll(List<TransactionDto> dtos);

    List<TransactionDto> findTransactionByGroupId(String groupId);

    TransactionDto findByPrimaryId(String primaryId);


    List<TransactionDto> findAllByCorrectIsFalse();

    Long count();
}
