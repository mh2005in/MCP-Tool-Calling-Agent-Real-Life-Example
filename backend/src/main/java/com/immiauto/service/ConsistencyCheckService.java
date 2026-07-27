package com.immiauto.service;

import com.immiauto.entity.ChecklistItem;
import com.immiauto.entity.Document;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.repository.ChecklistItemRepository;
import com.immiauto.repository.DocumentRepository;
import com.immiauto.repository.CaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsistencyCheckService {

    private final DocumentRepository documentRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CaseRepository caseRepository;

    @Transactional(readOnly = true)
    public List<Map<String, String>> checkConsistency(Long caseId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        List<Map<String, String>> issues = new ArrayList<>();
        List<Document> docs = documentRepository.findByImmigrationCaseId(caseId);

        checkExpiredDocuments(docs, issues);
        checkMissingRequiredItems(caseId, issues);
        checkDuplicateDocumentTypes(docs, issues);

        return issues;
    }

    private void checkExpiredDocuments(List<Document> docs, List<Map<String, String>> issues) {
        LocalDate today = LocalDate.now();
        for (Document doc : docs) {
            if (doc.getExpiryDate() != null && doc.getExpiryDate().isBefore(today)) {
                issues.add(Map.of(
                        "type", "EXPIRED_DOCUMENT",
                        "severity", "CRITICAL",
                        "description", "Document '" + doc.getOriginalFileName() + "' expired on " + doc.getExpiryDate(),
                        "documentId", doc.getId().toString()
                ));
            }
        }
    }

    private void checkMissingRequiredItems(Long caseId, List<Map<String, String>> issues) {
        List<ChecklistItem> missing = checklistItemRepository
                .findByImmigrationCaseIdAndStatus(caseId, DocumentStatus.NOT_UPLOADED);
        long requiredMissing = missing.stream().filter(ChecklistItem::isRequired).count();
        if (requiredMissing > 0) {
            issues.add(Map.of(
                    "type", "MISSING_REQUIRED_DOCUMENTS",
                    "severity", "WARNING",
                    "description", requiredMissing + " required document(s) still not uploaded"
            ));
        }
    }

    private void checkDuplicateDocumentTypes(List<Document> docs, List<Map<String, String>> issues) {
        Map<String, Long> typeCounts = docs.stream()
                .filter(d -> d.getDocumentType() != null)
                .collect(Collectors.groupingBy(Document::getDocumentType, Collectors.counting()));
        typeCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .forEach(e -> issues.add(Map.of(
                        "type", "DUPLICATE_DOCUMENT",
                        "severity", "INFO",
                        "description", "Multiple uploads of type '" + e.getKey() + "' (" + e.getValue() + " files)"
                )));
    }
}
