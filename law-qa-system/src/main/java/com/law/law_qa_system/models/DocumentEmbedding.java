package com.law.law_qa_system.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_document_embedding")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEmbedding extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String embeddingJson;

    public void setEmbedding(List<Double> embedding) {
        try {
            if (embedding == null || embedding.isEmpty()) {
                this.embeddingJson = "[]";
            } else {
                this.embeddingJson = new ObjectMapper().writeValueAsString(embedding);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Double> getEmbedding() {
        try {
            if (embeddingJson == null || embeddingJson.isEmpty()) {
                return new ArrayList<>();
            }
            return new ObjectMapper().readValue(this.embeddingJson, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
