package com.law.law_qa_system.models;

import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    private EnumTypes.TransactionType type;

    private long amount;

    private String description;
    private String bankCode;
    private String status;
}
