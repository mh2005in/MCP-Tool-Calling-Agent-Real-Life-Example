package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.ConsultantDto;
import com.immiauto.service.ConsultantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CONSULTANTS_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ConsultantController {

    private final ConsultantService consultantService;

    @PostMapping(ApiPaths.CONSULTANTS_CREATE)
    @PreAuthorize("@adminGuard.isAdminConsultant()")
    public ResponseEntity<ConsultantDto> createConsultant(@Valid @RequestBody ConsultantDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultantService.createConsultant(dto));
    }

    @GetMapping(ApiPaths.CONSULTANTS_GET)
    @PreAuthorize("@adminGuard.isAdminConsultant()")
    public ResponseEntity<List<ConsultantDto>> getAllConsultants() {
        return ResponseEntity.ok(consultantService.getAllConsultants());
    }

    @GetMapping(ApiPaths.CONSULTANTS_GET_BY_ID)
    public ResponseEntity<ConsultantDto> getConsultant(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getConsultant(id));
    }

    @GetMapping(ApiPaths.CONSULTANTS_PROFILE)
    public ResponseEntity<ConsultantDto> getOwnProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(consultantService.getOwnProfile(id));
    }

    @GetMapping(ApiPaths.CONSULTANTS_SEARCH_BY_NUMBER)
    public ResponseEntity<ConsultantDto> getConsultantByNumber(@RequestParam String consultantNumber) {
        return ResponseEntity.ok(consultantService.getConsultantByNumber(consultantNumber));
    }

    @PutMapping(ApiPaths.CONSULTANTS_UPDATE)
    public ResponseEntity<ConsultantDto> updateConsultant(@PathVariable UUID id,
                                                          @RequestBody ConsultantDto dto) {
        return ResponseEntity.ok(consultantService.updateConsultant(id, dto));
    }

    /** Enable/disable a consultant's login (admin only). */
    @PatchMapping(ApiPaths.CONSULTANTS_SET_ACTIVE)
    @PreAuthorize("@adminGuard.isAdminConsultant()")
    public ResponseEntity<ConsultantDto> setActive(@PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(consultantService.setActive(id, active));
    }
}
