package com.law.law_qa_system.models;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "tbl_subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer token;

    @Column(nullable = false)
    private Double price;

    @Column
    private String packageName;

    @Column(nullable = false)
    private LocalDateTime transactionDate;
}
