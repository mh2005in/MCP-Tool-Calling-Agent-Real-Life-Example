package com.immiauto.service.forms;

import java.util.UUID;

import com.immiauto.dto.forms.*;
import com.immiauto.entity.AppUser;
import com.immiauto.entity.forms.*;
import com.immiauto.enums.*;
import com.immiauto.mapper.FormCatalogueMapper;
import com.immiauto.repository.forms.*;
import com.immiauto.security.CurrentUserProvider;
import com.immiauto.service.CommonService;
import com.immiauto.service.forms.pdf.PdfFieldInfo;
import com.immiauto.service.forms.pdf.PdfFormEngine;
import com.immiauto.service.forms.pdf.PdfInspectionResult;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Governed form catalogue admin (Section 4.1 - Milestone 6): form definitions,
 * source-PDF inspection + field sync, package profiles, and mapping approval.
 * Inspection sets supportsFill/status (BLOCKED for XFA), which drives whether
 * the workspace offers auto-fill or manual upload.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FormCatalogueService {

    private final FormDefinitionRepository formDefinitionRepository;
    private final FormFieldDefinitionRepository fieldDefinitionRepository;
    private final FormMappingVersionRepository mappingVersionRepository;
    private final PackageProfileRepository packageProfileRepository;
    private final PackageProfileFormRepository packageProfileFormRepository;
    private final PackageDocumentRequirementRepository documentRequirementRepository;
    private final FormStorageService storage;
    private final PdfFormEngine pdfFormEngine;
    private final FormCatalogueMapper mapper;
    private final CommonService commonService;
    private final CurrentUserProvider currentUserProvider;

    // ---------------- form definitions ----------------

    @Transactional(readOnly = true)
    public List<FormDefinitionDto> listForms() {
        return formDefinitionRepository.findAll().stream().map(this::toFormDto).toList();
    }

    @Transactional(readOnly = true)
    public FormDefinitionDto getForm(UUID formId) {
        return toFormDto(requireForm(formId));
    }

    @Transactional
    public FormDefinitionDto createForm(FormDefinitionDto dto) {
        FormDefinition form = FormDefinition.builder()
                .formCode(dto.getFormCode())
                .displayName(dto.getDisplayName())
                .jurisdiction(parseEnum(Jurisdiction.class, dto.getJurisdiction(), Jurisdiction.FEDERAL))
                .programCategory(dto.getProgramCategory())
                .sourceUrl(dto.getSourceUrl())
                .sourceFileName(dto.getSourceFileName())
                .sourceSha256(dto.getSourceSha256())
                .effectiveDate(dto.getEffectiveDate())
                .retirementDate(dto.getRetirementDate())
                .editionLabel(dto.getEditionLabel())
                .supportsFill(dto.isSupportsFill())
                .supportsBarcode(dto.isSupportsBarcode())
                .status(parseEnum(FormStatus.class, dto.getStatus(), FormStatus.DRAFT))
                .notes(dto.getNotes())
                .build();
        form = formDefinitionRepository.save(form);
        commonService.logAudit("FormDefinition", form.getId(), "FORM_CREATED", form.getFormCode());
        return toFormDto(form);
    }

    @Transactional(readOnly = true)
    public List<FormFieldDefinitionDto> getFields(UUID formId) {
        requireForm(formId);
        return fieldDefinitionRepository.findByFormDefinitionIdOrderByPageNumber(formId)
                .stream().map(mapper::toDto).toList();
    }

    /**
     * Inspect the form's governed source PDF, sync its field definitions, stamp the
     * source SHA-256, and classify (STANDARD_ACROFORM enables fill; DYNAMIC_XFA is BLOCKED).
     */
    @Transactional
    public FormInspectionResultDto inspectForm(UUID formId) {
        FormDefinition form = requireForm(formId);
        Path source = storage.sourcePdf(form.getFormCode(), form.getEditionLabel());
        if (!Files.exists(source)) {
            throw new IllegalStateException("No source PDF registered at " + source
                    + " - place the governed PDF there before inspecting.");
        }

        PdfInspectionResult result;
        try {
            result = pdfFormEngine.inspect(source);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to inspect " + form.getFormCode() + ": " + e.getMessage(), e);
        }

        for (PdfFieldInfo fi : result.getFields()) {
            FormFieldDefinition def = fieldDefinitionRepository
                    .findByFormDefinitionIdAndPdfFieldName(formId, fi.getPdfFieldName())
                    .orElseGet(() -> FormFieldDefinition.builder()
                            .formDefinition(form).pdfFieldName(fi.getPdfFieldName()).build());
            def.setFieldType(fi.getFieldType());
            def.setRequired(fi.isRequired());
            def.setReadOnly(fi.isReadOnly());
            def.setMaxLength(fi.getMaxLength());
            def.setPageNumber(fi.getPageNumber());
            def.setAllowedValues(fi.getAllowedValues());
            fieldDefinitionRepository.save(def);
        }

        String classification;
        boolean supportsFill;
        FormStatus status;
        String message;
        if (result.getFields().isEmpty()) {
            classification = "NO_FORM_FIELDS";
            supportsFill = false;
            status = FormStatus.BLOCKED;
            message = "No fillable form fields found; manual upload required.";
        } else if (result.isHasXfa()) {
            classification = "DYNAMIC_XFA";
            supportsFill = false;
            status = FormStatus.BLOCKED;
            message = "Dynamic XFA form - PDFBox cannot fill; manual upload required.";
        } else {
            classification = "STANDARD_ACROFORM";
            supportsFill = true;
            status = FormStatus.ACTIVE;
            message = "Standard AcroForm - auto-fill enabled.";
        }

        form.setSourceSha256(result.getSha256());
        form.setSupportsBarcode(form.isSupportsBarcode());
        form.setSupportsFill(supportsFill);
        form.setStatus(status);
        form.setNotes(message);
        formDefinitionRepository.save(form);

        commonService.logAudit("FormDefinition", form.getId(), "FORM_INSPECTED",
                classification + " fields=" + result.getFields().size() + " sha256=" + result.getSha256());

        return FormInspectionResultDto.builder()
                .formId(form.getId()).formCode(form.getFormCode())
                .hasAcroForm(result.isHasAcroForm()).hasXfa(result.isHasXfa())
                .pageCount(result.getPageCount()).fieldCount(result.getFields().size())
                .sha256(result.getSha256()).classification(classification)
                .supportsFill(supportsFill).status(status.name()).message(message)
                .build();
    }

    // ---------------- mappings ----------------

    @Transactional(readOnly = true)
    public List<FormMappingVersionDto> listMappings(UUID formId) {
        requireForm(formId);
        return mappingVersionRepository.findByFormDefinitionIdOrderByMappingVersionDesc(formId)
                .stream().map(mapper::toDto).toList();
    }

    @Transactional
    public FormMappingVersionDto approveMappingVersion(UUID formId, UUID mappingVersionId) {
        FormMappingVersion mv = mappingVersionRepository.findById(mappingVersionId)
                .orElseThrow(() -> new EntityNotFoundException("Mapping version not found: " + mappingVersionId));
        if (!mv.getFormDefinition().getId().equals(formId)) {
            throw new EntityNotFoundException("Mapping version not found: " + mappingVersionId);
        }
        // Retire any currently-approved version for this form.
        for (FormMappingVersion other : mappingVersionRepository.findByFormDefinitionIdOrderByMappingVersionDesc(formId)) {
            if (other.getStatus() == MappingStatus.APPROVED && !other.getId().equals(mv.getId())) {
                other.setStatus(MappingStatus.RETIRED);
                mappingVersionRepository.save(other);
            }
        }
        AppUser user = currentUser();
        mv.setStatus(MappingStatus.APPROVED);
        mv.setApprovedAt(LocalDateTime.now());
        if (user != null) {
            mv.setApprovedByConsultantId(user.getConsultantId());
            mv.setApprovedByConsultantName(StringUtils.hasText(user.getDisplayName()) ? user.getDisplayName() : user.getEmail());
        }
        mv = mappingVersionRepository.save(mv);
        commonService.logAudit("FormMappingVersion", mv.getId(), "MAPPING_APPROVED",
                "form=" + formId + " v" + mv.getMappingVersion());
        return mapper.toDto(mv);
    }

    // ---------------- package profiles ----------------

    @Transactional(readOnly = true)
    public List<PackageProfileDto> listProfiles() {
        return packageProfileRepository.findAll().stream().map(this::toProfileDto).toList();
    }

    @Transactional
    public PackageProfileDto createProfile(PackageProfileDto dto) {
        PackageProfile profile = PackageProfile.builder()
                .profileCode(dto.getProfileCode())
                .displayName(dto.getDisplayName())
                .serviceType(parseNullableEnum(ServiceType.class, dto.getServiceType()))
                .caseSubtype(parseNullableEnum(CaseSubtype.class, dto.getCaseSubtype()))
                .jurisdiction(parseNullableEnum(Jurisdiction.class, dto.getJurisdiction()))
                .status(parseEnum(FormStatus.class, dto.getStatus(), FormStatus.DRAFT))
                .description(dto.getDescription())
                .effectiveDate(dto.getEffectiveDate())
                .retirementDate(dto.getRetirementDate())
                .build();
        profile = packageProfileRepository.save(profile);
        commonService.logAudit("PackageProfile", profile.getId(), "PROFILE_CREATED", profile.getProfileCode());
        return toProfileDto(profile);
    }

    @Transactional
    public PackageProfileDto updateProfile(UUID profileId, PackageProfileDto dto) {
        PackageProfile profile = packageProfileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Package profile not found: " + profileId));
        if (StringUtils.hasText(dto.getDisplayName())) profile.setDisplayName(dto.getDisplayName());
        if (dto.getServiceType() != null) profile.setServiceType(parseNullableEnum(ServiceType.class, dto.getServiceType()));
        if (dto.getCaseSubtype() != null) profile.setCaseSubtype(parseNullableEnum(CaseSubtype.class, dto.getCaseSubtype()));
        if (dto.getJurisdiction() != null) profile.setJurisdiction(parseNullableEnum(Jurisdiction.class, dto.getJurisdiction()));
        if (StringUtils.hasText(dto.getStatus())) profile.setStatus(parseEnum(FormStatus.class, dto.getStatus(), profile.getStatus()));
        if (dto.getDescription() != null) profile.setDescription(dto.getDescription());
        if (dto.getEffectiveDate() != null) profile.setEffectiveDate(dto.getEffectiveDate());
        if (dto.getRetirementDate() != null) profile.setRetirementDate(dto.getRetirementDate());
        profile = packageProfileRepository.save(profile);
        commonService.logAudit("PackageProfile", profile.getId(), "PROFILE_UPDATED", profile.getProfileCode());
        return toProfileDto(profile);
    }

    // ---------------- helpers ----------------

    private FormDefinition requireForm(UUID formId) {
        return formDefinitionRepository.findById(formId)
                .orElseThrow(() -> new EntityNotFoundException("Form not found: " + formId));
    }

    private FormDefinitionDto toFormDto(FormDefinition form) {
        FormDefinitionDto dto = mapper.toDto(form);
        dto.setFieldCount(fieldDefinitionRepository.findByFormDefinitionIdOrderByPageNumber(form.getId()).size());
        return dto;
    }

    private PackageProfileDto toProfileDto(PackageProfile profile) {
        PackageProfileDto dto = mapper.toDto(profile);
        dto.setFormCount(packageProfileFormRepository.findByPackageProfileIdOrderBySortOrder(profile.getId()).size());
        dto.setDocumentRequirementCount(documentRequirementRepository.findByPackageProfileIdOrderBySortOrder(profile.getId()).size());
        return dto;
    }

    private AppUser currentUser() {
        try {
            return currentUserProvider.getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String value, E fallback) {
        if (!StringUtils.hasText(value)) return fallback;
        try {
            return Enum.valueOf(type, value.trim());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private <E extends Enum<E>> E parseNullableEnum(Class<E> type, String value) {
        return parseEnum(type, value, null);
    }
}
