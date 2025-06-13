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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    private final JsonDataReader dataReader;
    private final TransactionService transactionService;

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,                // 2025-07-07T05:56:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH),          // 12-31-2025 13:19:45 (US)
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH),          // 31-12-2025 13:19:45 (EU)
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH),          // 12/31/2025 13:19:45 (US slash)
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
    };

    public DataLoader(JsonDataReader dataReader, TransactionService transactionService) {
        this.dataReader = dataReader;
        this.transactionService = transactionService;
    }


    @Override
    public void run(String... args) throws Exception {
        try{
            long count = transactionService.count();
            if (count == 0) {
                System.out.println("Loading transactions...");
                List<TransactionDto> transactions = loadTransactions();
                System.out.println("Transaction loaded.");
            } else {
                List<TransactionDto> transactions = dataReader.readTransactionsFromFile();
                System.out.println(transactions.get(1));
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

            for (TransactionDto transactionDto : chain) {
                boolean isCorrect = determineCorrect(transactionDto.getDate());
                transactionDto.setCorrect(isCorrect);
                if(!isCorrect) {
                    transactionDto.setPreviousDate(transactionDto.getDate());
                }
                transactionDto.setDate(fixMalformedDate(transactionDto.getDate()));
            }

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

        return transactions;
    }

    public boolean determineCorrect(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return false;

        // Try first with formatters
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDateTime dt = LocalDateTime.parse(dateStr, formatter);
                dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return true;
            } catch (DateTimeParseException ignored) {}
        }
        return false;
    }
    public Map<String, List<TransactionDto>> determineEventRank(List<TransactionDto> transactions) {
        Map<String, List<TransactionDto>> chains = new HashMap<>();

        Map<String, TransactionDto> bySecondaryId = new HashMap<>();
        for (TransactionDto transactionDto : transactions) {
            bySecondaryId.put(transactionDto.getSecondaryId(), transactionDto);
        }

        List<TransactionDto> roots = transactions.stream()
                .filter(transaction -> "CBAcquired".equals(transaction.getEvent().getEventType()))
                .collect(Collectors.toList());

        for (TransactionDto root : roots) {
            List<TransactionDto> chain = new ArrayList<>();
            TransactionDto current = root;
            int count = 0;
            if (current !=null){
                String currentId = current.getPrimaryId();

                while (current != null && count < 20) {
                    chain.add(current);
                    String currentPrimaryId = current.getPrimaryId();
                    current = bySecondaryId.get(currentPrimaryId);
                    count++;
                }

            chains.put(currentId, chain);
            }
        }

        return chains;
    }




    public String fixMalformedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        Pattern pattern = Pattern.compile("^(\\d{4})-(\\d{2,})-(\\d{2,})T(\\d{2,}):(\\d{2})(?::(\\d{2}))?$");
        Matcher matcher = pattern.matcher(dateStr);

        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            int hour = Integer.parseInt(matcher.group(4));
            int minute = Integer.parseInt(matcher.group(5));
            int second = matcher.group(6) != null ? Integer.parseInt(matcher.group(6)) : 0;

            if (month > 12) {
                year += (month - 1) / 12;
                month = (month - 1) % 12 + 1;
            }

            try {
                LocalDate date = LocalDate.of(year, 1, 1)
                        .plusMonths(month - 1L)
                        .plusDays(day - 1L);
                date = date.plusDays(hour / 24);
                hour = hour % 24;

                LocalDateTime fixedDateTime = date.atTime(hour, minute, second);

                //System.out.println("dateStr" + dateStr);
                //System.out.println("fixedDateTime "+ fixedDateTime);

                return fixedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeException e) {
                return dateStr;
            }
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {}

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDateTime dt = LocalDateTime.parse(dateStr, formatter);
                return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ignored) {}
        }

        return dateStr;
    }


}
