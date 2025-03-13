package com.law.law_qa_system.models;

import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory extends BaseEntity {
    private String query;
    private LocalDateTime searchTime;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
