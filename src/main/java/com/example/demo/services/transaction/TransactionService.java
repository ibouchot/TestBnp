package com.example.demo.services.transaction;

import com.example.demo.dto.TransactionDto;
import com.example.demo.model.Transaction;
import com.example.demo.services.AbstractService;

public interface TransactionService extends AbstractService<TransactionDto> {
    TransactionDto save(TransactionDto dto);
}
