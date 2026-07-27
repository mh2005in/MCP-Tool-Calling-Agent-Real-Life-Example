package com.immiauto.service;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SensitiveFieldRegistry {

    private static final Set<String> SENSITIVE_QUESTION_KEYS = Set.of(
            // Identity
            "full_name",
            "dob",
            "passport_number",
            "passport_expiry",
            "applicant_passport_expiry",

            // Names of other parties
            "host_name",
            "applicant_name",
            "candidate_name",
            "employer_legal_name",
            "employer_operating_name",

            // Contact / location
            "shared_address",
            "business_address",
            "work_location",
            "planned_stay_location",

            // Financial identifiers
            "cra_bn",
            "employer_cra_bn",
            "payroll_account",
            "insurance_policy_details",
            "lmia_number",
            "police_report_number",

            // Relationship dates that could identify individuals
            "first_contact_date",
            "first_meeting_date",
            "engagement_date",
            "marriage_cohabitation_date",
            "cohabitation_dates",

            // Free-text fields likely to contain PII
            "prior_refusal_details",
            "refusal_details",
            "absence_details",
            "travel_details",
            "prior_app_details",
            "change_details",
            "family_ties",
            "cohabitation_proof",
            "host_support_evidence",
            "countries_visited",
            "countries_lived_in",
            "previous_travel_history"
    );

    private static final Set<String> SENSITIVE_DOCUMENT_CATEGORIES = Set.of(
            "Identity",
            "Civil Status",
            "Financial",
            "Police"
    );

    public boolean isSensitiveQuestionKey(String questionKey) {
        return questionKey != null && SENSITIVE_QUESTION_KEYS.contains(questionKey);
    }

    public boolean isSensitiveDocumentCategory(String category) {
        return category != null && SENSITIVE_DOCUMENT_CATEGORIES.contains(category);
    }

    public Set<String> getSensitiveQuestionKeys() {
        return SENSITIVE_QUESTION_KEYS;
    }

    public Set<String> getSensitiveDocumentCategories() {
        return SENSITIVE_DOCUMENT_CATEGORIES;
    }
}
