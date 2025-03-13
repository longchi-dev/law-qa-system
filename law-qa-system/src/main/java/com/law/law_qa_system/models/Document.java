package com.law.law_qa_system.models;
import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tbl_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseEntity {
    @Column(columnDefinition = "LONGTEXT")
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content; // trich yeu

    private String keywords;

    private String sourceUrl;

    @NotBlank(message = "Please input author")
    private String author;

    @NotBlank(message = "Please input document number")
    private String number; // so hieu (vd: 123/2024/NĐ-CP)

    private String signer; // nguoi ky

    private LocalDateTime issueDate; // ngay ban hanh

    private LocalDateTime effectiveDate; // ngay hieu luc

    private LocalDateTime updateDate; // ngay cap nhat

    private String type;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String pdfContent;

    private boolean isFeatured = false;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @Builder.Default
    private List<DocumentDetails> documentDetails = new ArrayList<>();
}
