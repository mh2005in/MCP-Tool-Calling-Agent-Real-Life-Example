package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.dto.*;
import com.immiauto.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases/{caseId}")
@RequiredArgsConstructor
@PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
public class WorkflowController {

    private final TravelHistoryService travelHistoryService;
    private final WorkHistoryService workHistoryService;
    private final RelationshipTimelineService relationshipTimelineService;
    private final RecruitmentService recruitmentService;

    // --- Travel History (7.4, 5.12) ---

    @PostMapping("/travel-history")
    public TravelHistoryDto addTravelEntry(@PathVariable UUID caseId,
                                            @Valid @RequestBody TravelHistoryDto dto) {
        return travelHistoryService.addEntry(caseId, dto);
    }

    @GetMapping("/travel-history")
    public List<TravelHistoryDto> getTravelHistory(@PathVariable UUID caseId) {
        return travelHistoryService.getEntries(caseId);
    }

    @PutMapping("/travel-history/{entryId}")
    public TravelHistoryDto updateTravelEntry(@PathVariable UUID caseId,
                                               @PathVariable UUID entryId,
                                               @Valid @RequestBody TravelHistoryDto dto) {
        return travelHistoryService.updateEntry(caseId, entryId, dto);
    }

    @DeleteMapping("/travel-history/{entryId}")
    public ResponseEntity<Void> deleteTravelEntry(@PathVariable UUID caseId,
                                                    @PathVariable UUID entryId) {
        travelHistoryService.deleteEntry(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/physical-presence")
    public Map<String, Object> getPhysicalPresence(@PathVariable UUID caseId) {
        return travelHistoryService.getPhysicalPresenceSummary(caseId);
    }

    // --- Work History (7.3, 7.5) ---

    @PostMapping("/work-history")
    public WorkHistoryDto addWorkEntry(@PathVariable UUID caseId,
                                       @Valid @RequestBody WorkHistoryDto dto) {
        return workHistoryService.addEntry(caseId, dto);
    }

    @GetMapping("/work-history")
    public List<WorkHistoryDto> getWorkHistory(@PathVariable UUID caseId) {
        return workHistoryService.getEntries(caseId);
    }

    @PutMapping("/work-history/{entryId}")
    public WorkHistoryDto updateWorkEntry(@PathVariable UUID caseId,
                                           @PathVariable UUID entryId,
                                           @Valid @RequestBody WorkHistoryDto dto) {
        return workHistoryService.updateEntry(caseId, entryId, dto);
    }

    @DeleteMapping("/work-history/{entryId}")
    public ResponseEntity<Void> deleteWorkEntry(@PathVariable UUID caseId,
                                                 @PathVariable UUID entryId) {
        workHistoryService.deleteEntry(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    // --- Relationship Timeline (7.1) ---

    @PostMapping("/relationship-timeline")
    public RelationshipTimelineDto addTimelineEntry(@PathVariable UUID caseId,
                                                     @Valid @RequestBody RelationshipTimelineDto dto) {
        return relationshipTimelineService.addEntry(caseId, dto);
    }

    @GetMapping("/relationship-timeline")
    public List<RelationshipTimelineDto> getTimeline(@PathVariable UUID caseId) {
        return relationshipTimelineService.getEntries(caseId);
    }

    @PutMapping("/relationship-timeline/{entryId}")
    public RelationshipTimelineDto updateTimelineEntry(@PathVariable UUID caseId,
                                                        @PathVariable UUID entryId,
                                                        @Valid @RequestBody RelationshipTimelineDto dto) {
        return relationshipTimelineService.updateEntry(caseId, entryId, dto);
    }

    @DeleteMapping("/relationship-timeline/{entryId}")
    public ResponseEntity<Void> deleteTimelineEntry(@PathVariable UUID caseId,
                                                     @PathVariable UUID entryId) {
        relationshipTimelineService.deleteEntry(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    // --- Recruitment Evidence (7.6) ---

    @PostMapping("/recruitment-evidence")
    public RecruitmentEvidenceDto addEvidence(@PathVariable UUID caseId,
                                              @Valid @RequestBody RecruitmentEvidenceDto dto) {
        return recruitmentService.addEvidence(caseId, dto);
    }

    @GetMapping("/recruitment-evidence")
    public List<RecruitmentEvidenceDto> getEvidence(@PathVariable UUID caseId) {
        return recruitmentService.getEvidence(caseId);
    }

    @DeleteMapping("/recruitment-evidence/{entryId}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable UUID caseId,
                                                @PathVariable UUID entryId) {
        recruitmentService.deleteEvidence(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    // --- Candidate Comparison (7.7) ---

    @PostMapping("/candidates")
    public CandidateComparisonDto addCandidate(@PathVariable UUID caseId,
                                                @Valid @RequestBody CandidateComparisonDto dto) {
        return recruitmentService.addCandidate(caseId, dto);
    }

    @GetMapping("/candidates")
    public List<CandidateComparisonDto> getCandidates(@PathVariable UUID caseId) {
        return recruitmentService.getCandidates(caseId);
    }

    @DeleteMapping("/candidates/{candidateId}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID caseId,
                                                 @PathVariable UUID candidateId) {
        recruitmentService.deleteCandidate(caseId, candidateId);
        return ResponseEntity.noContent().build();
    }
}
