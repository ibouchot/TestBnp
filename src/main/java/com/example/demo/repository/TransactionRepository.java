package com.example.demo.repository;

import com.example.demo.model.Event;
import com.example.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.groupId = :groupId ORDER BY t.event.stepRank DESC, t.event.eventRank ASC")
    List<Transaction> findTransactionByGroupId(String groupId);
    List<Transaction> findAllByCorrectIsFalse();
    Optional<Transaction> findByPrimaryId(String primaryId);

}
