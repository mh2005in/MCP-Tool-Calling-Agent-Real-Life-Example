package com.immiauto.controller;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.CaseDto;
import com.immiauto.dto.CreateCaseRequest;
import com.immiauto.enums.CaseStatus;
import com.immiauto.enums.LeadStatus;
import com.immiauto.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.CASES_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@PreAuthorize("@consultantAccess.canAccess(#consultantId)")
public class CaseController {

    private final CaseService caseService;

    @PostMapping(ApiPaths.CASES_CREATE)
    public ResponseEntity<CaseDto> createCase(@PathVariable UUID consultantId,
                                               @Valid @RequestBody CreateCaseRequest request) {
        request.setConsultantId(consultantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.createCase(request));
    }

    @GetMapping(ApiPaths.CASES_GET_BY_ID)
    public ResponseEntity<CaseDto> getCase(@PathVariable UUID consultantId,
                                            @PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCase(consultantId, id));
    }

    @GetMapping(ApiPaths.CASES_GET)
    public ResponseEntity<List<CaseDto>> getAllCases(@PathVariable UUID consultantId) {
        return ResponseEntity.ok(caseService.getCasesByConsultant(consultantId));
    }

    @PutMapping(ApiPaths.CASES_UPDATE)
    public ResponseEntity<CaseDto> updateCase(@PathVariable UUID consultantId,
                                               @PathVariable UUID id,
                                               @RequestBody CaseDto dto) {
        return ResponseEntity.ok(caseService.updateCase(consultantId, id, dto));
    }

    @PatchMapping(ApiPaths.CASES_STATUS_BY_ID)
    public ResponseEntity<CaseDto> updateCaseStatus(@PathVariable UUID consultantId,
                                                     @PathVariable UUID id,
                                                     @RequestParam CaseStatus status) {
        return ResponseEntity.ok(caseService.updateCaseStatus(consultantId, id, status));
    }

    @PatchMapping(ApiPaths.CASES_LEAD_STATUS_BY_ID)
    public ResponseEntity<CaseDto> updateLeadStatus(@PathVariable UUID consultantId,
                                                     @PathVariable UUID id,
                                                     @RequestParam LeadStatus status) {
        return ResponseEntity.ok(caseService.updateLeadStatus(consultantId, id, status));
    }

    @GetMapping(ApiPaths.CASES_SEARCH_BY_NUMBER)
    public ResponseEntity<CaseDto> getCaseByCaseNumber(@PathVariable UUID consultantId,
                                                        @RequestParam String caseNumber) {
        return ResponseEntity.ok(caseService.getCaseByCaseNumber(consultantId, caseNumber));
    }

    @GetMapping(ApiPaths.CASES_DEADLINE)
    public ResponseEntity<List<CaseDto>> getUpcomingDeadlines(@PathVariable UUID consultantId,
                                                               @RequestParam(defaultValue = "14") int daysAhead) {
        return ResponseEntity.ok(caseService.getCasesWithUpcomingDeadlines(daysAhead));
    }

    @GetMapping(ApiPaths.CASES_GET_ALL_ADMIN)
    public ResponseEntity<List<CaseDto>> getAllCasesAdmin(@PathVariable UUID consultantId) {
        return ResponseEntity.ok(caseService.getAllCasesForAdmin(consultantId));
    }

    @GetMapping(ApiPaths.CASES_GET_BY_TARGET_CONSULTANT)
    public ResponseEntity<List<CaseDto>> getCasesByTargetConsultant(
            @PathVariable UUID consultantId,
            @PathVariable UUID targetConsultantId) {
        return ResponseEntity.ok(caseService.getCasesByConsultantForAdmin(consultantId, targetConsultantId));
    }
}
