package com.law.law_qa_system.models;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_legal_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalDocument extends BaseEntity {
    @Column(columnDefinition = "LONGTEXT")
    private String title;

    private String detailUrl;

    private String issueDate;

    @OneToOne(mappedBy = "legalDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LegalDocumentDetail legalDocumentDetail;
}
