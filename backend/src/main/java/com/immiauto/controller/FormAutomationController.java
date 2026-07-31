package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.dto.forms.CanonicalDataSnapshotDto;
import com.immiauto.dto.forms.CaseFormDraftDto;
import com.immiauto.dto.forms.CasePackageDto;
import com.immiauto.dto.forms.MappingPreviewDto;
import com.immiauto.dto.forms.PackageIndexDto;
import com.immiauto.dto.forms.PackageProfileSummaryDto;
import com.immiauto.dto.forms.PackageReadinessReportDto;
import com.immiauto.entity.forms.CaseFormDraft;
import com.immiauto.entity.forms.CasePackage;
import com.immiauto.service.forms.CaseFormGenerationService;
import com.immiauto.service.forms.CasePackageService;
import com.immiauto.service.forms.FormAutomationService;
import com.immiauto.service.forms.PackageValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

/**
 * Case-scoped read endpoints for the IRCC form & package automation workspace
 * (Section 4.1 - Milestone 2). Generation, validation, and package endpoints
 * are added in later milestones.
 */
@RestController
@RequestMapping("/v1/cases/{caseId}/form-automation")
@RequiredArgsConstructor
@PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
public class FormAutomationController {

    private final FormAutomationService formAutomationService;
    private final CaseFormGenerationService caseFormGenerationService;
    private final PackageValidationService packageValidationService;
    private final CasePackageService casePackageService;

    @GetMapping("/profiles")
    public List<PackageProfileSummaryDto> getAvailableProfiles(@PathVariable UUID caseId) {
        return formAutomationService.listAvailableProfiles(caseId);
    }

    @GetMapping("/canonical-snapshot")
    public CanonicalDataSnapshotDto getCanonicalSnapshot(@PathVariable UUID caseId) {
        return formAutomationService.getCanonicalSnapshot(caseId);
    }

    @GetMapping("/mapping-preview")
    public MappingPreviewDto getMappingPreview(@PathVariable UUID caseId,
                                               @RequestParam UUID packageProfileId) {
        return formAutomationService.previewMappings(caseId, packageProfileId);
    }

    @GetMapping("/readiness")
    public PackageReadinessReportDto getReadinessReport(@PathVariable UUID caseId,
                                                        @RequestParam UUID packageProfileId) {
        return packageValidationService.evaluate(caseId, packageProfileId);
    }

    // --- Draft form generation (Milestone 3) ---

    @PostMapping("/profiles/{profileId}/generate-drafts")
    public List<CaseFormDraftDto> generateDrafts(@PathVariable UUID caseId,
                                                 @PathVariable UUID profileId) {
        return caseFormGenerationService.generateDraftForms(caseId, profileId);
    }

    @GetMapping("/drafts")
    public List<CaseFormDraftDto> listDrafts(@PathVariable UUID caseId) {
        return caseFormGenerationService.listDrafts(caseId);
    }

    @GetMapping("/drafts/{draftId}")
    public CaseFormDraftDto getDraft(@PathVariable UUID caseId, @PathVariable UUID draftId) {
        return caseFormGenerationService.getFormDraft(caseId, draftId);
    }

    @PostMapping("/drafts/{draftId}/regenerate")
    public CaseFormDraftDto regenerateDraft(@PathVariable UUID caseId, @PathVariable UUID draftId) {
        return caseFormGenerationService.regenerateDraftForm(caseId, draftId);
    }

    @PostMapping(value = "/drafts/upload", consumes = "multipart/form-data")
    public CaseFormDraftDto uploadFilledForm(@PathVariable UUID caseId,
                                             @RequestParam UUID formDefinitionId,
                                             @RequestParam("file") MultipartFile file) {
        return caseFormGenerationService.uploadFilledForm(caseId, formDefinitionId, file);
    }

    @GetMapping("/drafts/{draftId}/download")
    public ResponseEntity<Resource> downloadDraft(@PathVariable UUID caseId, @PathVariable UUID draftId) {
        CaseFormDraft draft = caseFormGenerationService.getDraftForDownload(caseId, draftId);
        Resource resource = new FileSystemResource(draft.getDraftFilePath());
        String fileName = Paths.get(draft.getDraftFilePath()).getFileName().toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // --- Package assembly & approval (Milestone 5) ---

    @PostMapping("/packages")
    public CasePackageDto createOrRefreshPackage(@PathVariable UUID caseId,
                                                 @RequestParam UUID packageProfileId) {
        return casePackageService.createOrRefreshPackage(caseId, packageProfileId);
    }

    @GetMapping("/packages")
    public List<CasePackageDto> listPackages(@PathVariable UUID caseId) {
        return casePackageService.listPackages(caseId);
    }

    @GetMapping("/packages/{packageId}")
    public CasePackageDto getPackage(@PathVariable UUID caseId, @PathVariable UUID packageId) {
        return casePackageService.getPackage(caseId, packageId);
    }

    @PostMapping("/packages/{packageId}/refresh")
    public CasePackageDto refreshPackage(@PathVariable UUID caseId, @PathVariable UUID packageId) {
        return casePackageService.refreshPackage(caseId, packageId);
    }

    @GetMapping("/packages/{packageId}/readiness")
    public PackageReadinessReportDto getPackageReadiness(@PathVariable UUID caseId, @PathVariable UUID packageId) {
        return casePackageService.getReadinessReport(caseId, packageId);
    }

    @GetMapping("/packages/{packageId}/index")
    public PackageIndexDto getPackageIndex(@PathVariable UUID caseId, @PathVariable UUID packageId) {
        return casePackageService.getPackageIndex(caseId, packageId);
    }

    @PostMapping("/packages/{packageId}/issues/{issueId}/resolve")
    public PackageReadinessReportDto resolveIssue(@PathVariable UUID caseId, @PathVariable UUID packageId,
                                                  @PathVariable UUID issueId,
                                                  @RequestParam(required = false) String notes) {
        return casePackageService.resolveIssue(caseId, packageId, issueId, notes);
    }

    @PostMapping("/packages/{packageId}/approve")
    public CasePackageDto approvePackage(@PathVariable UUID caseId, @PathVariable UUID packageId,
                                         @RequestParam boolean acknowledged,
                                         @RequestParam(required = false) String notes) {
        return casePackageService.approvePackage(caseId, packageId, notes, acknowledged);
    }

    @GetMapping("/packages/{packageId}/download")
    public ResponseEntity<Resource> downloadPackage(@PathVariable UUID caseId, @PathVariable UUID packageId) {
        CasePackage pkg = casePackageService.getPackageForDownload(caseId, packageId);
        Resource resource = new FileSystemResource(pkg.getPackageZipPath());
        String fileName = Paths.get(pkg.getPackageZipPath()).getFileName().toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
