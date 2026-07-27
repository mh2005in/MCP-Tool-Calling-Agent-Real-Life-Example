package com.immiauto.controller;

import com.immiauto.dto.forms.*;
import com.immiauto.service.forms.FormCatalogueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin form-catalogue governance (Section 4.1 - Milestone 6): form definitions,
 * source-PDF inspection, mapping approval, and package profiles. Admin-only.
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@PreAuthorize("@adminGuard.isAdminConsultant()")
public class FormCatalogueController {

    private final FormCatalogueService formCatalogueService;

    // --- Form definitions ---

    @GetMapping("/forms")
    public List<FormDefinitionDto> listForms() {
        return formCatalogueService.listForms();
    }

    @PostMapping("/forms")
    public FormDefinitionDto createForm(@Valid @RequestBody FormDefinitionDto dto) {
        return formCatalogueService.createForm(dto);
    }

    @GetMapping("/forms/{formId}")
    public FormDefinitionDto getForm(@PathVariable Long formId) {
        return formCatalogueService.getForm(formId);
    }

    @GetMapping("/forms/{formId}/fields")
    public List<FormFieldDefinitionDto> getFields(@PathVariable Long formId) {
        return formCatalogueService.getFields(formId);
    }

    @PostMapping("/forms/{formId}/inspect")
    public FormInspectionResultDto inspectForm(@PathVariable Long formId) {
        return formCatalogueService.inspectForm(formId);
    }

    @GetMapping("/forms/{formId}/mappings")
    public List<FormMappingVersionDto> listMappings(@PathVariable Long formId) {
        return formCatalogueService.listMappings(formId);
    }

    @PostMapping("/forms/{formId}/mappings/{mappingVersionId}/approve")
    public FormMappingVersionDto approveMapping(@PathVariable Long formId, @PathVariable Long mappingVersionId) {
        return formCatalogueService.approveMappingVersion(formId, mappingVersionId);
    }

    // --- Package profiles ---

    @GetMapping("/package-profiles")
    public List<PackageProfileDto> listProfiles() {
        return formCatalogueService.listProfiles();
    }

    @PostMapping("/package-profiles")
    public PackageProfileDto createProfile(@Valid @RequestBody PackageProfileDto dto) {
        return formCatalogueService.createProfile(dto);
    }

    @PutMapping("/package-profiles/{profileId}")
    public PackageProfileDto updateProfile(@PathVariable Long profileId, @RequestBody PackageProfileDto dto) {
        return formCatalogueService.updateProfile(profileId, dto);
    }
}
