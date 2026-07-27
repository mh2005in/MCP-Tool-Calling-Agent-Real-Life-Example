package com.immiauto.service;

import com.immiauto.dto.*;
import com.immiauto.entity.*;
import com.immiauto.enums.*;
import com.immiauto.mapper.CaseMapper;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistTemplateRepository checklistTemplateRepository;
    private final CommonService commonService;
    private final CaseMapper caseMapper;

    @Transactional
    public CaseDto createCase(CreateCaseRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        Consultant consultant = consultantRepository.findById(request.getConsultantId())
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found"));

        ImmigrationCase imCase = ImmigrationCase.builder()
                .serviceType(request.getServiceType())
                .subtype(request.getSubtype())
                .applicantRole(request.getApplicantRole())
                .leadStatus(LeadStatus.NEW)
                .caseStatus(CaseStatus.INTAKE_PENDING)
                .client(client)
                .consultant(consultant)
                .deadline(request.getDeadline())
                .urgencyReason(request.getUrgencyReason())
                .consultantNotes(request.getConsultantNotes())
                .build();

        imCase = caseRepository.save(imCase);

        // Apply default checklist template if requested
        if (request.isApplyDefaultChecklist()) {
            applyChecklistTemplate(imCase);
        }

        commonService.logAudit("Case", imCase.getId(), "CREATED",
                "Case created for " + client.getFullName() + " - " + request.getServiceType());

        return toDto(imCase);
    }

    @Transactional(readOnly = true)
    public CaseDto getCase(Long consultantId, Long caseId) {
        ImmigrationCase imCase = findCaseOrThrow(caseId);
        commonService.verifyOwnershipOrAdmin(consultantId, imCase.getConsultant().getId(), "case");
        return toDto(imCase);
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getAllCases() {
        return caseRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getCasesByConsultant(Long consultantId) {
        return caseRepository.findByConsultantId(consultantId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getCasesByConsultantForAdmin(Long requestingConsultantId, Long targetConsultantId) {
        commonService.requireAdmin(requestingConsultantId);
        return caseRepository.findByConsultantId(targetConsultantId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getAllCasesForAdmin(Long requestingConsultantId) {
        commonService.requireAdmin(requestingConsultantId);
        return caseRepository.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CaseDto getCaseForAdmin(Long requestingConsultantId, Long caseId) {
        commonService.requireAdmin(requestingConsultantId);
        return toDto(findCaseOrThrow(caseId));
    }

    @Transactional(readOnly = true)
    public CaseDto getCaseByCaseNumber(Long consultantId, String caseNumber) {
        ImmigrationCase imCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new EntityNotFoundException("Case not found with number: " + caseNumber));
        commonService.verifyOwnershipOrAdmin(consultantId, imCase.getConsultant().getId(), "case");
        return toDto(imCase);
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getActiveCasesByConsultant(Long consultantId) {
        return caseRepository.findActiveCasesByConsultant(consultantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CaseDto updateCaseStatus(Long consultantId, Long id, CaseStatus newStatus) {
        ImmigrationCase imCase = findCaseOrThrow(id);
        commonService.verifyOwnershipOrAdmin(consultantId, imCase.getConsultant().getId(), "case");
        CaseStatus old = imCase.getCaseStatus();
        imCase.setCaseStatus(newStatus);
        caseRepository.save(imCase);
        commonService.logAudit("Case", id, "STATUS_CHANGED", old + " -> " + newStatus);
        return toDto(imCase);
    }

    @Transactional
    public CaseDto updateLeadStatus(Long consultantId, Long id, LeadStatus newStatus) {
        ImmigrationCase imCase = findCaseOrThrow(id);
        commonService.verifyOwnershipOrAdmin(consultantId, imCase.getConsultant().getId(), "case");
        imCase.setLeadStatus(newStatus);
        caseRepository.save(imCase);
        return toDto(imCase);
    }

    @Transactional
    public CaseDto updateCase(Long consultantId, Long id, CaseDto dto) {
        ImmigrationCase imCase = findCaseOrThrow(id);
        commonService.verifyOwnershipOrAdmin(consultantId, imCase.getConsultant().getId(), "case");
        if (dto.getConsultantNotes() != null) imCase.setConsultantNotes(dto.getConsultantNotes());
        if (dto.getIntakeSummary() != null) imCase.setIntakeSummary(dto.getIntakeSummary());
        if (dto.getDeadline() != null) imCase.setDeadline(dto.getDeadline());
        if (dto.getUrgencyReason() != null) imCase.setUrgencyReason(dto.getUrgencyReason());
        if (dto.getApplicationNumber() != null) imCase.setApplicationNumber(dto.getApplicationNumber());
        if (dto.getSubmissionDate() != null) imCase.setSubmissionDate(dto.getSubmissionDate());
        if (dto.getPortalUsed() != null) imCase.setPortalUsed(dto.getPortalUsed());
        if (dto.getBiometricsStatus() != null) imCase.setBiometricsStatus(dto.getBiometricsStatus());
        if (dto.getMedicalStatus() != null) imCase.setMedicalStatus(dto.getMedicalStatus());
        if (dto.getRetainerSignedDate() != null) imCase.setRetainerSignedDate(dto.getRetainerSignedDate());
        if (dto.getRetainerDocumentPath() != null) imCase.setRetainerDocumentPath(dto.getRetainerDocumentPath());
        if (dto.getEngagementLetterStatus() != null) imCase.setEngagementLetterStatus(dto.getEngagementLetterStatus());
        if (dto.getFinalReviewedBy() != null) imCase.setFinalReviewedBy(dto.getFinalReviewedBy());
        if (dto.getFinalReviewedAt() != null) imCase.setFinalReviewedAt(dto.getFinalReviewedAt());
        if (dto.getFinalReviewNotes() != null) imCase.setFinalReviewNotes(dto.getFinalReviewNotes());
        if (dto.isConsultantSignedOff()) imCase.setConsultantSignedOff(true);
        caseRepository.save(imCase);
        return toDto(imCase);
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getCasesWithUpcomingDeadlines(int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return caseRepository.findCasesWithUpcomingDeadlines(cutoff)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CaseDto> getCasesWithUpcomingDeadlinesByConsultant(Long consultantId, int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return caseRepository.findCasesWithUpcomingDeadlinesByConsultant(consultantId, cutoff)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private void applyChecklistTemplate(ImmigrationCase imCase) {
        List<ChecklistTemplate> templates =
                checklistTemplateRepository.findByServiceTypeAndApprovedForUseTrueOrderBySortOrder(imCase.getServiceType());

        if (templates.isEmpty()) {
            templates = checklistTemplateRepository.findByServiceTypeOrderBySortOrder(imCase.getServiceType());
        }

        for (ChecklistTemplate t : templates) {
            ChecklistItem item = ChecklistItem.builder()
                    .immigrationCase(imCase)
                    .category(t.getCategory())
                    .documentName(t.getDocumentName())
                    .description(t.getDescription())
                    .status(DocumentStatus.NOT_UPLOADED)
                    .required(t.isRequired())
                    .conditional(t.isConditional())
                    .conditionDescription(t.getConditionDescription())
                    .sortOrder(t.getSortOrder())
                    .build();
            checklistItemRepository.save(item);
        }
    }


    private ImmigrationCase findCaseOrThrow(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + id));
    }

    private CaseDto toDto(ImmigrationCase c) {
        CaseDto dto = caseMapper.toDto(c);
        dto.setTotalChecklistItems(checklistItemRepository.countByImmigrationCaseId(c.getId()));
        dto.setCompletedItems(checklistItemRepository.countByImmigrationCaseIdAndStatus(c.getId(), DocumentStatus.ACCEPTED));
        dto.setMissingItems(checklistItemRepository.countByImmigrationCaseIdAndStatus(c.getId(), DocumentStatus.NOT_UPLOADED));
        return dto;
    }

}
