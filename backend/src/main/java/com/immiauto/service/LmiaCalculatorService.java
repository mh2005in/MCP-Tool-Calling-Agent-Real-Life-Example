package com.immiauto.service;

import com.immiauto.entity.RecruitmentEvidence;
import com.immiauto.repository.RecruitmentEvidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LmiaCalculatorService {

    private static final int MIN_JOB_BANK_DAYS = 28;
    private static final int MIN_ADDITIONAL_ADS = 2;
    private static final int PRE_APPLICATION_WINDOW_MONTHS = 3;

    private final RecruitmentEvidenceRepository recruitmentEvidenceRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> calculateRecruitmentCompliance(Long caseId) {
        return calculateRecruitmentCompliance(caseId, null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> calculateRecruitmentCompliance(Long caseId, LocalDate intendedSubmissionDate) {
        List<RecruitmentEvidence> evidence = recruitmentEvidenceRepository
                .findByImmigrationCaseIdOrderBySortOrder(caseId);

        Map<String, Object> result = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate submissionDate = intendedSubmissionDate != null ? intendedSubmissionDate : today;
        LocalDate windowStart = submissionDate.minusMonths(PRE_APPLICATION_WINDOW_MONTHS);

        result.put("intendedSubmissionDate", submissionDate.toString());
        result.put("recruitmentWindowStart", windowStart.toString());

        Optional<RecruitmentEvidence> jobBank = evidence.stream()
                .filter(e -> "JOB_BANK_POSTING".equals(e.getEvidenceType()))
                .findFirst();

        if (jobBank.isPresent()) {
            RecruitmentEvidence jb = jobBank.get();
            long daysPosted = jb.getExpiryDate() != null
                    ? ChronoUnit.DAYS.between(jb.getPostingDate(), jb.getExpiryDate())
                    : ChronoUnit.DAYS.between(jb.getPostingDate(), today);
            boolean withinWindow = !jb.getPostingDate().isBefore(windowStart)
                    || (jb.getExpiryDate() != null && !jb.getExpiryDate().isBefore(windowStart));
            result.put("jobBankPosted", true);
            result.put("jobBankDaysPosted", daysPosted);
            result.put("jobBankMeetsMinimum", daysPosted >= MIN_JOB_BANK_DAYS);
            result.put("jobBankPostingDate", jb.getPostingDate());
            result.put("jobBankWithinWindow", withinWindow);
        } else {
            result.put("jobBankPosted", false);
            result.put("jobBankMeetsMinimum", false);
            result.put("jobBankWithinWindow", false);
        }

        List<RecruitmentEvidence> additionalAds = evidence.stream()
                .filter(e -> "JOB_AD".equals(e.getEvidenceType()))
                .toList();
        long additionalAdsCount = additionalAds.size();

        Set<String> platforms = additionalAds.stream()
                .map(RecruitmentEvidence::getPlatform)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        boolean distinctAudiences = platforms.size() >= MIN_ADDITIONAL_ADS;

        boolean adsWithinWindow = additionalAds.stream().allMatch(ad ->
                !ad.getPostingDate().isBefore(windowStart));

        result.put("additionalAdsCount", additionalAdsCount);
        result.put("additionalAdsMeetMinimum", additionalAdsCount >= MIN_ADDITIONAL_ADS);
        result.put("distinctPlatforms", platforms.size());
        result.put("distinctAudiencesVerified", distinctAudiences);
        result.put("allAdsWithinWindow", adsWithinWindow);

        int totalApplicants = evidence.stream()
                .mapToInt(RecruitmentEvidence::getApplicantsReceived)
                .sum();
        int totalInterviews = evidence.stream()
                .mapToInt(RecruitmentEvidence::getInterviewsConducted)
                .sum();
        result.put("totalApplicants", totalApplicants);
        result.put("totalInterviews", totalInterviews);

        List<String> warnings = new ArrayList<>();
        if (!(boolean) result.get("jobBankPosted")) {
            warnings.add("Job Bank posting is missing");
        }
        if (!(boolean) result.get("jobBankMeetsMinimum")) {
            warnings.add("Job Bank posting less than 28 days");
        }
        if (result.containsKey("jobBankWithinWindow") && !(boolean) result.get("jobBankWithinWindow")) {
            warnings.add("Job Bank posting may be outside the 3-month pre-application window");
        }
        if (!(boolean) result.get("additionalAdsMeetMinimum")) {
            warnings.add("Need at least 2 additional recruitment ads");
        }
        if (!distinctAudiences) {
            warnings.add("Ads should target distinct audiences on different platforms");
        }
        if (!adsWithinWindow) {
            warnings.add("Some ads may be outside the 3-month pre-application window");
        }
        if (totalInterviews == 0) {
            warnings.add("No interviews recorded — document reasons for not interviewing");
        }
        if (totalInterviews > totalApplicants && totalApplicants > 0) {
            warnings.add("Interview count exceeds applicant count — verify data");
        }

        boolean preliminaryPass = (boolean) result.getOrDefault("jobBankMeetsMinimum", false)
                && (boolean) result.getOrDefault("additionalAdsMeetMinimum", false)
                && (boolean) result.getOrDefault("jobBankWithinWindow", false);
        result.put("preliminaryReviewStatus", preliminaryPass ? "REQUIREMENTS_APPEAR_MET" : "REQUIREMENTS_NOT_YET_MET");
        result.put("warnings", warnings);
        result.put("disclaimer", "PRELIMINARY REVIEW — This is an automated check for organizational purposes only. "
                + "Recruitment compliance must be verified by a licensed consultant considering the applicable LMIA stream, "
                + "provincial variations, Job Match/Direct Apply obligations, and required advertisement content.");
        return result;
    }
}
