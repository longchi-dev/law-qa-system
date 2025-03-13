package com.law.law_qa_system.models;

import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_saved_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedDocument extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "legal_document_id", referencedColumnName = "id", nullable = false)
    private LegalDocument legalDocument;

    private LocalDateTime savedAt;
}
