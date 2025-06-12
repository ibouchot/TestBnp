package com.example.demo.services.load;

import com.example.demo.dto.EventDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.services.event.EventService;
import com.example.demo.services.reader.JsonDataReader;
import com.example.demo.services.transaction.TransactionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    private final JsonDataReader dataReader;
    private final TransactionService transactionService;

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,                // 2025-07-07T05:56:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),    // 2025-02-01T33:19
    };

    public DataLoader(JsonDataReader dataReader, TransactionService transactionService, EventService eventService) {
        this.dataReader = dataReader;
        this.transactionService = transactionService;
    }


    @Override
    public void run(String... args) throws Exception {
        try{
            long count = transactionService.count();
            if (count == 0) {
                List<TransactionDto> transactions = loadTransactions();
            } else {
                System.out.println("La table Transaction n'est pas vide, chargement ignoré.");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public List<TransactionDto> loadTransactions() throws IOException {
        List<TransactionDto> transactions = dataReader.readTransactionsFromFile();

        List<TransactionDto> transactionsToSave = new ArrayList<>();
        Map<String, List<TransactionDto>> chains = determineEventRank(transactions);
        for (Map.Entry<String, List<TransactionDto>> entry : chains.entrySet()) {
            List<TransactionDto> chain = entry.getValue();
            String groupId = entry.getKey();
            for (int i = 0; i < chain.size(); i++) {
                TransactionDto transactionDto = chain.get(i);
                EventDto eventDto = new EventDto();
                eventDto.setId(i+1);
                transactionDto.setEvent(eventDto);
                transactionDto.setGroupId(groupId);
                transactionsToSave.add(transactionDto);
            }
        }
        transactionService.saveAll(transactionsToSave);

        for (TransactionDto dto : transactions) {
            //dto.setDate(fixMalformedDate(dto.getDate()));
        }

        return transactions;
    }

    private Map<String, List<TransactionDto>>  determineEventRank(List<TransactionDto> transactions){

        ArrayList<String> lastEventList = new ArrayList<String>();
        Map<String, TransactionDto> byPrimaryId = new HashMap<>();
        Map<String, String> secondaryByPrimary = new HashMap<>();


        for (TransactionDto transactionDto : transactions) {
            byPrimaryId.put(transactionDto.getPrimaryId(), transactionDto);
            secondaryByPrimary.put(transactionDto.getPrimaryId(), transactionDto.getSecondaryId());
            if ("CBAcquiredToPfmt101".equals(transactionDto.getEvent().getEventType())) {
                lastEventList.add(transactionDto.getPrimaryId());
            }
        }

        System.out.println("LastEventMap.size() "+ lastEventList.size());

        Map<String, List<TransactionDto>> chains = new HashMap<>();

        for (String lastEvent : lastEventList) {
                List<TransactionDto> chain = new ArrayList<>();
                String currentId = lastEvent;
                int count = 0;

                while (currentId != null && count < 20) {
                    TransactionDto current = byPrimaryId.get(currentId);
                    if (current == null) break;

                    chain.add(current);
                    count++;

                    currentId = secondaryByPrimary.get(currentId);
                }
                Collections.reverse(chain);
                chains.put(lastEvent, chain);
        }
        return chains;
    }



    private String fixMalformedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
                return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            String[] parts = dateStr.split("T");
            if (parts.length == 2) {
                String datePart = parts[0];
                String timePart = parts[1];
                String[] timeParts = timePart.split(":");
                if (timeParts.length >= 2) {
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    if (hour > 23) hour = 23;
                    LocalDateTime fixedDateTime = LocalDateTime.parse(
                            datePart + "T" + String.format("%02d:%02d", hour, minute),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    );
                    return fixedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }


}
