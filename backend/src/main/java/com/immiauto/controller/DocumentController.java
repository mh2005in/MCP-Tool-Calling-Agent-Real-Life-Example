package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.DocumentDto;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.DOCUMENTS_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(path = ApiPaths.DOCUMENTS_UPLOAD, consumes = "multipart/form-data")
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<DocumentDto> uploadDocument(@PathVariable UUID caseId,
                                                       @RequestParam("file") MultipartFile file,
                                                       @RequestParam(required = false) String category,
                                                       @RequestParam(required = false) String type) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadDocument(caseId, file, category, type));
    }

    @GetMapping(ApiPaths.DOCUMENTS_GET)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<List<DocumentDto>> getDocuments(@PathVariable UUID caseId) {
        return ResponseEntity.ok(documentService.getDocumentsForCase(caseId));
    }

    @PatchMapping(ApiPaths.DOCUMENTS_REVIEW)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<DocumentDto> reviewDocument(@PathVariable UUID caseId,
                                                       @PathVariable UUID docId,
                                                       @RequestParam DocumentStatus status,
                                                       @RequestParam(required = false) String reviewNote,
                                                       @RequestParam(required = false) String rejectionReason) {
        return ResponseEntity.ok(documentService.reviewDocument(caseId, docId, status, reviewNote, rejectionReason));
    }
}
