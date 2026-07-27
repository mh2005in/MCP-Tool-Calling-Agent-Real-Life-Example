package com.immiauto.service;

import com.immiauto.dto.ChecklistItemDto;
import com.immiauto.dto.IntakeResponseDto;
import com.immiauto.entity.*;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.enums.ServiceType;
import com.immiauto.mapper.ChecklistItemMapper;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistGeneratorService {

    private final ChecklistTemplateRepository templateRepository;
    private final ConditionalRuleRepository ruleRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final IntakeResponseRepository intakeResponseRepository;
    private final CaseRepository caseRepository;
    private final CommonService commonService;
    private final ChecklistItemMapper checklistItemMapper;

    @Transactional
    public List<ChecklistItemDto> generateChecklist(Long caseId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        ServiceType serviceType = imCase.getServiceType();

        List<ChecklistTemplate> allTemplates =
                templateRepository.findByServiceTypeAndApprovedForUseTrueOrderBySortOrder(serviceType);
        if (allTemplates.isEmpty()) {
            allTemplates = templateRepository.findByServiceTypeOrderBySortOrder(serviceType);
        }

        List<ConditionalRule> rules =
                ruleRepository.findByServiceTypeAndActiveTrueOrderByTriggerQuestionKey(serviceType);

        Map<String, String> intakeAnswers = intakeResponseRepository
                .findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream()
                .filter(r -> r.getAnswer() != null && !r.getAnswer().isBlank())
                .collect(Collectors.toMap(
                        IntakeResponse::getQuestionKey,
                        IntakeResponse::getAnswer,
                        (a, b) -> b));

        Set<Long> excludedTemplateIds = new HashSet<>();
        Set<Long> includedConditionalIds = new HashSet<>();

        for (ConditionalRule rule : rules) {
            boolean matches = evaluateRule(rule, intakeAnswers);
            if (matches) {
                switch (rule.getActionType()) {
                    case "INCLUDE" -> {
                        if (rule.getTargetChecklistTemplateId() != null) {
                            includedConditionalIds.add(rule.getTargetChecklistTemplateId());
                        }
                    }
                    case "EXCLUDE" -> {
                        if (rule.getTargetChecklistTemplateId() != null) {
                            excludedTemplateIds.add(rule.getTargetChecklistTemplateId());
                        }
                    }
                }
            }
        }

        checklistItemRepository.deleteAll(
                checklistItemRepository.findByImmigrationCaseIdOrderBySortOrder(caseId));

        List<ChecklistItemDto> result = new ArrayList<>();
        for (ChecklistTemplate t : allTemplates) {
            if (excludedTemplateIds.contains(t.getId())) continue;

            if (t.isConditional() && !includedConditionalIds.contains(t.getId())) {
                if (!intakeAnswers.isEmpty()) continue;
            }

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
            item = checklistItemRepository.save(item);
            result.add(checklistItemMapper.toDto(item));
        }

        commonService.logAudit("Case", caseId, "CHECKLIST_GENERATED",
                "Generated " + result.size() + " checklist items based on intake answers and rules for " + serviceType);

        return result;
    }

    private boolean evaluateRule(ConditionalRule rule, Map<String, String> answers) {
        String actual = answers.get(rule.getTriggerQuestionKey());
        if (actual == null) return false;

        String expected = rule.getTriggerValue();
        return switch (rule.getOperator()) {
            case "EQUALS" -> actual.equalsIgnoreCase(expected);
            case "NOT_EQUALS" -> !actual.equalsIgnoreCase(expected);
            case "CONTAINS" -> actual.toLowerCase().contains(expected.toLowerCase());
            case "NOT_CONTAINS" -> !actual.toLowerCase().contains(expected.toLowerCase());
            case "YES" -> actual.equalsIgnoreCase("yes") || actual.equalsIgnoreCase("true");
            case "NO" -> actual.equalsIgnoreCase("no") || actual.equalsIgnoreCase("false");
            default -> false;
        };
    }

}
