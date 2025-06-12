package com.example.demo.controller;

import com.example.demo.dto.TransactionDto;
import com.example.demo.services.transaction.TransactionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("ping ok");
    }

    @GetMapping("/all")
    public HttpEntity<List<TransactionDto>> getAllTransactions(){
        List <TransactionDto> transactions = transactionService.findAll();
        return ResponseEntity.ok(transactions);
    }
}
