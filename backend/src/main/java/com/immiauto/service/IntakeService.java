package com.immiauto.service;

import com.immiauto.dto.*;
import com.immiauto.entity.*;
import com.immiauto.enums.ServiceType;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntakeService {

    private final IntakeQuestionTemplateRepository questionTemplateRepository;
    private final IntakeResponseRepository responseRepository;
    private final CaseRepository caseRepository;
    private final CommonService commonService;

    @Transactional(readOnly = true)
    public List<IntakeQuestionTemplateDto> getQuestionsForServiceType(ServiceType serviceType) {
        return questionTemplateRepository.findByServiceTypeOrderBySortOrder(serviceType)
                .stream().map(this::toQuestionDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IntakeQuestionTemplateDto> getQuestionsBySection(ServiceType serviceType, String sectionName) {
        return questionTemplateRepository.findByServiceTypeAndSectionNameOrderBySortOrder(serviceType, sectionName)
                .stream().map(this::toQuestionDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getSectionsForServiceType(ServiceType serviceType) {
        return questionTemplateRepository.findByServiceTypeOrderBySortOrder(serviceType)
                .stream()
                .map(IntakeQuestionTemplate::getSectionName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<IntakeResponseDto> submitIntake(Long caseId, IntakeSubmissionRequest request) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        List<IntakeQuestionTemplate> templates =
                questionTemplateRepository.findByServiceTypeOrderBySortOrder(imCase.getServiceType());
        Map<String, IntakeQuestionTemplate> templateMap = templates.stream()
                .collect(Collectors.toMap(IntakeQuestionTemplate::getQuestionKey, Function.identity()));

        Set<String> seenKeys = new HashSet<>();
        for (IntakeResponseDto dto : request.getResponses()) {
            if (!seenKeys.add(dto.getQuestionKey())) {
                throw new IllegalArgumentException("Duplicate question key: " + dto.getQuestionKey());
            }
        }

        Map<String, IntakeResponseDto> submittedByKey = request.getResponses().stream()
                .collect(Collectors.toMap(IntakeResponseDto::getQuestionKey, Function.identity()));

        for (String key : submittedByKey.keySet()) {
            if (!templateMap.containsKey(key)) {
                throw new IllegalArgumentException("Unknown question key: " + key);
            }
        }

        for (IntakeQuestionTemplate tmpl : templates) {
            if (tmpl.isRequired()) {
                IntakeResponseDto dto = submittedByKey.get(tmpl.getQuestionKey());
                if (dto == null || dto.getAnswer() == null || dto.getAnswer().isBlank()) {
                    throw new IllegalArgumentException(
                            "Required question not answered: " + tmpl.getQuestionKey());
                }
            }
        }

        for (IntakeResponseDto dto : request.getResponses()) {
            IntakeQuestionTemplate tmpl = templateMap.get(dto.getQuestionKey());
            String answer = dto.getAnswer();

            if (answer != null && !answer.isBlank()) {
                if ("SELECT".equalsIgnoreCase(tmpl.getInputType()) && tmpl.getOptions() != null) {
                    Set<String> allowed = Arrays.stream(tmpl.getOptions().split("\\|"))
                            .map(String::trim)
                            .collect(Collectors.toSet());
                    if (!allowed.contains(answer)) {
                        throw new IllegalArgumentException(
                                "Invalid option for " + tmpl.getQuestionKey() + ": " + answer);
                    }
                }

                if ("DATE".equalsIgnoreCase(tmpl.getInputType())) {
                    try {
                        LocalDate.parse(answer);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException(
                                "Invalid date for " + tmpl.getQuestionKey() + ": " + answer);
                    }
                }
            }
        }

        responseRepository.deleteAll(
                responseRepository.findByImmigrationCaseIdOrderBySortOrder(caseId));

        List<IntakeResponseDto> saved = new ArrayList<>();
        for (IntakeResponseDto dto : request.getResponses()) {
            IntakeQuestionTemplate tmpl = templateMap.get(dto.getQuestionKey());
            IntakeResponse response = IntakeResponse.builder()
                    .immigrationCase(imCase)
                    .sectionName(tmpl.getSectionName())
                    .questionKey(tmpl.getQuestionKey())
                    .questionLabel(tmpl.getQuestionLabel())
                    .answer(dto.getAnswer())
                    .sortOrder(tmpl.getSortOrder())
                    .flaggedForReview(false)
                    .build();
            saved.add(toResponseDto(responseRepository.save(response)));
        }

        commonService.logAudit("Case", caseId, "INTAKE_SUBMITTED",
                "Intake submitted with " + saved.size() + " responses for " + imCase.getServiceType());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<IntakeResponseDto> getResponses(Long caseId) {
        return responseRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IntakeSummaryDto getIntakeSummary(Long caseId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        List<IntakeResponseDto> responses = getResponses(caseId);

        Map<String, List<IntakeResponseDto>> bySection = responses.stream()
                .collect(Collectors.groupingBy(IntakeResponseDto::getSectionName,
                        LinkedHashMap::new, Collectors.toList()));

        List<IntakeResponseDto> flagged = responses.stream()
                .filter(IntakeResponseDto::isFlaggedForReview)
                .collect(Collectors.toList());

        String aiSummary = generatePlainTextSummary(imCase.getServiceType(), responses);

        return IntakeSummaryDto.builder()
                .caseId(caseId)
                .serviceType(imCase.getServiceType())
                .responsesBySection(bySection)
                .aiSummary(aiSummary)
                .flaggedResponses(flagged)
                .build();
    }

    @Transactional
    public IntakeResponseDto flagResponse(Long responseId, boolean flagged) {
        IntakeResponse response = responseRepository.findById(responseId)
                .orElseThrow(() -> new EntityNotFoundException("Response not found: " + responseId));
        response.setFlaggedForReview(flagged);
        return toResponseDto(responseRepository.save(response));
    }

    @Transactional
    public IntakeResponseDto updateResponse(Long responseId, String answer) {
        IntakeResponse response = responseRepository.findById(responseId)
                .orElseThrow(() -> new EntityNotFoundException("Response not found: " + responseId));
        response.setAnswer(answer);
        return toResponseDto(responseRepository.save(response));
    }

    private String generatePlainTextSummary(ServiceType serviceType, List<IntakeResponseDto> responses) {
        if (responses.isEmpty()) {
            return "No intake responses submitted yet.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Intake Summary for ").append(serviceType.getDisplayName()).append(":\n\n");

        Map<String, List<IntakeResponseDto>> bySection = responses.stream()
                .collect(Collectors.groupingBy(IntakeResponseDto::getSectionName,
                        LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<IntakeResponseDto>> entry : bySection.entrySet()) {
            sb.append("## ").append(entry.getKey()).append("\n");
            for (IntakeResponseDto r : entry.getValue()) {
                if (r.getAnswer() != null && !r.getAnswer().isBlank()) {
                    sb.append("- ").append(r.getQuestionLabel()).append(": ").append(r.getAnswer());
                    if (r.isFlaggedForReview()) {
                        sb.append(" [FLAGGED]");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private IntakeQuestionTemplateDto toQuestionDto(IntakeQuestionTemplate q) {
        return IntakeQuestionTemplateDto.builder()
                .id(q.getId())
                .serviceType(q.getServiceType())
                .sectionName(q.getSectionName())
                .questionKey(q.getQuestionKey())
                .questionLabel(q.getQuestionLabel())
                .helpText(q.getHelpText())
                .inputType(q.getInputType())
                .options(q.getOptions())
                .required(q.isRequired())
                .isTriggerQuestion(q.isTriggerQuestion())
                .sortOrder(q.getSortOrder())
                .build();
    }

    private IntakeResponseDto toResponseDto(IntakeResponse r) {
        return IntakeResponseDto.builder()
                .id(r.getId())
                .caseId(r.getImmigrationCase().getId())
                .sectionName(r.getSectionName())
                .questionKey(r.getQuestionKey())
                .questionLabel(r.getQuestionLabel())
                .answer(r.getAnswer())
                .sortOrder(r.getSortOrder())
                .flaggedForReview(r.isFlaggedForReview())
                .build();
    }

}
