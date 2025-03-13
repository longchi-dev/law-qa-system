package com.law.law_qa_system.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_document_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    private String agency; // Co quan ban hanh

    private String gazetteNumber; // so cong bao

    private LocalDateTime gazetteDate; // ngay dang cong bao

    private String applyStatus; // ap dung

    private String validityStatus; // tinh trang hieu luc

    private String field; // linh vuc
}
