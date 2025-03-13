package com.law.law_qa_system.models;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_legal_document_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalDocumentDetail extends  BaseEntity {
    @Column(nullable = false, unique = true)
    private String detailUrl;
    @Lob
    @Column(columnDefinition = "LONGTEXT")

    private String content;

    private String issuingAgency; // Cơ quan ban hành

    private String officialGazetteNumber; // Số công báo

    private String documentNumber; // Số hiệu

    private String publicationDate; // Ngày đăng công báo

    private String documentType; // Loại văn bản

    private String signer; // Người ký

    private String title;

    private String issuedDate; // Ngày ban hành

    private String effectiveDate; // Ngày hết hiệu lực

    private String fields; // Lĩnh vực

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String pdfUrl;

    @OneToOne
    @JoinColumn(name = "legal_document_id", referencedColumnName = "id")
    private LegalDocument legalDocument;
    public LegalDocumentDetail(String detailUrl, String content,String issuingAgency, String officialGazetteNumber, String publicationDate, String documentType, String signer, String title, String issuedDate, String documentNumber, String pdfUrl, String fields) {
        this.content = content;
        this.detailUrl = detailUrl;
        this.issuingAgency = issuingAgency;
        this.officialGazetteNumber = officialGazetteNumber;
        this.publicationDate = publicationDate;
        this.documentType = documentType;
        this.signer = signer;
        this.title = title;
        this.issuedDate = issuedDate;
        this.documentNumber = documentNumber;
        this.pdfUrl = pdfUrl;
        this.fields = fields;
    }

}
