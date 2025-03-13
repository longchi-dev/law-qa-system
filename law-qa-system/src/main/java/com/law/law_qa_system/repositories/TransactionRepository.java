package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
