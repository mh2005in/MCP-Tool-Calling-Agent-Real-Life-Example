package com.immiauto.config;

import com.immiauto.entity.ConditionalRule;
import com.immiauto.enums.ServiceType;
import com.immiauto.repository.ChecklistTemplateRepository;
import com.immiauto.repository.ConditionalRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("dev")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class ConditionalRuleSeeder implements CommandLineRunner {

    private final ConditionalRuleRepository ruleRepo;
    private final ChecklistTemplateRepository templateRepo;

    @Override
    public void run(String... args) {
        if (ruleRepo.count() > 0) return;
        log.info("Seeding conditional rules...");

        // Study Permit rules
        rule(ServiceType.STUDY_PERMIT, "is_quebec", "Yes", "EQUALS", "INCLUDE", null,
                "Include CAQ requirement when school is in Quebec");
        rule(ServiceType.STUDY_PERMIT, "school_province", "QC", "EQUALS", "INCLUDE", null,
                "Include CAQ requirement when province is Quebec");
        rule(ServiceType.STUDY_PERMIT, "has_sponsor", "Yes", "EQUALS", "INCLUDE", null,
                "Include sponsor financial documents when sponsor is involved");
        rule(ServiceType.STUDY_PERMIT, "spouse_accompanying", "Yes", "EQUALS", "INCLUDE", null,
                "Include spouse/family documents when family is accompanying");
        rule(ServiceType.STUDY_PERMIT, "children_accompanying", "Yes", "EQUALS", "INCLUDE", null,
                "Include dependent children documents when children are accompanying");
        rule(ServiceType.STUDY_PERMIT, "prior_refusals", "Yes", "EQUALS", "INCLUDE", null,
                "Include prior refusal explanation letter when refusals exist");
        rule(ServiceType.STUDY_PERMIT, "funding_source", "Scholarship", "EQUALS", "INCLUDE", null,
                "Include scholarship award letter when scholarship-funded");
        rule(ServiceType.STUDY_PERMIT, "has_gic", "Yes", "EQUALS", "INCLUDE", null,
                "Include GIC confirmation when GIC is available");

        // Visitor Visa rules
        rule(ServiceType.VISITOR_VISA, "has_canadian_host", "Yes", "EQUALS", "INCLUDE", null,
                "Include invitation letter and host documents when host exists");
        rule(ServiceType.VISITOR_VISA, "visit_purpose", "Business", "EQUALS", "INCLUDE", null,
                "Include business invitation/conference documents for business visits");
        rule(ServiceType.VISITOR_VISA, "prior_visa_refusals", "Yes", "EQUALS", "INCLUDE", null,
                "Include refusal explanation when prior refusals exist");
        rule(ServiceType.VISITOR_VISA, "funding_type", "Host-funded", "EQUALS", "INCLUDE", null,
                "Include host financial proof when host is funding trip");

        // Spousal Sponsorship rules
        rule(ServiceType.SPOUSAL_SPONSORSHIP, "relationship_type", "Common-Law", "EQUALS", "INCLUDE", null,
                "Include statutory declaration of common-law union");
        rule(ServiceType.SPOUSAL_SPONSORSHIP, "relationship_type", "Conjugal", "EQUALS", "INCLUDE", null,
                "Include conjugal relationship evidence and barrier explanation");
        rule(ServiceType.SPOUSAL_SPONSORSHIP, "has_children", "Yes", "EQUALS", "INCLUDE", null,
                "Include children's identity/custody documents");
        rule(ServiceType.SPOUSAL_SPONSORSHIP, "sponsor_previously_sponsored", "Yes", "EQUALS", "INCLUDE", null,
                "Include proof that 3-year undertaking from previous sponsorship has ended");
        rule(ServiceType.SPOUSAL_SPONSORSHIP, "sponsor_in_quebec", "Yes", "EQUALS", "INCLUDE", null,
                "Include Quebec CSQ/undertaking forms for Quebec sponsorship");
        rule(ServiceType.SPOUSAL_SPONSORSHIP, "prior_refusals_removals", "Yes", "EQUALS", "INCLUDE", null,
                "Include rehabilitation/explanation for prior refusals or inadmissibility");

        // Express Entry rules
        rule(ServiceType.EXPRESS_ENTRY, "education_canadian_or_foreign", "Foreign", "EQUALS", "INCLUDE", null,
                "Include ECA report for foreign education credentials");
        rule(ServiceType.EXPRESS_ENTRY, "education_canadian_or_foreign", "Both", "EQUALS", "INCLUDE", null,
                "Include ECA report when applicant has both Canadian and foreign education");
        rule(ServiceType.EXPRESS_ENTRY, "has_dependents", "Yes", "EQUALS", "INCLUDE", null,
                "Include dependent children documents");
        rule(ServiceType.EXPRESS_ENTRY, "spouse_accompanying", "Yes", "EQUALS", "INCLUDE", null,
                "Include spouse documents and language test");
        rule(ServiceType.EXPRESS_ENTRY, "prior_refusals", "Yes", "EQUALS", "INCLUDE", null,
                "Include prior refusal explanation letter");
        rule(ServiceType.EXPRESS_ENTRY, "program_path", "PNP Express Entry", "EQUALS", "INCLUDE", null,
                "Include PNP nomination certificate");

        // Work Permit rules
        rule(ServiceType.WORK_PERMIT, "lmia_status", "LMIA Required (has LMIA)", "EQUALS", "INCLUDE", null,
                "Include LMIA confirmation letter");
        rule(ServiceType.WORK_PERMIT, "wp_type", "Open Work Permit", "EQUALS", "EXCLUDE", null,
                "Exclude employer-specific documents for open work permits");
        rule(ServiceType.WORK_PERMIT, "spouse_accompanying", "Yes", "EQUALS", "INCLUDE", null,
                "Include spouse/family documents");
        rule(ServiceType.WORK_PERMIT, "prior_refusals", "Yes", "EQUALS", "INCLUDE", null,
                "Include prior refusal explanation letter");
        rule(ServiceType.WORK_PERMIT, "application_location", "Inside Canada", "EQUALS", "INCLUDE", null,
                "Include current status documents for in-Canada applications");

        // LMIA rules
        rule(ServiceType.LMIA, "lmia_stream", "Global Talent Stream", "EQUALS", "INCLUDE", null,
                "Include ESDC referral partner letter for Global Talent Stream");
        rule(ServiceType.LMIA, "lmia_stream", "Agricultural", "EQUALS", "INCLUDE", null,
                "Include seasonal agricultural worker program documents");
        rule(ServiceType.LMIA, "lmia_stream", "Caregiver", "EQUALS", "INCLUDE", null,
                "Include caregiver-specific employer requirements");
        rule(ServiceType.LMIA, "recent_layoffs", "Yes", "EQUALS", "INCLUDE", null,
                "Include layoff justification when recent layoffs occurred");

        // Citizenship rules
        rule(ServiceType.CITIZENSHIP, "applicant_age_category", "Minor (under 18)", "EQUALS", "INCLUDE", null,
                "Include parent/guardian documents for minor applicants");
        rule(ServiceType.CITIZENSHIP, "applicant_age_category", "Minor (under 18)", "EQUALS", "EXCLUDE", null,
                "Exclude language test for minor applicants");
        rule(ServiceType.CITIZENSHIP, "absences_outside_canada", "Yes", "EQUALS", "INCLUDE", null,
                "Include absence log with proof of travel dates");
        rule(ServiceType.CITIZENSHIP, "criminality", "Yes", "EQUALS", "INCLUDE", null,
                "Include court records and rehabilitation documents");
        rule(ServiceType.CITIZENSHIP, "name_consistency", "No", "EQUALS", "INCLUDE", null,
                "Include legal name change certificates");

        // PR Card rules
        rule(ServiceType.PR_CARD_PRTD, "application_reason", "PR Travel Document (PRTD)", "EQUALS", "INCLUDE", null,
                "Include PRTD-specific documents and proof of ties to Canada");
        rule(ServiceType.PR_CARD_PRTD, "card_lost_stolen_damaged", "Stolen", "EQUALS", "INCLUDE", null,
                "Include police report for stolen card");
        rule(ServiceType.PR_CARD_PRTD, "name_gender_changes", "Yes", "EQUALS", "INCLUDE", null,
                "Include legal name/gender change documents");

        // PGWP rules
        rule(ServiceType.PGWP, "transcript_available", "Pending", "EQUALS", "INCLUDE", null,
                "Include interim transcript and expected date of final transcript");
        rule(ServiceType.PGWP, "program_transfers", "Yes", "EQUALS", "INCLUDE", null,
                "Include transfer letters from all institutions attended");
        rule(ServiceType.PGWP, "language_test_taken", "No", "EQUALS", "INCLUDE", null,
                "Flag: language test may be required depending on graduation date");

        // Super Visa rules
        rule(ServiceType.SUPER_VISA, "has_medical_insurance", "No", "EQUALS", "INCLUDE", null,
                "Flag: medical insurance is mandatory for Super Visa");
        rule(ServiceType.SUPER_VISA, "prior_refusals", "Yes", "EQUALS", "INCLUDE", null,
                "Include prior refusal explanation letter");

        log.info("Conditional rules seeded.");
    }

    private void rule(ServiceType serviceType, String triggerKey, String triggerValue,
                      String operator, String actionType, UUID targetTemplateId,
                      String description) {
        ruleRepo.save(ConditionalRule.builder()
                .serviceType(serviceType)
                .triggerQuestionKey(triggerKey)
                .triggerValue(triggerValue)
                .operator(operator)
                .actionType(actionType)
                .targetChecklistTemplateId(targetTemplateId)
                .description(description)
                .active(true)
                .build());
    }
}
