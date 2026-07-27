package com.immiauto.controller;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.ChecklistTemplateDto;
import com.immiauto.entity.AuditLog;
import com.immiauto.enums.ServiceType;
import com.immiauto.service.ChecklistTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TEMPLATES_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ChecklistTemplateController {

    private final ChecklistTemplateService templateService;

    @PostMapping(ApiPaths.TEMPLATES_CREATE)
    public ResponseEntity<ChecklistTemplateDto> createTemplate(
            @Valid @RequestBody ChecklistTemplateDto dto,
            @RequestParam Long consultantId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateService.createTemplate(dto, consultantId));
    }

    @PutMapping(ApiPaths.TEMPLATES_UPDATE)
    public ResponseEntity<ChecklistTemplateDto> updateTemplate(
            @PathVariable Long templateId,
            @Valid @RequestBody ChecklistTemplateDto dto,
            @RequestParam Long consultantId) {
        return ResponseEntity.ok(templateService.updateTemplate(templateId, dto, consultantId));
    }

    @PatchMapping(ApiPaths.TEMPLATES_REVIEW)
    public ResponseEntity<ChecklistTemplateDto> reviewTemplate(
            @PathVariable Long templateId,
            @RequestParam Long consultantId) {
        return ResponseEntity.ok(templateService.reviewTemplate(templateId, consultantId));
    }

    @PatchMapping(ApiPaths.TEMPLATES_APPROVE)
    public ResponseEntity<ChecklistTemplateDto> approveTemplate(
            @PathVariable Long templateId,
            @RequestParam Long consultantId) {
        return ResponseEntity.ok(templateService.approveTemplate(templateId, consultantId));
    }

    @PatchMapping(ApiPaths.TEMPLATES_REVOKE)
    public ResponseEntity<ChecklistTemplateDto> revokeApproval(
            @PathVariable Long templateId,
            @RequestParam Long consultantId) {
        return ResponseEntity.ok(templateService.revokeApproval(templateId, consultantId));
    }

    @GetMapping(ApiPaths.TEMPLATES_GET_BY_TYPE)
    public ResponseEntity<List<ChecklistTemplateDto>> getByServiceType(
            @RequestParam ServiceType serviceType) {
        return ResponseEntity.ok(templateService.getTemplatesByServiceType(serviceType));
    }

    @GetMapping(ApiPaths.TEMPLATES_GET_APPROVED)
    public ResponseEntity<List<ChecklistTemplateDto>> getApprovedByServiceType(
            @RequestParam ServiceType serviceType) {
        return ResponseEntity.ok(templateService.getApprovedTemplatesByServiceType(serviceType));
    }

    @GetMapping(ApiPaths.TEMPLATES_GET_BY_ID)
    public ResponseEntity<ChecklistTemplateDto> getTemplate(@PathVariable Long templateId) {
        return ResponseEntity.ok(templateService.getTemplate(templateId));
    }

    @GetMapping(ApiPaths.TEMPLATES_GET_ALL)
    public ResponseEntity<List<ChecklistTemplateDto>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @DeleteMapping(ApiPaths.TEMPLATES_DELETE)
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long templateId,
            @RequestParam Long consultantId) {
        templateService.deleteTemplate(templateId, consultantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPaths.TEMPLATES_AUDIT)
    public ResponseEntity<List<AuditLog>> getAuditHistory(@PathVariable Long templateId) {
        return ResponseEntity.ok(templateService.getTemplateAuditHistory(templateId));
    }
}
