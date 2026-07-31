package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.mcp.*;
import com.immiauto.entity.AppUser;
import com.immiauto.entity.Consultant;
import com.immiauto.repository.ConsultantRepository;
import com.immiauto.security.CurrentUserProvider;
import com.immiauto.service.AiBoundaryService;
import com.immiauto.service.McpDataService;
import com.immiauto.service.McpToolAuditService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.MCP_CONTROLLER)
@RequiredArgsConstructor
public class McpApiController {

    private final McpDataService mcpDataService;
    private final McpToolAuditService mcpToolAuditService;
    private final CurrentUserProvider currentUserProvider;
    private final ConsultantRepository consultantRepository;

    @GetMapping(ApiPaths.MCP_INTAKE_SUMMARY)
    public ResponseEntity<Map<String, List<MaskedIntakeResponseDto>>> getIntakeSummary(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getIntakeSummary(caseId, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_DOCUMENTS)
    public ResponseEntity<List<SanitizedDocumentDto>> getDocuments(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getDocuments(caseId, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_CHECKLIST_STATUS)
    public ResponseEntity<List<McpChecklistItemDto>> getChecklistStatus(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getChecklistStatus(caseId, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_CONSISTENCY_REPORT)
    public ResponseEntity<List<Map<String, String>>> getConsistencyReport(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getConsistencyReport(caseId, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_TIMELINE)
    public ResponseEntity<List<McpTimelineEntryDto>> getTimeline(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getTimeline(caseId, getMcpConsultant(request)));
    }

    @PostMapping(ApiPaths.MCP_CLASSIFY_DOCUMENT)
    public ResponseEntity<Map<String, String>> classifyDocument(
            @PathVariable UUID caseId,
            @Valid @RequestBody McpDocumentClassificationRequest request) {
        return ResponseEntity.ok(mcpDataService.classifyDocument(
                request.getFilename(), request.getMimeType(), request.getSize()));
    }

    @GetMapping(ApiPaths.MCP_REMINDERS_PENDING)
    public ResponseEntity<List<McpReminderDto>> getPendingReminders(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getPendingReminders(caseId, getMcpConsultant(request)));
    }

    @PostMapping(ApiPaths.MCP_VALIDATE_OUTPUT)
    public ResponseEntity<AiBoundaryService.AiBoundaryResult> validateOutput(
            @Valid @RequestBody McpValidateOutputRequest request) {
        return ResponseEntity.ok(mcpDataService.validateOutput(request.getText()));
    }

    @GetMapping(ApiPaths.MCP_CASE_OVERVIEW)
    public ResponseEntity<CaseSummaryProjection> getCaseOverview(
            @PathVariable UUID caseId, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getCaseSummary(caseId, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_CLIENTS_LIST)
    public ResponseEntity<List<MaskedClientDto>> listClients(HttpServletRequest request) {
        Consultant caller = getMcpConsultant(request);
        return ResponseEntity.ok(mcpDataService.getMaskedClients(caller));
    }

    @GetMapping(ApiPaths.MCP_CLIENT_GET)
    public ResponseEntity<MaskedClientDto> getClient(
            @PathVariable UUID clientId, HttpServletRequest request) {
        Consultant caller = getMcpConsultant(request);
        return ResponseEntity.ok(mcpDataService.getMaskedClient(clientId, caller));
    }

    @PostMapping(ApiPaths.MCP_CLIENT_CREATE)
    public ResponseEntity<MaskedClientDto> createClient(
            @Valid @RequestBody McpCreateClientRequest mcpRequest, HttpServletRequest request) {
        Consultant caller = getMcpConsultant(request);
        return ResponseEntity.ok(mcpDataService.createAndMaskClient(
                mcpRequest.getFullName(), mcpRequest.getEmail(), caller));
    }

    @GetMapping(ApiPaths.MCP_SEARCH_BY_CASE_NUMBER)
    public ResponseEntity<CaseSummaryProjection> searchByCaseNumber(
            @RequestParam String caseNumber, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getCaseSummaryByNumber(caseNumber, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_SEARCH_BY_CLIENT_NUMBER)
    public ResponseEntity<MaskedClientDto> searchByClientNumber(
            @RequestParam String clientNumber, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getMaskedClientByNumber(clientNumber, getMcpConsultant(request)));
    }

    @GetMapping(ApiPaths.MCP_SEARCH_BY_CONSULTANT_NUMBER)
    public ResponseEntity<Map<String, Object>> searchByConsultantNumber(
            @RequestParam String consultantNumber, HttpServletRequest request) {
        return ResponseEntity.ok(mcpDataService.getConsultantByNumber(consultantNumber, getMcpConsultant(request)));
    }

    @PostMapping(ApiPaths.MCP_AUDIT)
    public ResponseEntity<Void> recordAudit(
            @Valid @RequestBody McpAuditEventRequest event, HttpServletRequest request) {
        // The MCP server posts audit events using its own service-account token (no linked
        // consultant), so this endpoint does not require one - unlike the data endpoints above.
        // The acting user is captured in the event body (externalSubject). If a consultant IS
        // linked, associate the record with it.
        AppUser user = currentUserProvider.getCurrentUser();
        mcpToolAuditService.record(event, user.getConsultantId());
        return ResponseEntity.accepted().build();
    }

    /**
     * Resolves the consultant from the On-Behalf-Of user token (Phase 3) — the MCP server now
     * forwards the user's identity, so the consultant comes from the authenticated AppUser
     * instead of a shared API key.
     */
    private Consultant getMcpConsultant(HttpServletRequest request) {
        AppUser user = currentUserProvider.getCurrentUser();
        if (user.getConsultantId() == null) {
            throw new AccessDeniedException("No consultant is linked to the authenticated user");
        }
        return consultantRepository.findById(user.getConsultantId())
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found: " + user.getConsultantId()));
    }
}
