package com.immiauto.service;

import com.immiauto.dto.ChecklistTemplateDto;
import com.immiauto.entity.AuditLog;
import com.immiauto.entity.ChecklistTemplate;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.ServiceType;
import com.immiauto.exception.AdminAccessRequiredException;
import com.immiauto.mapper.ChecklistTemplateMapper;
import com.immiauto.repository.AuditLogRepository;
import com.immiauto.repository.ChecklistTemplateRepository;
import com.immiauto.repository.ConsultantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistTemplateService {

    private final ChecklistTemplateRepository templateRepository;
    private final ConsultantRepository consultantRepository;
    private final AuditLogRepository auditLogRepository;
    private final CommonService commonService;
    private final ChecklistTemplateMapper checklistTemplateMapper;

    @Transactional
    public ChecklistTemplateDto createTemplate(ChecklistTemplateDto dto, Long consultantId) {
        Consultant consultant = findAdminConsultantOrThrow(consultantId);

        ChecklistTemplate template = ChecklistTemplate.builder()
                .serviceType(dto.getServiceType())
                .category(dto.getCategory())
                .documentName(dto.getDocumentName())
                .description(dto.getDescription())
                .required(dto.isRequired())
                .conditional(dto.isConditional())
                .conditionDescription(dto.getConditionDescription())
                .sortOrder(dto.getSortOrder())
                .sourceUrl(dto.getSourceUrl())
                .ruleVersion(1)
                .approvedForUse(false)
                .build();
        template.setCreatedBy(consultant.getFullName());

        template = templateRepository.save(template);

        commonService.logAudit("ChecklistTemplate", template.getId(), "CREATED",
                "Template created: " + dto.getDocumentName() + " for " + dto.getServiceType()
                        + " by " + consultant.getFullName());

        return checklistTemplateMapper.toDto(template);
    }

    @Transactional
    public ChecklistTemplateDto updateTemplate(Long templateId, ChecklistTemplateDto dto, Long consultantId) {
        ChecklistTemplate template = findTemplateOrThrow(templateId);
        Consultant consultant = findAdminConsultantOrThrow(consultantId);

        StringBuilder changes = new StringBuilder();
        if (dto.getCategory() != null && !dto.getCategory().equals(template.getCategory())) {
            changes.append("category: ").append(template.getCategory()).append(" -> ").append(dto.getCategory()).append("; ");
            template.setCategory(dto.getCategory());
        }
        if (dto.getDocumentName() != null && !dto.getDocumentName().equals(template.getDocumentName())) {
            changes.append("documentName: ").append(template.getDocumentName()).append(" -> ").append(dto.getDocumentName()).append("; ");
            template.setDocumentName(dto.getDocumentName());
        }
        if (dto.getDescription() != null) template.setDescription(dto.getDescription());
        if (dto.getConditionDescription() != null) template.setConditionDescription(dto.getConditionDescription());
        if (dto.getSourceUrl() != null) template.setSourceUrl(dto.getSourceUrl());

        template.setRequired(dto.isRequired());
        template.setConditional(dto.isConditional());
        template.setSortOrder(dto.getSortOrder());
        template.setUpdatedBy(consultant.getFullName());

        // Bump rule version on content changes
        template.setRuleVersion(template.getRuleVersion() + 1);

        // Reset approval when template content changes
        template.setApprovedForUse(false);
        template.setApprovedByConsultantId(null);
        template.setApprovedByConsultantName(null);
        template.setApprovedDate(null);

        template = templateRepository.save(template);

        commonService.logAudit("ChecklistTemplate", template.getId(), "UPDATED",
                "Template updated (v" + template.getRuleVersion() + ") by " + consultant.getFullName()
                        + ". Changes: " + (changes.length() > 0 ? changes.toString() : "content updated"));

        return checklistTemplateMapper.toDto(template);
    }

    @Transactional
    public ChecklistTemplateDto reviewTemplate(Long templateId, Long consultantId) {
        ChecklistTemplate template = findTemplateOrThrow(templateId);
        Consultant consultant = findConsultantOrThrow(consultantId);

        template.setLastReviewedDate(LocalDate.now());
        template.setReviewedByConsultantId(consultantId);
        template.setReviewedByConsultantName(consultant.getFullName());
        template.setUpdatedBy(consultant.getFullName());

        template = templateRepository.save(template);

        commonService.logAudit("ChecklistTemplate", template.getId(), "REVIEWED",
                "Template reviewed by " + consultant.getFullName()
                        + " on " + LocalDate.now() + " (v" + template.getRuleVersion() + ")");

        return checklistTemplateMapper.toDto(template);
    }

    @Transactional
    public ChecklistTemplateDto approveTemplate(Long templateId, Long consultantId) {
        ChecklistTemplate template = findTemplateOrThrow(templateId);
        Consultant consultant = findAdminConsultantOrThrow(consultantId);

        template.setApprovedForUse(true);
        template.setApprovedByConsultantId(consultantId);
        template.setApprovedByConsultantName(consultant.getFullName());
        template.setApprovedDate(LocalDate.now());
        template.setUpdatedBy(consultant.getFullName());

        template = templateRepository.save(template);

        commonService.logAudit("ChecklistTemplate", template.getId(), "APPROVED",
                "Template approved for client use by " + consultant.getFullName()
                        + " on " + LocalDate.now() + " (v" + template.getRuleVersion() + ")");

        return checklistTemplateMapper.toDto(template);
    }

    @Transactional
    public ChecklistTemplateDto revokeApproval(Long templateId, Long consultantId) {
        ChecklistTemplate template = findTemplateOrThrow(templateId);
        Consultant consultant = findAdminConsultantOrThrow(consultantId);

        template.setApprovedForUse(false);
        template.setApprovedByConsultantId(null);
        template.setApprovedByConsultantName(null);
        template.setApprovedDate(null);
        template.setUpdatedBy(consultant.getFullName());

        template = templateRepository.save(template);

        commonService.logAudit("ChecklistTemplate", template.getId(), "APPROVAL_REVOKED",
                "Template approval revoked by " + consultant.getFullName());

        return checklistTemplateMapper.toDto(template);
    }

    @Transactional(readOnly = true)
    public List<ChecklistTemplateDto> getTemplatesByServiceType(ServiceType serviceType) {
        return templateRepository.findByServiceTypeOrderBySortOrder(serviceType)
                .stream().map(checklistTemplateMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChecklistTemplateDto> getApprovedTemplatesByServiceType(ServiceType serviceType) {
        return templateRepository.findByServiceTypeAndApprovedForUseTrueOrderBySortOrder(serviceType)
                .stream().map(checklistTemplateMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChecklistTemplateDto getTemplate(Long templateId) {
        return checklistTemplateMapper.toDto(findTemplateOrThrow(templateId));
    }

    @Transactional(readOnly = true)
    public List<ChecklistTemplateDto> getAllTemplates() {
        return templateRepository.findAll()
                .stream().map(checklistTemplateMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteTemplate(Long templateId, Long consultantId) {
        ChecklistTemplate template = findTemplateOrThrow(templateId);
        Consultant consultant = findAdminConsultantOrThrow(consultantId);

        commonService.logAudit("ChecklistTemplate", template.getId(), "DELETED",
                "Template deleted: " + template.getDocumentName() + " for " + template.getServiceType()
                        + " by " + consultant.getFullName());

        templateRepository.delete(template);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getTemplateAuditHistory(Long templateId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByPerformedAtDesc(
                "ChecklistTemplate", templateId);
    }

    private ChecklistTemplate findTemplateOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Checklist template not found: " + id));
    }

    private Consultant findConsultantOrThrow(Long id) {
        return consultantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found: " + id));
    }

    private Consultant findAdminConsultantOrThrow(Long id) {
        Consultant consultant = findConsultantOrThrow(id);
        if (!consultant.isAdmin()) {
            throw new AdminAccessRequiredException(
                    "Only admin consultants can manage checklist templates");
        }
        return consultant;
    }

}
