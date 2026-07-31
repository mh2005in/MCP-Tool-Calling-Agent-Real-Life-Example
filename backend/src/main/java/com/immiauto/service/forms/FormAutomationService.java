package com.immiauto.service.forms;

import java.util.UUID;

import com.immiauto.dto.forms.CanonicalDataSnapshotDto;
import com.immiauto.dto.forms.FormMappingPreviewDto;
import com.immiauto.dto.forms.MappingPreviewDto;
import com.immiauto.dto.forms.PackageProfileSummaryDto;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.forms.PackageProfile;
import com.immiauto.entity.forms.PackageProfileForm;
import com.immiauto.enums.FormStatus;
import com.immiauto.mapper.PackageProfileMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.forms.PackageDocumentRequirementRepository;
import com.immiauto.repository.forms.PackageProfileFormRepository;
import com.immiauto.repository.forms.PackageProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade for the forms & package workspace read flows (Section 4.1 - Milestone 2):
 * available package profiles, canonical snapshot, and mapping preview.
 * Generation and package-assembly methods are added behind this facade in later milestones.
 */
@Service
@RequiredArgsConstructor
public class FormAutomationService {

    private final CaseRepository caseRepository;
    private final PackageProfileRepository packageProfileRepository;
    private final PackageProfileFormRepository packageProfileFormRepository;
    private final PackageDocumentRequirementRepository documentRequirementRepository;
    private final PackageProfileMapper packageProfileMapper;
    private final CanonicalApplicantDataService canonicalDataService;
    private final FormMappingService formMappingService;

    /**
     * Active package profiles available for a case: those with no specific service
     * type, or matching the case's service type.
     */
    @Transactional(readOnly = true)
    public List<PackageProfileSummaryDto> listAvailableProfiles(UUID caseId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        List<PackageProfileSummaryDto> result = new ArrayList<>();
        for (PackageProfile profile : packageProfileRepository.findByStatus(FormStatus.ACTIVE)) {
            boolean matchesCase = profile.getServiceType() == null
                    || profile.getServiceType() == imCase.getServiceType();
            if (!matchesCase) {
                continue;
            }
            PackageProfileSummaryDto dto = packageProfileMapper.toSummary(profile);
            dto.setFormCount(packageProfileFormRepository.findByPackageProfileIdOrderBySortOrder(profile.getId()).size());
            dto.setDocumentRequirementCount(documentRequirementRepository.findByPackageProfileIdOrderBySortOrder(profile.getId()).size());
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CanonicalDataSnapshotDto getCanonicalSnapshot(UUID caseId) {
        return canonicalDataService.buildSnapshot(caseId);
    }

    /**
     * Build the full mapping preview for a case + package profile: the canonical
     * snapshot plus per-form mapped field previews.
     */
    @Transactional(readOnly = true)
    public MappingPreviewDto previewMappings(UUID caseId, UUID packageProfileId) {
        PackageProfile profile = packageProfileRepository.findById(packageProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Package profile not found: " + packageProfileId));

        CanonicalDataSnapshotDto snapshot = canonicalDataService.buildSnapshot(caseId);

        List<FormMappingPreviewDto> forms = new ArrayList<>();
        for (PackageProfileForm ppf : packageProfileFormRepository.findByPackageProfileIdOrderBySortOrder(packageProfileId)) {
            forms.add(formMappingService.preview(ppf.getFormDefinition(), snapshot));
        }

        return MappingPreviewDto.builder()
                .caseId(caseId)
                .packageProfileId(packageProfileId)
                .profileCode(profile.getProfileCode())
                .profileDisplayName(profile.getDisplayName())
                .generatedAt(LocalDateTime.now().toString())
                .snapshot(snapshot)
                .forms(forms)
                .build();
    }
}
