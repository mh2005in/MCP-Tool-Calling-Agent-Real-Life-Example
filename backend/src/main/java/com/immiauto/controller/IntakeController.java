package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.*;
import com.immiauto.enums.ServiceType;
import com.immiauto.service.ChecklistGeneratorService;
import com.immiauto.service.IntakeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.INTAKE_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class IntakeController {

    private final IntakeService intakeService;
    private final ChecklistGeneratorService checklistGeneratorService;

    @GetMapping(ApiPaths.INTAKE_QUESTIONS)
    public ResponseEntity<List<IntakeQuestionTemplateDto>> getQuestions(
            @RequestParam ServiceType serviceType) {
        return ResponseEntity.ok(intakeService.getQuestionsForServiceType(serviceType));
    }

    @GetMapping(ApiPaths.INTAKE_QUESTIONS_BY_SECTION)
    public ResponseEntity<List<IntakeQuestionTemplateDto>> getQuestionsBySection(
            @RequestParam ServiceType serviceType,
            @RequestParam String sectionName) {
        return ResponseEntity.ok(intakeService.getQuestionsBySection(serviceType, sectionName));
    }

    @GetMapping(ApiPaths.INTAKE_SECTIONS)
    public ResponseEntity<List<String>> getSections(@RequestParam ServiceType serviceType) {
        return ResponseEntity.ok(intakeService.getSectionsForServiceType(serviceType));
    }

    @PostMapping(ApiPaths.INTAKE_SUBMIT)
    public ResponseEntity<List<IntakeResponseDto>> submitIntake(
            @PathVariable UUID caseId,
            @Valid @RequestBody IntakeSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(intakeService.submitIntake(caseId, request));
    }

    @GetMapping(ApiPaths.INTAKE_RESPONSES)
    public ResponseEntity<List<IntakeResponseDto>> getResponses(@PathVariable UUID caseId) {
        return ResponseEntity.ok(intakeService.getResponses(caseId));
    }

    @GetMapping(ApiPaths.INTAKE_SUMMARY)
    public ResponseEntity<IntakeSummaryDto> getSummary(@PathVariable UUID caseId) {
        return ResponseEntity.ok(intakeService.getIntakeSummary(caseId));
    }

    @PatchMapping(ApiPaths.INTAKE_FLAG)
    public ResponseEntity<IntakeResponseDto> flagResponse(
            @PathVariable UUID responseId,
            @RequestParam boolean flagged) {
        return ResponseEntity.ok(intakeService.flagResponse(responseId, flagged));
    }

    @PatchMapping(ApiPaths.INTAKE_UPDATE_RESPONSE)
    public ResponseEntity<IntakeResponseDto> updateResponse(
            @PathVariable UUID responseId,
            @RequestParam String answer) {
        return ResponseEntity.ok(intakeService.updateResponse(responseId, answer));
    }

    @PostMapping(ApiPaths.INTAKE_GENERATE_CHECKLIST)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<List<ChecklistItemDto>> generateChecklist(@PathVariable UUID caseId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checklistGeneratorService.generateChecklist(caseId));
    }
}
