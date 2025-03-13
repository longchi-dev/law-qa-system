package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubcriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findTopByUserIdOrderByTokenDesc(Long userId);
    List<SubscriptionPlan> findByUserId(Long userId);
}
