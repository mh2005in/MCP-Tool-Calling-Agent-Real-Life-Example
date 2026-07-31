package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.ReminderDto;
import com.immiauto.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.REMINDERS_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping(ApiPaths.REMINDERS_CREATE)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<ReminderDto> createReminder(@PathVariable UUID caseId,
                                                       @Valid @RequestBody ReminderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reminderService.createReminder(caseId, dto));
    }

    @GetMapping(ApiPaths.REMINDERS_GET)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<List<ReminderDto>> getReminders(@PathVariable UUID caseId) {
        return ResponseEntity.ok(reminderService.getRemindersForCase(caseId));
    }

    @PatchMapping(ApiPaths.REMINDERS_APPROVE)
    public ResponseEntity<ReminderDto> approveReminder(@PathVariable UUID reminderId,
                                                        @RequestParam String approvedBy) {
        return ResponseEntity.ok(reminderService.approveReminder(reminderId, approvedBy));
    }

    @GetMapping(ApiPaths.REMINDERS_PENDING)
    public ResponseEntity<List<ReminderDto>> getPendingReminders() {
        return ResponseEntity.ok(reminderService.getPendingReminders());
    }

    @GetMapping(ApiPaths.REMINDERS_PENDING_BY_CONSULTANT)
    @PreAuthorize("@consultantAccess.canAccess(#consultantId)")
    public ResponseEntity<List<ReminderDto>> getPendingRemindersByConsultant(@PathVariable UUID consultantId) {
        return ResponseEntity.ok(reminderService.getPendingRemindersByConsultant(consultantId));
    }
}
