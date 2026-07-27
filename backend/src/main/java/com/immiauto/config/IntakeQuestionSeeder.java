package com.immiauto.config;

import com.immiauto.entity.IntakeQuestionTemplate;
import com.immiauto.enums.ServiceType;
import com.immiauto.repository.IntakeQuestionTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class IntakeQuestionSeeder implements CommandLineRunner {

    private final IntakeQuestionTemplateRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        log.info("Seeding intake question templates...");

        seedStudyPermit();
        seedVisitorVisa();
        seedSpousalSponsorship();
        seedExpressEntry();
        seedWorkPermit();
        seedLMIA();
        seedCitizenship();
        seedPRCard();
        seedPGWP();
        seedSuperVisa();

        log.info("Intake question templates seeded.");
    }

    // ======== STUDY PERMIT (3.2) ========
    private void seedStudyPermit() {
        ServiceType t = ServiceType.STUDY_PERMIT;
        int o = 0;
        // Applicant Identity
        save(t, "Applicant Identity", "full_name", "Full Legal Name (as on passport)", null, "TEXT", null, true, false, ++o);
        save(t, "Applicant Identity", "dob", "Date of Birth", null, "DATE", null, true, false, ++o);
        save(t, "Applicant Identity", "citizenship", "Country of Citizenship", null, "TEXT", null, true, false, ++o);
        save(t, "Applicant Identity", "passport_number", "Passport Number", null, "TEXT", null, true, false, ++o);
        save(t, "Applicant Identity", "passport_expiry", "Passport Expiry Date", null, "DATE", null, true, false, ++o);
        // Education
        save(t, "Education", "dli_name", "Name of Designated Learning Institution (DLI)", null, "TEXT", null, true, false, ++o);
        save(t, "Education", "program_name", "Program Name", null, "TEXT", null, true, false, ++o);
        save(t, "Education", "program_start_date", "Program Start Date", null, "DATE", null, true, false, ++o);
        save(t, "Education", "program_end_date", "Program End Date", null, "DATE", null, true, false, ++o);
        save(t, "Education", "level_of_study", "Level of Study", null, "SELECT", "Certificate|Diploma|Bachelor|Master|PhD|K-12|Language|Other", true, false, ++o);
        // Location
        save(t, "Location", "school_province", "Province of School", null, "SELECT", "AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT", true, true, ++o);
        save(t, "Location", "is_quebec", "Is this a Quebec institution?", "If yes, CAQ process applies instead of PAL/TAL", "SELECT", "Yes|No", true, true, ++o);
        // PAL/TAL/CAQ
        save(t, "PAL/TAL/CAQ", "has_pal_tal", "Has the school issued a PAL/TAL?", null, "SELECT", "Yes|No|Not Sure", false, true, ++o);
        save(t, "PAL/TAL/CAQ", "has_caq", "Has the school issued a CAQ? (Quebec only)", null, "SELECT", "Yes|No|Not Applicable", false, true, ++o);
        // Finances
        save(t, "Finances", "funding_source", "Who pays tuition and living expenses?", null, "SELECT", "Self|Parent/Family|Sponsor|Scholarship|Employer|GIC|Combination", true, true, ++o);
        save(t, "Finances", "funds_available", "Approximate funds available (CAD)", null, "TEXT", null, true, false, ++o);
        save(t, "Finances", "has_gic", "Do you have a GIC?", null, "SELECT", "Yes|No", false, false, ++o);
        // Sponsor
        save(t, "Sponsor", "has_sponsor", "Is a sponsor providing financial support?", null, "SELECT", "Yes|No", false, true, ++o);
        save(t, "Sponsor", "sponsor_relationship", "Relationship to Sponsor", null, "TEXT", null, false, false, ++o);
        save(t, "Sponsor", "sponsor_income_source", "Sponsor's Source of Income", null, "TEXT", null, false, false, ++o);
        // History
        save(t, "History", "prior_refusals", "Have you had any prior visa/permit refusals?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "History", "prior_refusal_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
        save(t, "History", "prior_overstays", "Have you ever overstayed a visa?", null, "SELECT", "Yes|No", true, false, ++o);
        save(t, "History", "previous_canadian_apps", "Any previous Canadian immigration applications?", null, "SELECT", "Yes|No", true, false, ++o);
        // Family
        save(t, "Family", "spouse_accompanying", "Is your spouse/partner accompanying you?", null, "SELECT", "Yes|No|Not Applicable", false, true, ++o);
        save(t, "Family", "children_accompanying", "Are children accompanying you?", null, "SELECT", "Yes|No|Not Applicable", false, true, ++o);
        // Country-specific
        save(t, "Country-Specific", "applying_from_country", "Which country are you applying from?", null, "TEXT", null, true, true, ++o);
    }

    // ======== VISITOR VISA (3.3) ========
    private void seedVisitorVisa() {
        ServiceType t = ServiceType.VISITOR_VISA;
        int o = 0;
        save(t, "Visit Purpose", "visit_purpose", "Purpose of Visit", null, "SELECT", "Tourism|Family Visit|Business|Event|Medical|Other", true, true, ++o);
        save(t, "Visit Purpose", "visit_purpose_details", "Details of Visit Purpose", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Host", "has_canadian_host", "Is there a Canadian host?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Host", "host_relationship", "Relationship to Host", null, "TEXT", null, false, false, ++o);
        save(t, "Host", "host_name", "Host Full Name", null, "TEXT", null, false, false, ++o);
        save(t, "Host", "host_status", "Host's Immigration Status", null, "SELECT", "Citizen|Permanent Resident|Temporary Resident|Other", false, false, ++o);
        save(t, "Duration", "planned_arrival", "Planned Arrival Date", null, "DATE", null, true, false, ++o);
        save(t, "Duration", "planned_departure", "Planned Departure Date", null, "DATE", null, true, false, ++o);
        save(t, "Funding", "funding_type", "Who is funding the trip?", null, "SELECT", "Self-funded|Host-funded|Employer|Combination", true, true, ++o);
        save(t, "Employment", "current_employer", "Current Employer", null, "TEXT", null, false, false, ++o);
        save(t, "Employment", "job_title", "Job Title", null, "TEXT", null, false, false, ++o);
        save(t, "Employment", "leave_approved", "Has leave been approved?", null, "SELECT", "Yes|No|Not Applicable", false, false, ++o);
        save(t, "Assets/Ties", "property_owned", "Do you own property in your home country?", null, "SELECT", "Yes|No", false, false, ++o);
        save(t, "Assets/Ties", "family_ties", "Family members remaining in home country", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Assets/Ties", "business_owned", "Do you own a business?", null, "SELECT", "Yes|No", false, false, ++o);
        save(t, "Travel History", "countries_visited", "Countries visited in the last 10 years", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Travel History", "prior_canadian_visits", "Have you visited Canada before?", null, "SELECT", "Yes|No", false, false, ++o);
        save(t, "Refusals", "prior_visa_refusals", "Any previous visa refusals (any country)?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Refusals", "refusal_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
    }

    // ======== SPOUSAL SPONSORSHIP (3.4) ========
    private void seedSpousalSponsorship() {
        ServiceType t = ServiceType.SPOUSAL_SPONSORSHIP;
        int o = 0;
        // Sponsor
        save(t, "Sponsor", "sponsor_status", "Sponsor's Immigration Status", null, "SELECT", "Canadian Citizen|Permanent Resident", true, true, ++o);
        save(t, "Sponsor", "sponsor_in_canada", "Is sponsor currently living in Canada?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Sponsor", "sponsor_province", "Sponsor's Province of Residence", null, "SELECT", "AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT|Outside Canada", true, true, ++o);
        // Applicant
        save(t, "Applicant", "applicant_country", "Applicant's Country of Residence", null, "TEXT", null, true, false, ++o);
        save(t, "Applicant", "applicant_current_status", "Applicant's Current Immigration Status", null, "TEXT", null, true, false, ++o);
        save(t, "Applicant", "applicant_prior_marriages", "Has applicant been previously married?", null, "SELECT", "Yes|No", true, false, ++o);
        // Relationship Type
        save(t, "Relationship Type", "relationship_type", "Relationship Type", null, "SELECT", "Married|Common-Law|Conjugal", true, true, ++o);
        // Relationship Timeline
        save(t, "Relationship Timeline", "first_contact_date", "Date of First Contact", null, "DATE", null, true, false, ++o);
        save(t, "Relationship Timeline", "first_meeting_date", "Date of First In-Person Meeting", null, "DATE", null, true, false, ++o);
        save(t, "Relationship Timeline", "engagement_date", "Engagement Date (if applicable)", null, "DATE", null, false, false, ++o);
        save(t, "Relationship Timeline", "marriage_cohabitation_date", "Marriage/Cohabitation Date", null, "DATE", null, true, false, ++o);
        // Cohabitation
        save(t, "Cohabitation", "shared_address", "Do you share an address?", null, "SELECT", "Yes|No", true, false, ++o);
        save(t, "Cohabitation", "cohabitation_dates", "Dates of Cohabitation", null, "TEXT", null, false, false, ++o);
        save(t, "Cohabitation", "cohabitation_proof", "What cohabitation proof is available?", null, "TEXTAREA", null, false, false, ++o);
        // Dependents
        save(t, "Dependents", "has_children", "Are there any children?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Dependents", "custody_issues", "Any custody issues?", null, "SELECT", "Yes|No|Not Applicable", false, false, ++o);
        // Previous Sponsorship
        save(t, "Previous Sponsorship", "sponsor_previously_sponsored", "Has the sponsor sponsored before?", null, "SELECT", "Yes|No", true, true, ++o);
        // Criminal/Immigration History
        save(t, "Criminal/Immigration History", "prior_refusals_removals", "Any prior refusals, removals, or criminality?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Criminal/Immigration History", "refusal_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
        // Quebec
        save(t, "Quebec", "sponsor_in_quebec", "Does the sponsor live in Quebec?", "Quebec has a separate sponsorship process", "SELECT", "Yes|No", true, true, ++o);
    }

    // ======== EXPRESS ENTRY (3.5) ========
    private void seedExpressEntry() {
        ServiceType t = ServiceType.EXPRESS_ENTRY;
        int o = 0;
        save(t, "Program Path", "program_path", "Which Express Entry program?", null, "SELECT", "Federal Skilled Worker (FSW)|Canadian Experience Class (CEC)|Federal Skilled Trades (FST)|PNP Express Entry", true, true, ++o);
        save(t, "Work History", "current_employer", "Current/Most Recent Employer", null, "TEXT", null, true, false, ++o);
        save(t, "Work History", "job_title", "Job Title", null, "TEXT", null, true, false, ++o);
        save(t, "Work History", "noc_teer", "NOC/TEER Code (if known)", null, "TEXT", null, false, false, ++o);
        save(t, "Work History", "employment_dates", "Employment Start and End Dates", null, "TEXT", null, true, false, ++o);
        save(t, "Work History", "hours_per_week", "Hours Per Week", null, "TEXT", null, true, false, ++o);
        save(t, "Work History", "job_duties", "Main Job Duties", null, "TEXTAREA", null, true, false, ++o);
        save(t, "Education", "education_canadian_or_foreign", "Is your highest education Canadian or Foreign?", null, "SELECT", "Canadian|Foreign|Both", true, true, ++o);
        save(t, "Education", "eca_completed", "Has an Educational Credential Assessment (ECA) been completed?", null, "SELECT", "Yes|No|Not Applicable", true, true, ++o);
        save(t, "Education", "highest_education", "Highest Level of Education", null, "SELECT", "High School|1-Year Post-Secondary|2-Year Post-Secondary|3-Year Post-Secondary|Bachelor|Two or More Degrees|Master|PhD", true, false, ++o);
        save(t, "Language", "language_test_type", "Language Test Type", null, "SELECT", "IELTS|CELPIP|TEF|TCF|None Yet", true, true, ++o);
        save(t, "Language", "language_test_date", "Test Date", null, "DATE", null, false, false, ++o);
        save(t, "Language", "language_scores", "Test Scores (L/R/W/S)", null, "TEXT", null, false, false, ++o);
        save(t, "Marital Status", "marital_status", "Marital Status", null, "SELECT", "Single|Married|Common-Law|Separated|Divorced|Widowed", true, true, ++o);
        save(t, "Marital Status", "spouse_accompanying", "Is spouse/partner accompanying?", null, "SELECT", "Yes|No|Not Applicable", false, true, ++o);
        save(t, "Dependents", "has_dependents", "Any dependent children?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Dependents", "custody_issues", "Any custody issues?", null, "SELECT", "Yes|No|Not Applicable", false, false, ++o);
        save(t, "Funds", "funds_required", "Are proof of funds required or exempt?", null, "SELECT", "Required|Exempt (have valid job offer)|Not Sure", true, true, ++o);
        save(t, "Travel/Residence", "countries_lived_in", "Countries lived in for 6+ months since age 18", "Used to determine police certificate requirements", "TEXTAREA", null, true, true, ++o);
        save(t, "Prior Applications", "prior_refusals", "Any prior Canadian refusals, inadmissibility, or misrepresentation?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Prior Applications", "prior_refusal_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
    }

    // ======== WORK PERMIT (3.6) ========
    private void seedWorkPermit() {
        ServiceType t = ServiceType.WORK_PERMIT;
        int o = 0;
        save(t, "Application Location", "application_location", "Where are you applying from?", null, "SELECT", "Inside Canada|Outside Canada|Port of Entry", true, true, ++o);
        save(t, "Work Permit Type", "wp_type", "Type of Work Permit", null, "SELECT", "Employer-Specific|Open Work Permit", true, true, ++o);
        save(t, "LMIA", "lmia_status", "LMIA Status", null, "SELECT", "LMIA Required (has LMIA)|LMIA Exempt|Unknown/Not Sure", true, true, ++o);
        save(t, "LMIA", "lmia_number", "LMIA Number (if applicable)", null, "TEXT", null, false, false, ++o);
        save(t, "Employer", "employer_name", "Employer Legal Name", null, "TEXT", null, true, false, ++o);
        save(t, "Employer", "employer_cra_bn", "Employer CRA Business Number", null, "TEXT", null, false, false, ++o);
        save(t, "Employer", "employer_job_title", "Job Title", null, "TEXT", null, true, false, ++o);
        save(t, "Offer", "wage", "Offered Wage (hourly/annual)", null, "TEXT", null, true, false, ++o);
        save(t, "Offer", "work_location", "Work Location (city, province)", null, "TEXT", null, true, false, ++o);
        save(t, "Offer", "job_duties", "Job Duties", null, "TEXTAREA", null, true, false, ++o);
        save(t, "Offer", "noc_teer", "NOC/TEER Code (if known)", null, "TEXT", null, false, false, ++o);
        save(t, "Applicant Status", "current_status_in_canada", "Current Immigration Status in Canada", null, "SELECT", "Study Permit|Work Permit|Visitor Record|No Status|Not in Canada", false, true, ++o);
        save(t, "Applicant Status", "status_expiry_date", "Current Status Expiry Date", null, "DATE", null, false, false, ++o);
        save(t, "Family", "spouse_accompanying", "Is spouse/partner accompanying?", null, "SELECT", "Yes|No|Not Applicable", false, true, ++o);
        save(t, "Family", "children_accompanying", "Are children accompanying?", null, "SELECT", "Yes|No|Not Applicable", false, true, ++o);
        save(t, "Qualifications", "highest_education", "Highest Level of Education", null, "TEXT", null, false, false, ++o);
        save(t, "Qualifications", "professional_licence", "Any professional licence/certification?", null, "TEXT", null, false, false, ++o);
        save(t, "Qualifications", "years_experience", "Years of Relevant Work Experience", null, "TEXT", null, false, false, ++o);
        save(t, "Prior Refusals", "prior_refusals", "Any prior work permit or visa refusals?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Prior Refusals", "refusal_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
    }

    // ======== LMIA (3.7) ========
    private void seedLMIA() {
        ServiceType t = ServiceType.LMIA;
        int o = 0;
        save(t, "Employer", "employer_legal_name", "Employer Legal Name", null, "TEXT", null, true, false, ++o);
        save(t, "Employer", "employer_operating_name", "Operating/Trade Name", null, "TEXT", null, false, false, ++o);
        save(t, "Employer", "cra_bn", "CRA Business Number", null, "TEXT", null, true, false, ++o);
        save(t, "Employer", "payroll_account", "CRA Payroll Account Number", null, "TEXT", null, true, false, ++o);
        save(t, "Business", "industry", "Industry/Sector", null, "TEXT", null, true, false, ++o);
        save(t, "Business", "business_address", "Business Address", null, "TEXT", null, true, false, ++o);
        save(t, "Business", "num_employees", "Number of Employees", null, "TEXT", null, true, false, ++o);
        save(t, "Business", "revenue_proof_available", "Is revenue/financial proof available?", null, "SELECT", "Yes|No", true, false, ++o);
        save(t, "Job", "job_title", "Job Title", null, "TEXT", null, true, false, ++o);
        save(t, "Job", "wage", "Offered Wage", null, "TEXT", null, true, false, ++o);
        save(t, "Job", "hours_per_week", "Hours Per Week", null, "TEXT", null, true, false, ++o);
        save(t, "Job", "work_location", "Work Location", null, "TEXT", null, true, false, ++o);
        save(t, "Job", "job_duties", "Main Duties", null, "TEXTAREA", null, true, false, ++o);
        save(t, "Job", "noc_teer", "NOC/TEER Code (if known)", null, "TEXT", null, false, false, ++o);
        save(t, "Stream", "lmia_stream", "LMIA Stream", null, "SELECT", "High-Wage|Low-Wage|PR Support|Global Talent Stream|Agricultural|Caregiver|Seasonal", true, true, ++o);
        save(t, "Recruitment", "where_advertised", "Where were job ads posted?", null, "TEXTAREA", null, true, false, ++o);
        save(t, "Recruitment", "ad_start_date", "Job Ad Start Date", null, "DATE", null, true, false, ++o);
        save(t, "Recruitment", "ad_end_date", "Job Ad End Date", null, "DATE", null, true, false, ++o);
        save(t, "Recruitment", "num_applicants", "Number of Applicants Received", null, "TEXT", null, false, false, ++o);
        save(t, "Recruitment", "num_interviewed", "Number of Applicants Interviewed", null, "TEXT", null, false, false, ++o);
        save(t, "Candidate", "candidate_name", "Candidate Full Name", null, "TEXT", null, true, false, ++o);
        save(t, "Candidate", "candidate_qualifications", "Candidate Qualifications", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Candidate", "candidate_experience", "Candidate Relevant Experience", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Compliance", "prior_lmia_history", "Any prior LMIA applications?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Compliance", "prior_inspections", "Any prior ESDC inspections?", null, "SELECT", "Yes|No", false, false, ++o);
        save(t, "Compliance", "recent_layoffs", "Any recent layoffs for this position?", null, "SELECT", "Yes|No", true, true, ++o);
    }

    // ======== CITIZENSHIP (3.8) ========
    private void seedCitizenship() {
        ServiceType t = ServiceType.CITIZENSHIP;
        int o = 0;
        save(t, "Age", "applicant_age_category", "Is the applicant an adult or minor?", null, "SELECT", "Adult (18+)|Minor (under 18)", true, true, ++o);
        save(t, "PR Date", "pr_date", "Date Became Permanent Resident", null, "DATE", null, true, false, ++o);
        save(t, "Absences", "absences_outside_canada", "Have you travelled outside Canada during the eligibility period?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Absences", "absence_details", "If yes, list all trips (country, dates)", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Tax Years", "tax_years_filed", "For which years have you filed Canadian taxes?", null, "TEXT", null, true, false, ++o);
        save(t, "Tax Years", "missing_tax_years", "Are there any missing tax filing years?", null, "SELECT", "Yes|No|Not Sure", false, true, ++o);
        save(t, "Language", "language_proof_available", "Is language proof available?", "Required for applicants 18-54 at time of signing", "SELECT", "Yes|No|Exempt (age)", true, true, ++o);
        save(t, "Language", "language_test_type", "Language Test/Proof Type", null, "TEXT", null, false, false, ++o);
        save(t, "Prohibitions", "criminality", "Any criminal charges, convictions, or ongoing proceedings?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Prohibitions", "removal_order", "Any removal orders?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Prohibitions", "probation", "Currently on probation?", null, "SELECT", "Yes|No", true, false, ++o);
        save(t, "Identity", "available_ids", "What identification documents are available?", null, "TEXTAREA", null, true, false, ++o);
        save(t, "Identity", "name_consistency", "Is your name consistent across all documents?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Past Applications", "prior_citizenship_app", "Any prior citizenship application?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Past Applications", "prior_app_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
    }

    // ======== PR CARD / PRTD (3.9) ========
    private void seedPRCard() {
        ServiceType t = ServiceType.PR_CARD_PRTD;
        int o = 0;
        save(t, "Application Reason", "application_reason", "Reason for Application", null, "SELECT", "Renewal|Replacement|First Card|PR Travel Document (PRTD)", true, true, ++o);
        save(t, "Card Expiry", "pr_card_expiry", "Current PR Card Expiry Date", null, "DATE", null, false, false, ++o);
        save(t, "Travel", "days_outside_canada", "Approximate days outside Canada in last 5 years", null, "TEXT", null, true, true, ++o);
        save(t, "Travel", "travel_details", "List all trips outside Canada (country, dates)", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Lost/Damaged", "card_lost_stolen_damaged", "Was the card lost, stolen, or damaged?", null, "SELECT", "Lost|Stolen|Damaged|Not Applicable", false, true, ++o);
        save(t, "Lost/Damaged", "police_report_number", "Police Report/Incident Number (if applicable)", null, "TEXT", null, false, false, ++o);
        save(t, "Urgency", "urgent_processing", "Do you need urgent processing?", null, "SELECT", "Yes|No", false, false, ++o);
        save(t, "Urgency", "urgency_reason", "If yes, explain the urgency", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Name Changes", "name_gender_changes", "Any legal name or gender changes since PR was granted?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Name Changes", "change_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Documents", "copr_available", "Is your COPR (Confirmation of PR) available?", null, "SELECT", "Yes|No", true, false, ++o);
        save(t, "Documents", "record_of_landing_available", "Is your Record of Landing available?", null, "SELECT", "Yes|No|Not Sure", false, false, ++o);
        save(t, "Documents", "passport_available", "Is your current passport available?", null, "SELECT", "Yes|No", true, false, ++o);
    }

    // ======== PGWP (3.10) ========
    private void seedPGWP() {
        ServiceType t = ServiceType.PGWP;
        int o = 0;
        save(t, "Graduation", "completion_date", "Program Completion Date", null, "DATE", null, true, false, ++o);
        save(t, "Graduation", "transcript_available", "Is your final transcript available?", null, "SELECT", "Yes|No|Pending", true, true, ++o);
        save(t, "Graduation", "completion_letter_available", "Is your completion/graduation letter available?", null, "SELECT", "Yes|No|Pending", true, true, ++o);
        save(t, "School", "dli_name", "Designated Learning Institution Name", null, "TEXT", null, true, false, ++o);
        save(t, "School", "program_length", "Program Length (months)", null, "TEXT", null, true, false, ++o);
        save(t, "School", "program_level", "Program Level", null, "SELECT", "Certificate|Diploma|Associate|Bachelor|Master|PhD", true, false, ++o);
        save(t, "Status", "study_permit_expiry", "Study Permit Expiry Date", null, "DATE", null, true, true, ++o);
        save(t, "Status", "current_status", "Current Immigration Status", null, "SELECT", "Valid Study Permit|Implied Status|Visitor Record|Other", true, true, ++o);
        save(t, "Language", "language_test_taken", "Have you taken a language test?", "Required for some PGWP applications", "SELECT", "Yes|No|Exempt", true, true, ++o);
        save(t, "Language", "language_test_date", "Language Test Date", null, "DATE", null, false, false, ++o);
        save(t, "Language", "language_scores", "Language Test Scores", null, "TEXT", null, false, false, ++o);
        save(t, "Study History", "full_time_all_terms", "Were all terms full-time?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "Study History", "authorized_leaves", "Any authorized leaves or part-time terms?", null, "SELECT", "Yes|No", false, false, ++o);
        save(t, "Study History", "program_transfers", "Any program or school transfers?", null, "SELECT", "Yes|No", false, true, ++o);
        save(t, "Passport", "passport_expiry", "Passport Expiry Date", null, "DATE", null, true, true, ++o);
    }

    // ======== SUPER VISA (3.11) ========
    private void seedSuperVisa() {
        ServiceType t = ServiceType.SUPER_VISA;
        int o = 0;
        save(t, "Applicant", "applicant_name", "Applicant Full Name", null, "TEXT", null, true, false, ++o);
        save(t, "Applicant", "applicant_relationship", "Relationship to Host", null, "SELECT", "Parent|Grandparent", true, true, ++o);
        save(t, "Applicant", "applicant_passport_expiry", "Passport Expiry Date", null, "DATE", null, true, false, ++o);
        save(t, "Applicant", "applicant_country_residence", "Country of Residence", null, "TEXT", null, true, false, ++o);
        save(t, "Host", "host_name", "Host Full Name", null, "TEXT", null, true, false, ++o);
        save(t, "Host", "host_status", "Host Immigration Status", null, "SELECT", "Canadian Citizen|Permanent Resident", true, true, ++o);
        save(t, "Host", "host_relationship_proof", "What proof of relationship is available?", null, "TEXT", null, true, false, ++o);
        save(t, "Host", "household_size", "Total Household Size (including applicant)", null, "TEXT", null, true, false, ++o);
        save(t, "Finances", "host_income_docs_available", "Are host income documents available (NOA, employment letter)?", null, "SELECT", "Yes|No|Partial", true, true, ++o);
        save(t, "Finances", "host_support_evidence", "What financial support evidence is available?", null, "TEXTAREA", null, false, false, ++o);
        save(t, "Insurance", "has_medical_insurance", "Has qualifying medical insurance been obtained?", null, "SELECT", "Yes|No|In Progress", true, true, ++o);
        save(t, "Insurance", "insurance_policy_details", "Insurance Provider and Policy Number", null, "TEXT", null, false, false, ++o);
        save(t, "Insurance", "insurance_coverage_amount", "Coverage Amount", null, "TEXT", null, false, false, ++o);
        save(t, "Purpose/Duration", "planned_arrival", "Planned Arrival Date", null, "DATE", null, false, false, ++o);
        save(t, "Purpose/Duration", "planned_stay_location", "Where in Canada will applicant stay?", null, "TEXT", null, false, false, ++o);
        save(t, "History", "prior_refusals", "Any prior visa refusals?", null, "SELECT", "Yes|No", true, true, ++o);
        save(t, "History", "refusal_details", "If yes, provide details", null, "TEXTAREA", null, false, false, ++o);
        save(t, "History", "previous_travel_history", "Previous travel history", null, "TEXTAREA", null, false, false, ++o);
        save(t, "History", "medical_concerns", "Any medical concerns?", null, "SELECT", "Yes|No", false, false, ++o);
    }

    private void save(ServiceType type, String section, String key, String label,
                      String helpText, String inputType, String options,
                      boolean required, boolean trigger, int order) {
        repo.save(IntakeQuestionTemplate.builder()
                .serviceType(type)
                .sectionName(section)
                .questionKey(key)
                .questionLabel(label)
                .helpText(helpText)
                .inputType(inputType)
                .options(options)
                .required(required)
                .isTriggerQuestion(trigger)
                .sortOrder(order)
                .build());
    }
}
