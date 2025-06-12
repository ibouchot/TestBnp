package com.example.demo.services.load;

import com.example.demo.dto.TransactionDto;
import com.example.demo.services.reader.JsonDataReader;
import com.example.demo.services.transaction.TransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Clock;
import java.util.List;

@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    private final JsonDataReader dataReader;
    private final TransactionService transactionService;

    public DataLoader(JsonDataReader dataReader, TransactionService transactionService) {
        this.dataReader = dataReader;
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) throws Exception {
        try{
            //loadTransactionsIntoDatabase();
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void loadTransactionsIntoDatabase(List<TransactionDto> transactions) throws IOException {
        transactions.forEach(transactionService::save);
    }

    public void reconciliateTransactions() throws IOException {
        List<TransactionDto> transactions = dataReader.readTransactionsFromFile();
        transactions.forEach(transactionService::save);
        
    }


}
