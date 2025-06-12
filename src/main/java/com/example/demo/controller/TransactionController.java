package com.example.demo.controller;

import com.example.demo.dto.TransactionDto;
import com.example.demo.services.transaction.TransactionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<TransactionDto>> getAllTransactions(){
        List <TransactionDto> transactions = transactionService.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{primaryId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsById(@PathVariable String primaryId){
        System.out.println(primaryId);
        TransactionDto transactionDto = transactionService.findByPrimaryId(primaryId);
        System.out.println(transactionDto);
        String groupId = transactionDto.getGroupId();
        List<TransactionDto> transactions = transactionService.findTransactionByGroupId(groupId);

        return ResponseEntity.ok(transactions);
    }

}
