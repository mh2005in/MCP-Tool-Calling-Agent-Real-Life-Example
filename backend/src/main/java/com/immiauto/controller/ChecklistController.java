package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.ChecklistItemDto;
import com.immiauto.dto.ClientChecklistResponse;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CHECKLIST_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ChecklistController {

    private final ChecklistService checklistService;

    @PostMapping(ApiPaths.CHECKLIST_CREATE)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<ChecklistItemDto> addItem(@PathVariable UUID caseId,
                                                     @Valid @RequestBody ChecklistItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(checklistService.addItem(caseId, dto));
    }

    @GetMapping(ApiPaths.CHECKLIST_GET)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<List<ChecklistItemDto>> getChecklist(@PathVariable UUID caseId) {
        return ResponseEntity.ok(checklistService.getChecklistForCase(caseId));
    }

    @GetMapping(ApiPaths.CHECKLIST_MISSING)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<List<ChecklistItemDto>> getMissingItems(@PathVariable UUID caseId) {
        return ResponseEntity.ok(checklistService.getMissingItems(caseId));
    }

    @PatchMapping(ApiPaths.CHECKLIST_UPDATE_STATUS)
    @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")
    public ResponseEntity<ChecklistItemDto> updateStatus(@PathVariable UUID caseId,
                                                          @PathVariable UUID itemId,
                                                          @RequestParam DocumentStatus status,
                                                          @RequestParam(required = false) String reviewNote) {
        return ResponseEntity.ok(checklistService.updateItemStatus(itemId, status, reviewNote));
    }

    @DeleteMapping(ApiPaths.CHECKLIST_DELETE)
    @PreAuthorize("@consultantAccess.canAccessChecklistItem(#itemId)")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID caseId, @PathVariable UUID itemId) {
        checklistService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPaths.CHECKLIST_CLIENT_VIEW)
    public ResponseEntity<ClientChecklistResponse> getClientChecklist(@PathVariable UUID caseId) {
        return ResponseEntity.ok(checklistService.getClientChecklist(caseId));
    }
}
