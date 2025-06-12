package com.example.demo.dto;

import com.example.demo.model.Event;
import com.example.demo.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Integer id;
    @JsonProperty("primary_id")
    private String primaryId;
    @JsonProperty("secondary_id")
    private String secondaryId;
    private EventDto event;
    private String date;
    private boolean correct;
    private String groupId;

    public static TransactionDto fromEntity(Transaction transaction) {
        if (transaction == null) return null;
        return new TransactionDto(
                transaction.getId(),
                transaction.getPrimaryId(),
                transaction.getSecondaryId(),
                EventDto.fromEntity(transaction.getEvent()),
                transaction.getDate(),
                transaction.isCorrect(),
                transaction.getGroupId()
        );
    }

    public static Transaction toEntity(TransactionDto dto) {
        if (dto == null) return null;
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setPrimaryId(dto.getPrimaryId());
        transaction.setSecondaryId(dto.getSecondaryId());

        Event event = EventDto.toEntity(dto.getEvent());
        transaction.setEvent(event);

        transaction.setDate(dto.getDate());
        transaction.setCorrect(dto.isCorrect());
        transaction.setGroupId(dto.getGroupId());
        return transaction;
    }
}
