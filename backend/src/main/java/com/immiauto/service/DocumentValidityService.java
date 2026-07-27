package com.immiauto.service;

import com.immiauto.dto.IntakeResponseDto;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.ServiceType;
import com.immiauto.repository.CaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentValidityService {

    private static final int LANGUAGE_TEST_VALIDITY_YEARS = 2;
    private static final int ECA_VALIDITY_YEARS = 5;

    private final IntakeService intakeService;
    private final CaseRepository caseRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> validateExpressEntryDocuments(Long caseId) {
        return validateExpressEntryDocuments(caseId, null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validateExpressEntryDocuments(Long caseId, LocalDate plannedSubmissionDate) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        if (imCase.getServiceType() != ServiceType.EXPRESS_ENTRY) {
            throw new IllegalArgumentException("Document validity check is only applicable to Express Entry cases");
        }

        List<IntakeResponseDto> responses = intakeService.getResponses(caseId);
        Map<String, String> answerMap = new HashMap<>();
        for (IntakeResponseDto r : responses) {
            answerMap.put(r.getQuestionKey(), r.getAnswer());
        }

        LocalDate referenceDate = plannedSubmissionDate != null ? plannedSubmissionDate : LocalDate.now();
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> issues = new ArrayList<>();

        validateLanguageTest(answerMap, referenceDate, result, issues);
        validateEca(answerMap, referenceDate, result, issues);

        result.put("validationDate", referenceDate.toString());
        result.put("issues", issues);
        result.put("hasBlockingIssues", issues.stream().anyMatch(i -> "ERROR".equals(i.get("severity"))));
        result.put("disclaimer", "PRELIMINARY CHECK — Document validity must be confirmed by a licensed consultant.");
        return result;
    }

    private void validateLanguageTest(Map<String, String> answers, LocalDate referenceDate,
                                       Map<String, Object> result, List<Map<String, Object>> issues) {
        String testType = answers.get("language_test_type");
        String testDateStr = answers.get("language_test_date");
        String scores = answers.get("language_scores");

        result.put("languageTestType", testType);
        result.put("languageTestDate", testDateStr);
        result.put("languageScores", scores);

        if (testType == null || testType.isBlank() || "None Yet".equalsIgnoreCase(testType)) {
            issues.add(createIssue("ERROR", "language_test",
                    "No language test recorded. A valid language test (IELTS, CELPIP, TEF, or TCF) is required for Express Entry."));
            result.put("languageTestValid", false);
            return;
        }

        if (testDateStr == null || testDateStr.isBlank()) {
            issues.add(createIssue("WARNING", "language_test_date",
                    "Language test date not provided. Cannot verify validity period."));
            result.put("languageTestValid", null);
            return;
        }

        try {
            LocalDate testDate = LocalDate.parse(testDateStr);
            LocalDate expiryDate = testDate.plusYears(LANGUAGE_TEST_VALIDITY_YEARS);
            long daysUntilExpiry = ChronoUnit.DAYS.between(referenceDate, expiryDate);

            result.put("languageTestExpiryDate", expiryDate.toString());
            result.put("languageTestDaysUntilExpiry", daysUntilExpiry);

            if (expiryDate.isBefore(referenceDate)) {
                issues.add(createIssue("ERROR", "language_test_expired",
                        "Language test expired on " + expiryDate + ". Results are valid for " + LANGUAGE_TEST_VALIDITY_YEARS + " years from the test date. A new test is required."));
                result.put("languageTestValid", false);
            } else if (daysUntilExpiry <= 90) {
                issues.add(createIssue("WARNING", "language_test_expiring_soon",
                        "Language test expires on " + expiryDate + " (" + daysUntilExpiry + " days). Consider retaking before submission."));
                result.put("languageTestValid", true);
            } else {
                result.put("languageTestValid", true);
            }
        } catch (DateTimeParseException e) {
            issues.add(createIssue("WARNING", "language_test_date_invalid",
                    "Language test date format is invalid. Expected YYYY-MM-DD."));
            result.put("languageTestValid", null);
        }

        if (scores == null || scores.isBlank()) {
            issues.add(createIssue("WARNING", "language_scores_missing",
                    "Language test scores (L/R/W/S) not recorded. Scores are required for CRS calculation."));
        }
    }

    private void validateEca(Map<String, String> answers, LocalDate referenceDate,
                              Map<String, Object> result, List<Map<String, Object>> issues) {
        String ecaCompleted = answers.get("eca_completed");
        String educationType = answers.get("education_canadian_or_foreign");

        result.put("ecaCompleted", ecaCompleted);
        result.put("educationType", educationType);

        if ("Canadian".equalsIgnoreCase(educationType)) {
            result.put("ecaRequired", false);
            result.put("ecaValid", null);
            return;
        }

        result.put("ecaRequired", true);

        if (ecaCompleted == null || "No".equalsIgnoreCase(ecaCompleted)) {
            issues.add(createIssue("ERROR", "eca_missing",
                    "Educational Credential Assessment (ECA) is required for foreign education but has not been completed. "
                    + "An ECA from a designated organization is mandatory for Express Entry."));
            result.put("ecaValid", false);
            return;
        }

        issues.add(createIssue("WARNING", "eca_details_missing",
                "ECA is marked as completed but issue date, designated organization, and report number are not captured. "
                + "ECA reports are valid for " + ECA_VALIDITY_YEARS + " years from issue date. "
                + "Provide these details to verify validity."));
        result.put("ecaValid", null);
    }

    private Map<String, Object> createIssue(String severity, String field, String message) {
        Map<String, Object> issue = new LinkedHashMap<>();
        issue.put("severity", severity);
        issue.put("field", field);
        issue.put("message", message);
        return issue;
    }
}
