package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.DocumentDto;
import com.immiauto.entity.Document;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.mapper.DocumentMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.DocumentRepository;
import com.immiauto.util.CommonUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;
    private final DocumentMapper documentMapper;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size-bytes:20971520}")
    private long maxFileSizeBytes;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "jpg", "jpeg", "png", "tif", "tiff", "bmp",
            "doc", "docx", "xls", "xlsx", "txt", "rtf", "odt", "ods"
    );

    @Transactional
    public DocumentDto uploadDocument(UUID caseId, MultipartFile file,
                                       String documentCategory, String documentType) throws IOException {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        CommonUtil.validateUpload(file, ALLOWED_EXTENSIONS, maxFileSizeBytes);

        String caseFolderName = imCase.getCaseNumber();
        Path caseDir = Paths.get(uploadDir, caseFolderName);
        Files.createDirectories(caseDir);

        String sanitizedName = CommonUtil.sanitizeFilename(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + sanitizedName;
        Path filePath = caseDir.resolve(storedName);
        Files.copy(file.getInputStream(), filePath);

        String detectedMime = CommonUtil.detectMimeType(file.getOriginalFilename());

        Document doc = Document.builder()
                .immigrationCase(imCase)
                .originalFileName(sanitizedName)
                .storedFileName(storedName)
                .filePath(filePath.toString())
                .mimeType(detectedMime)
                .fileSizeBytes(file.getSize())
                .documentCategory(documentCategory)
                .documentType(documentType)
                .status(DocumentStatus.UPLOADED)
                .build();

        return documentMapper.toDto(documentRepository.save(doc));
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> getDocumentsForCase(UUID caseId) {
        return documentRepository.findByImmigrationCaseId(caseId)
                .stream().map(documentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public DocumentDto reviewDocument(UUID caseId, UUID docId, DocumentStatus status,
                                       String reviewNote, String rejectionReason) {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        if (!doc.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Document not found");
        }
        doc.setStatus(status);
        if (reviewNote != null) doc.setReviewNote(reviewNote);
        if (rejectionReason != null) doc.setRejectionReason(rejectionReason);
        return documentMapper.toDto(documentRepository.save(doc));
    }
}
