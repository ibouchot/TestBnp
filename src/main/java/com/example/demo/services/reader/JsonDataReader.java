package com.example.demo.services.reader;

import com.example.demo.dto.TransactionDto;
import com.example.demo.services.transaction.TransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public class JsonDataReader {

    private static final String TRANSACTION_FILE_PATH = "classpath:data/transactions.json";
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    public JsonDataReader(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public List<TransactionDto> readTransactionsFromFile() throws IOException {
        Resource resource = resourceLoader.getResource(TRANSACTION_FILE_PATH);

        if (!resource.exists()) {
            throw new FileNotFoundException("Could not find file: " + TRANSACTION_FILE_PATH);
        }

        List<TransactionDto> transactions = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<TransactionDto>>(){}
        );

        return transactions;
    }

}
