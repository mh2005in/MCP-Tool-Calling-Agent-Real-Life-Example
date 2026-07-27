package com.immiauto.service;

import com.immiauto.dto.mcp.SanitizedDocumentDto;
import com.immiauto.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentMetadataSanitizer {

    private final SensitiveFieldRegistry registry;

    public List<SanitizedDocumentDto> sanitize(List<Document> documents) {
        return documents.stream()
                .map(this::sanitizeSingle)
                .toList();
    }

    private SanitizedDocumentDto sanitizeSingle(Document doc) {
        String category = doc.getDocumentCategory();
        String type = registry.isSensitiveDocumentCategory(category)
                ? "***MASKED***"
                : doc.getDocumentType();

        return SanitizedDocumentDto.builder()
                .documentCategory(category)
                .documentType(type)
                .status(doc.getStatus())
                .expiryDate(doc.getExpiryDate())
                .mimeType(doc.getMimeType())
                .translationRequired(doc.isTranslationRequired())
                .notarizationRequired(doc.isNotarizationRequired())
                .build();
    }
}
