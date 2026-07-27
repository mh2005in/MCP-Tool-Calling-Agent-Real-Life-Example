package com.immiauto.controller;

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
    public TravelHistoryDto addTravelEntry(@PathVariable Long caseId,
                                            @Valid @RequestBody TravelHistoryDto dto) {
        return travelHistoryService.addEntry(caseId, dto);
    }

    @GetMapping("/travel-history")
    public List<TravelHistoryDto> getTravelHistory(@PathVariable Long caseId) {
        return travelHistoryService.getEntries(caseId);
    }

    @PutMapping("/travel-history/{entryId}")
    public TravelHistoryDto updateTravelEntry(@PathVariable Long caseId,
                                               @PathVariable Long entryId,
                                               @Valid @RequestBody TravelHistoryDto dto) {
        return travelHistoryService.updateEntry(caseId, entryId, dto);
    }

    @DeleteMapping("/travel-history/{entryId}")
    public ResponseEntity<Void> deleteTravelEntry(@PathVariable Long caseId,
                                                    @PathVariable Long entryId) {
        travelHistoryService.deleteEntry(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/physical-presence")
    public Map<String, Object> getPhysicalPresence(@PathVariable Long caseId) {
        return travelHistoryService.getPhysicalPresenceSummary(caseId);
    }

    // --- Work History (7.3, 7.5) ---

    @PostMapping("/work-history")
    public WorkHistoryDto addWorkEntry(@PathVariable Long caseId,
                                       @Valid @RequestBody WorkHistoryDto dto) {
        return workHistoryService.addEntry(caseId, dto);
    }

    @GetMapping("/work-history")
    public List<WorkHistoryDto> getWorkHistory(@PathVariable Long caseId) {
        return workHistoryService.getEntries(caseId);
    }

    @PutMapping("/work-history/{entryId}")
    public WorkHistoryDto updateWorkEntry(@PathVariable Long caseId,
                                           @PathVariable Long entryId,
                                           @Valid @RequestBody WorkHistoryDto dto) {
        return workHistoryService.updateEntry(caseId, entryId, dto);
    }

    @DeleteMapping("/work-history/{entryId}")
    public ResponseEntity<Void> deleteWorkEntry(@PathVariable Long caseId,
                                                 @PathVariable Long entryId) {
        workHistoryService.deleteEntry(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    // --- Relationship Timeline (7.1) ---

    @PostMapping("/relationship-timeline")
    public RelationshipTimelineDto addTimelineEntry(@PathVariable Long caseId,
                                                     @Valid @RequestBody RelationshipTimelineDto dto) {
        return relationshipTimelineService.addEntry(caseId, dto);
    }

    @GetMapping("/relationship-timeline")
    public List<RelationshipTimelineDto> getTimeline(@PathVariable Long caseId) {
        return relationshipTimelineService.getEntries(caseId);
    }

    @PutMapping("/relationship-timeline/{entryId}")
    public RelationshipTimelineDto updateTimelineEntry(@PathVariable Long caseId,
                                                        @PathVariable Long entryId,
                                                        @Valid @RequestBody RelationshipTimelineDto dto) {
        return relationshipTimelineService.updateEntry(caseId, entryId, dto);
    }

    @DeleteMapping("/relationship-timeline/{entryId}")
    public ResponseEntity<Void> deleteTimelineEntry(@PathVariable Long caseId,
                                                     @PathVariable Long entryId) {
        relationshipTimelineService.deleteEntry(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    // --- Recruitment Evidence (7.6) ---

    @PostMapping("/recruitment-evidence")
    public RecruitmentEvidenceDto addEvidence(@PathVariable Long caseId,
                                              @Valid @RequestBody RecruitmentEvidenceDto dto) {
        return recruitmentService.addEvidence(caseId, dto);
    }

    @GetMapping("/recruitment-evidence")
    public List<RecruitmentEvidenceDto> getEvidence(@PathVariable Long caseId) {
        return recruitmentService.getEvidence(caseId);
    }

    @DeleteMapping("/recruitment-evidence/{entryId}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long caseId,
                                                @PathVariable Long entryId) {
        recruitmentService.deleteEvidence(caseId, entryId);
        return ResponseEntity.noContent().build();
    }

    // --- Candidate Comparison (7.7) ---

    @PostMapping("/candidates")
    public CandidateComparisonDto addCandidate(@PathVariable Long caseId,
                                                @Valid @RequestBody CandidateComparisonDto dto) {
        return recruitmentService.addCandidate(caseId, dto);
    }

    @GetMapping("/candidates")
    public List<CandidateComparisonDto> getCandidates(@PathVariable Long caseId) {
        return recruitmentService.getCandidates(caseId);
    }

    @DeleteMapping("/candidates/{candidateId}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long caseId,
                                                 @PathVariable Long candidateId) {
        recruitmentService.deleteCandidate(caseId, candidateId);
        return ResponseEntity.noContent().build();
    }
}
