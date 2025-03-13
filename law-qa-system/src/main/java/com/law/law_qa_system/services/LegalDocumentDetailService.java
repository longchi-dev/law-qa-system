package com.law.law_qa_system.services;

import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.LegalDocumentDetail;
import com.law.law_qa_system.repositories.LegalDocumentDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalDocumentDetailService {
    @Autowired
    private LegalDocumentDetailRepository legalDocumentDetailRepository;

    public boolean existsByDetailUrl(String detailUrl) {
        return legalDocumentDetailRepository.existsByDetailUrl(detailUrl);
    }

    public void save(LegalDocumentDetail legalDocumentDetail) {
//        legalDocumentDetailRepository.save(legalDocumentDetail);
        if (!existsByDetailUrl(legalDocumentDetail.getDetailUrl())) {
            legalDocumentDetailRepository.save(legalDocumentDetail);
        } else {
            System.out.println("⚠Document already exists: " + legalDocumentDetail.getDetailUrl());
        }
    }

    public List<LegalDocumentDetail> getAllLegalDocumentDetail() {
        return legalDocumentDetailRepository.findAll();
    }

    public List<LegalDocumentDetail> getLegalDocumentDetailsByLegalDocumentId(Long id) {
        return legalDocumentDetailRepository.findByLegalDocumentId(id);
    }

    public boolean deleteLegalDocumentDetails(Long id) {
        if (legalDocumentDetailRepository.existsById(id)) {
            legalDocumentDetailRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
