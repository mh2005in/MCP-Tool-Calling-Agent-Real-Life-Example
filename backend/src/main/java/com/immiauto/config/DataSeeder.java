package com.immiauto.config;

import com.immiauto.entity.AppUser;
import com.immiauto.entity.ChecklistTemplate;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.enums.ServiceType;
import com.immiauto.repository.AppUserRepository;
import com.immiauto.repository.ChecklistTemplateRepository;
import com.immiauto.repository.ConsultantRepository;
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
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final ChecklistTemplateRepository templateRepo;
    private final ConsultantRepository consultantRepo;
    private final AppUserRepository appUserRepo;

    @Override
    public void run(String... args) {
        if (templateRepo.count() > 0) return;

        log.info("Seeding checklist templates...");

        seedStudyPermitTemplates();
        seedVisitorVisaTemplates();
        seedSpousalSponsorshipTemplates();
        seedExpressEntryTemplates();
        seedWorkPermitTemplates();
        seedLMIATemplates();
        seedCitizenshipTemplates();
        seedPRCardTemplates();
        seedPGWPTemplates();
        seedSuperVisaTemplates();

        // --- Demo consultant ---
        if (consultantRepo.count() == 0) {
            Consultant demoConsultant = consultantRepo.save(Consultant.builder()
                    .fullName("Demo Consultant")
                    .email("demo@immiauto.ca")
                    .licenseNumber("R000000")
                    .companyName("Demo Immigration Services")
                    .admin(true)
                    .active(true)
                    .build());

            // Demo Entra identity -> app_user mapping (demo user's real Entra oid).
            appUserRepo.save(AppUser.builder()
                    .externalSubject("0186e7d8-a81f-4526-b1bc-b4a5726af370")
                    .email(demoConsultant.getEmail())
                    .displayName(demoConsultant.getFullName())
                    .role(AppRole.CONSULTANT_OWNER)
                    .status(AppUserStatus.ACTIVE)
                    .consultantId(demoConsultant.getId())
                    .build());
        }

        log.info("Seed data loaded.");
    }

    private void seedStudyPermitTemplates() {
        List.of(
            template(ServiceType.STUDY_PERMIT, "Identity", "Passport biographical page",
                "Clear scan of your passport page showing your photo, name, and passport number.", true, 1),
            template(ServiceType.STUDY_PERMIT, "Identity", "National ID card",
                "Front and back of your national identity card, if applicable.", false, 2),
            template(ServiceType.STUDY_PERMIT, "Identity", "Birth certificate",
                "Official birth certificate with English or French translation if needed.", false, 3),
            template(ServiceType.STUDY_PERMIT, "Education", "Letter of acceptance from DLI",
                "Original letter of acceptance from a Designated Learning Institution in Canada.", true, 4),
            template(ServiceType.STUDY_PERMIT, "Education", "Previous transcripts",
                "Academic transcripts from your most recent educational institution.", true, 5),
            template(ServiceType.STUDY_PERMIT, "Education", "Previous diplomas or degrees",
                "Copies of diplomas or degree certificates you have earned.", true, 6),
            template(ServiceType.STUDY_PERMIT, "Attestation", "Provincial Attestation Letter (PAL/TAL)",
                "Attestation letter from the province where your DLI is located.", true, 7),
            conditionalTemplate(ServiceType.STUDY_PERMIT, "Attestation", "Quebec Acceptance Certificate (CAQ)",
                "Certificat d'acceptation du Québec, required for Quebec institutions.", true, 8,
                "Required if school is in Quebec"),
            template(ServiceType.STUDY_PERMIT, "Financial", "Proof of funds - bank statements",
                "Bank statements from the last 4 months showing sufficient funds for tuition and living expenses.", true, 9),
            template(ServiceType.STUDY_PERMIT, "Financial", "Tuition payment receipt",
                "Receipt showing tuition has been paid or deposit made to the school.", false, 10),
            template(ServiceType.STUDY_PERMIT, "Financial", "Scholarship or funding letter",
                "Letter confirming scholarship, bursary, or other funding if applicable.", false, 11),
            template(ServiceType.STUDY_PERMIT, "Financial", "GIC certificate",
                "Guaranteed Investment Certificate from a participating financial institution, if applicable.", false, 12),
            conditionalTemplate(ServiceType.STUDY_PERMIT, "Financial", "Sponsor financial documents",
                "Bank statements, employment letter, and income proof from your financial sponsor.", false, 13,
                "Required if a sponsor is providing financial support"),
            template(ServiceType.STUDY_PERMIT, "Language", "Language test results (IELTS/CELPIP/TEF/TCF)",
                "Official language test results, if required by the program or for SDS.", false, 14),
            template(ServiceType.STUDY_PERMIT, "Purpose", "Study plan / statement of purpose",
                "Written explanation of why you chose this program and your plans after graduation.", true, 15),
            template(ServiceType.STUDY_PERMIT, "Ties to Home", "Proof of ties to home country",
                "Employment letter, property documents, family obligations, or other evidence of intent to return.", false, 16),
            template(ServiceType.STUDY_PERMIT, "Immigration History", "Previous visa refusal letter",
                "If you have been refused a visa before, provide the refusal letter.", false, 17),
            template(ServiceType.STUDY_PERMIT, "Immigration History", "Current visa or permit (if in Canada)",
                "Copy of current study or work permit, if applying from inside Canada.", false, 18),
            template(ServiceType.STUDY_PERMIT, "Immigration History", "Previous travel history",
                "Copies of previous visas and travel stamps from past international travel.", false, 19),
            template(ServiceType.STUDY_PERMIT, "Civil Status", "Marriage certificate",
                "If married, provide your marriage certificate.", false, 20),
            conditionalTemplate(ServiceType.STUDY_PERMIT, "Family", "Spouse passport and documents",
                "Passport and supporting documents for accompanying spouse.", false, 21,
                "Required if spouse is accompanying"),
            conditionalTemplate(ServiceType.STUDY_PERMIT, "Family", "Children birth certificates and passports",
                "Birth certificates and passports for accompanying dependent children.", false, 22,
                "Required if children are accompanying"),
            template(ServiceType.STUDY_PERMIT, "Representative", "Use of Representative form (IMM 5476)",
                "Signed form authorizing your immigration consultant to act on your behalf.", true, 23),
            template(ServiceType.STUDY_PERMIT, "Photos", "Passport-size photos",
                "Two recent passport-size photos meeting IRCC specifications.", true, 24),
            template(ServiceType.STUDY_PERMIT, "Country-specific", "Police clearance certificate",
                "Police certificate from your country or any country you lived in for 6+ months since age 18.", false, 25),
            template(ServiceType.STUDY_PERMIT, "Country-specific", "Medical exam results",
                "Results of immigration medical exam, if required based on your country.", false, 26)
        ).forEach(templateRepo::save);
    }

    private void seedVisitorVisaTemplates() {
        List.of(
            template(ServiceType.VISITOR_VISA, "Identity", "Passport biographical page",
                "Clear scan of your passport showing photo, name, and passport number.", true, 1),
            template(ServiceType.VISITOR_VISA, "Identity", "National ID card",
                "Front and back scan of national identity card.", false, 2),
            template(ServiceType.VISITOR_VISA, "Financial", "Bank statements (3 months)",
                "Bank statements from the last 3 months showing sufficient funds.", true, 3),
            template(ServiceType.VISITOR_VISA, "Financial", "Employment letter",
                "Letter from employer confirming your position, salary, and approved leave.", true, 4),
            template(ServiceType.VISITOR_VISA, "Financial", "Property or asset documents",
                "Documents showing property ownership, investments, or other ties to home country.", false, 5),
            conditionalTemplate(ServiceType.VISITOR_VISA, "Host Documents", "Host invitation letter",
                "Formal letter from host inviting you to Canada, with dates, purpose, and accommodation details.", false, 6,
                "Required if visiting a host in Canada"),
            conditionalTemplate(ServiceType.VISITOR_VISA, "Host Documents", "Host proof of status",
                "Copy of host's Canadian citizenship, PR card, or valid immigration status.", false, 7,
                "Required if visiting a host in Canada"),
            conditionalTemplate(ServiceType.VISITOR_VISA, "Host Documents", "Host proof of income/finances",
                "Host's employment letter, NOA, or bank statements if host is financially supporting visit.", false, 8,
                "Required if host is funding the trip"),
            template(ServiceType.VISITOR_VISA, "Purpose of Visit", "Travel itinerary",
                "Flight booking or travel plan showing intended dates.", false, 9),
            template(ServiceType.VISITOR_VISA, "Purpose of Visit", "Hotel reservations",
                "Accommodation booking confirmations.", false, 10),
            conditionalTemplate(ServiceType.VISITOR_VISA, "Purpose of Visit", "Business invitation / conference registration",
                "Business invitation letter or conference registration for business visitors.", false, 11,
                "Required for business visitors"),
            conditionalTemplate(ServiceType.VISITOR_VISA, "Purpose of Visit", "Event tickets or registration",
                "Tickets, registration confirmation, or event details.", false, 12,
                "Required for event visitors"),
            template(ServiceType.VISITOR_VISA, "Status Documents", "Current immigration status (if in Canada)",
                "Copy of current visa, permit, or visitor record if applying from inside Canada.", false, 13),
            template(ServiceType.VISITOR_VISA, "Ties to Home Country", "Business registration",
                "Business registration or ownership documents if self-employed.", false, 14),
            template(ServiceType.VISITOR_VISA, "Ties to Home Country", "Proof of family ties",
                "Documents showing family members remaining in your home country.", false, 15),
            template(ServiceType.VISITOR_VISA, "Civil Status", "Marriage certificate",
                "If married, provide marriage certificate.", false, 16),
            template(ServiceType.VISITOR_VISA, "Civil Status", "Children birth certificates",
                "Birth certificates of dependent children, if applicable.", false, 17),
            template(ServiceType.VISITOR_VISA, "Travel History", "Previous visas and travel stamps",
                "Copies of previous visas and passport stamps from past travel.", false, 18),
            conditionalTemplate(ServiceType.VISITOR_VISA, "Immigration History", "Previous refusal explanation",
                "Written explanation and supporting documents for any prior visa refusals.", false, 19,
                "Required if previously refused a visa"),
            template(ServiceType.VISITOR_VISA, "Photos", "Passport-size photos",
                "Two recent passport-size photos meeting IRCC specifications.", true, 20)
        ).forEach(templateRepo::save);
    }

    private void seedSpousalSponsorshipTemplates() {
        List.of(
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Identity - Sponsor", "Sponsor passport",
                "Clear scan of sponsor's Canadian passport or PR card.", true, 1),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Identity - Sponsor", "Sponsor proof of status",
                "PR card, citizenship certificate, or Canadian passport.", true, 2),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Identity - Sponsor", "Sponsor birth certificate",
                "Official birth certificate of the sponsor.", false, 3),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Identity - Applicant", "Applicant passport",
                "Clear scan of applicant's passport biographical page.", true, 4),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Identity - Applicant", "Applicant birth certificate",
                "Official birth certificate of the applicant.", true, 5),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship", "Marriage certificate",
                "Official marriage certificate.", true, 6),
            conditionalTemplate(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship", "Statutory declaration of common-law union",
                "Sworn declaration confirming 12+ months of continuous cohabitation.", true, 7,
                "Required for common-law relationships"),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship", "Relationship timeline narrative",
                "Written narrative of how you met, your relationship history, and milestones.", true, 8),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Photos", "Photos together",
                "Photos at different times, with family, at events, in different locations.", true, 9),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Communication", "Chat/messaging history",
                "Screenshots of text messages, WhatsApp, Facebook Messenger, or other messaging apps.", true, 10),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Communication", "Call logs and video call history",
                "Phone call logs and video call screenshots/history.", true, 11),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Travel", "Travel records together",
                "Flight bookings, boarding passes, hotel reservations, or passport stamps from trips together.", false, 12),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Financial", "Joint bank accounts or shared finances",
                "Joint bank account statements or evidence of shared financial responsibilities.", false, 13),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Financial", "Joint lease, utilities, or insurance",
                "Shared lease agreements, utility bills, or insurance policies showing both names.", false, 14),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Relationship Evidence - Third Party", "Letters from friends and family",
                "Sworn affidavits or letters from people who know the relationship.", false, 15),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Financial - Sponsor", "Tax returns / NOA",
                "Notice of Assessment from CRA for the most recent tax year.", true, 16),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Financial - Sponsor", "Employment letter",
                "Letter from sponsor's employer confirming employment and salary.", false, 17),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Financial - Sponsor", "Bank statements (3 months)",
                "Sponsor's bank statements for the last 3 months.", false, 18),
            conditionalTemplate(ServiceType.SPOUSAL_SPONSORSHIP, "Dependents", "Dependent children birth certificates",
                "Birth certificates for all dependent children included in the application.", false, 19,
                "Required if there are dependent children"),
            conditionalTemplate(ServiceType.SPOUSAL_SPONSORSHIP, "Dependents", "Custody or adoption documents",
                "Court orders, custody agreements, or adoption certificates.", false, 20,
                "Required if there are custody arrangements"),
            conditionalTemplate(ServiceType.SPOUSAL_SPONSORSHIP, "Quebec", "Quebec undertaking (CSQ forms)",
                "Quebec-specific sponsorship undertaking forms.", false, 21,
                "Required if sponsor lives in Quebec"),
            conditionalTemplate(ServiceType.SPOUSAL_SPONSORSHIP, "Previous Sponsorship", "Previous sponsorship proof of completion",
                "Proof that 3-year undertaking from a previous sponsorship has ended.", false, 22,
                "Required if sponsor has sponsored before"),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Country-specific", "Police clearance - applicant",
                "Police certificate from applicant's country and any country lived in 6+ months since age 18.", true, 23),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Country-specific", "Police clearance - sponsor",
                "Police certificate for the sponsor, if applicable.", false, 24),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Country-specific", "Medical exam results",
                "Immigration medical exam results for the applicant and dependents.", true, 25),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Photos", "Passport-size photos",
                "Two recent passport-size photos meeting IRCC specifications.", true, 26),
            template(ServiceType.SPOUSAL_SPONSORSHIP, "Representative", "Use of Representative form (IMM 5476)",
                "Signed form authorizing your immigration consultant to act on your behalf.", true, 27)
        ).forEach(templateRepo::save);
    }

    // --- 4.4 Express Entry ---
    private void seedExpressEntryTemplates() {
        List.of(
            template(ServiceType.EXPRESS_ENTRY, "Identity", "Passport biographical page", "Clear scan of passport.", true, 1),
            template(ServiceType.EXPRESS_ENTRY, "Identity", "Birth certificate", "Official birth certificate.", false, 2),
            template(ServiceType.EXPRESS_ENTRY, "Language", "Language test results (IELTS/CELPIP/TEF/TCF)", "Official language test results for primary applicant.", true, 3),
            conditionalTemplate(ServiceType.EXPRESS_ENTRY, "Language", "Spouse language test results", "Language test results for accompanying spouse, if applicable.", false, 4, "Required if spouse is accompanying"),
            template(ServiceType.EXPRESS_ENTRY, "Education", "Canadian transcripts and diplomas", "Transcripts and diplomas from Canadian educational institutions.", false, 5),
            conditionalTemplate(ServiceType.EXPRESS_ENTRY, "Education", "Educational Credential Assessment (ECA)", "WES, IQAS, or other designated ECA report for foreign education.", true, 6, "Required for foreign education credentials"),
            template(ServiceType.EXPRESS_ENTRY, "Education", "Foreign transcripts and diplomas", "Transcripts and diplomas from foreign educational institutions.", false, 7),
            template(ServiceType.EXPRESS_ENTRY, "Work Experience", "Employment reference letters", "Detailed reference letters for each qualifying work period: duties, hours, dates, salary.", true, 8),
            template(ServiceType.EXPRESS_ENTRY, "Work Experience", "Pay stubs or tax documents", "Pay stubs, T4s, or equivalent to corroborate employment claims.", false, 9),
            template(ServiceType.EXPRESS_ENTRY, "Financial", "Proof of settlement funds", "Bank statements, investment statements, or GIC showing required funds.", true, 10),
            conditionalTemplate(ServiceType.EXPRESS_ENTRY, "Job Offer", "Valid job offer letter", "Signed employment offer from a Canadian employer.", false, 11, "Required if claiming job offer points"),
            conditionalTemplate(ServiceType.EXPRESS_ENTRY, "Job Offer", "LMIA confirmation (if applicable)", "Labour Market Impact Assessment for the job offer.", false, 12, "Required if job offer requires LMIA"),
            conditionalTemplate(ServiceType.EXPRESS_ENTRY, "PNP", "Provincial Nomination Certificate", "Official nomination certificate from a Canadian province or territory.", false, 13, "Required for PNP Express Entry stream"),
            template(ServiceType.EXPRESS_ENTRY, "Police Certificates", "Police certificate - country of citizenship", "Police certificate from your country of citizenship.", true, 14),
            template(ServiceType.EXPRESS_ENTRY, "Police Certificates", "Police certificate - countries lived 6+ months", "Police certificates from every country you lived in 6+ months since age 18.", true, 15),
            template(ServiceType.EXPRESS_ENTRY, "Medical", "Immigration medical exam (IME)", "Medical exam from an IRCC-designated panel physician.", true, 16),
            template(ServiceType.EXPRESS_ENTRY, "Civil Status", "Marriage certificate or divorce decree", "Marriage certificate, or divorce/annulment decree if previously married.", false, 17),
            conditionalTemplate(ServiceType.EXPRESS_ENTRY, "Dependents", "Dependent children documents", "Birth certificates, passports, and custody documents for dependent children.", false, 18, "Required if including dependents"),
            template(ServiceType.EXPRESS_ENTRY, "Photos", "Passport-size photos", "Photos meeting IRCC specifications for applicant and all family members.", true, 19),
            template(ServiceType.EXPRESS_ENTRY, "Representative", "Use of Representative form (IMM 5476)", "Signed form authorizing consultant.", true, 20)
        ).forEach(templateRepo::save);
    }

    // --- 4.5 Work Permit ---
    private void seedWorkPermitTemplates() {
        List.of(
            template(ServiceType.WORK_PERMIT, "Identity", "Passport biographical page", "Clear scan of passport.", true, 1),
            template(ServiceType.WORK_PERMIT, "Job/Employer", "Job offer letter", "Signed employment offer with title, duties, wage, hours, and location.", true, 2),
            template(ServiceType.WORK_PERMIT, "Job/Employer", "Employer LMIA confirmation", "Positive LMIA from ESDC, if applicable.", false, 3),
            template(ServiceType.WORK_PERMIT, "Job/Employer", "Employer compliance fee receipt", "Proof employer paid the compliance fee ($230).", false, 4),
            template(ServiceType.WORK_PERMIT, "Job/Employer", "Employment contract", "Signed contract with terms and conditions.", true, 5),
            template(ServiceType.WORK_PERMIT, "Qualifications", "Resume / CV", "Current resume showing relevant experience.", true, 6),
            template(ServiceType.WORK_PERMIT, "Qualifications", "Educational credentials", "Diplomas, degrees, or certificates relevant to the job.", false, 7),
            template(ServiceType.WORK_PERMIT, "Qualifications", "Professional licence or certification", "Licence or certification required for the occupation.", false, 8),
            template(ServiceType.WORK_PERMIT, "Experience", "Previous employment reference letters", "Reference letters from previous employers detailing duties and dates.", false, 9),
            conditionalTemplate(ServiceType.WORK_PERMIT, "Status", "Current Canadian status documents", "Current work permit, study permit, or visitor record.", false, 10, "Required if applying from inside Canada"),
            conditionalTemplate(ServiceType.WORK_PERMIT, "Status", "Status extension/restoration application", "Proof of pending status application if status has expired.", false, 11, "Required if current status has expired"),
            conditionalTemplate(ServiceType.WORK_PERMIT, "Family", "Spouse open work permit documents", "Spouse passport, marriage certificate, and SOWP application documents.", false, 12, "Required if spouse is applying for open work permit"),
            conditionalTemplate(ServiceType.WORK_PERMIT, "Family", "Dependent children documents", "Birth certificates and passports for accompanying children.", false, 13, "Required if children are accompanying"),
            template(ServiceType.WORK_PERMIT, "Financial", "Proof of funds for initial settlement", "Bank statements showing funds for initial period in Canada.", false, 14),
            template(ServiceType.WORK_PERMIT, "Photos", "Passport-size photos", "Photos meeting IRCC specifications.", true, 15),
            template(ServiceType.WORK_PERMIT, "Country-specific", "Police clearance certificate", "Police certificate if required based on country.", false, 16),
            template(ServiceType.WORK_PERMIT, "Country-specific", "Medical exam results", "Immigration medical exam if required.", false, 17),
            template(ServiceType.WORK_PERMIT, "Representative", "Use of Representative form (IMM 5476)", "Signed form authorizing consultant.", true, 18)
        ).forEach(templateRepo::save);
    }

    // --- 4.6 LMIA ---
    private void seedLMIATemplates() {
        List.of(
            template(ServiceType.LMIA, "Employer Identity", "Business licence or registration", "Provincial or federal business registration documents.", true, 1),
            template(ServiceType.LMIA, "Employer Identity", "CRA Business Number confirmation", "Proof of CRA Business Number.", true, 2),
            template(ServiceType.LMIA, "Employer Identity", "Articles of incorporation", "Corporate registration or articles of incorporation.", false, 3),
            template(ServiceType.LMIA, "Business Legitimacy", "T4 Summary (previous year)", "T4 Summary showing payroll for Canadian employees.", true, 4),
            template(ServiceType.LMIA, "Business Legitimacy", "Financial statements or tax returns", "Business financial statements or corporate tax returns.", true, 5),
            template(ServiceType.LMIA, "Business Legitimacy", "Commercial lease or property proof", "Proof of business premises.", true, 6),
            template(ServiceType.LMIA, "Business Legitimacy", "Business insurance", "Proof of WCB/WSIB or equivalent workplace insurance.", false, 7),
            template(ServiceType.LMIA, "Recruitment", "Job advertisement screenshots", "Screenshots of all job ads with dates, platforms, and content.", true, 8),
            template(ServiceType.LMIA, "Recruitment", "Job Bank posting confirmation", "Proof of posting on the Government of Canada Job Bank for minimum 4 weeks.", true, 9),
            template(ServiceType.LMIA, "Recruitment", "Recruitment summary report", "Summary of number of applicants, interviews conducted, and reasons for non-hire.", true, 10),
            template(ServiceType.LMIA, "Recruitment", "Interview records", "Records of interviews conducted with Canadian applicants.", false, 11),
            template(ServiceType.LMIA, "Transition Plan", "Transition plan document", "Written plan to reduce reliance on temporary foreign workers.", true, 12),
            conditionalTemplate(ServiceType.LMIA, "Provincial", "Provincial attestation or requirements", "Province-specific attestation or documentation requirements.", false, 13, "Required by some provinces"),
            template(ServiceType.LMIA, "Worker Documents", "Candidate resume / CV", "Resume of the foreign worker being hired.", true, 14),
            template(ServiceType.LMIA, "Worker Documents", "Candidate credentials and qualifications", "Proof of qualifications relevant to the position.", false, 15),
            template(ServiceType.LMIA, "Fees", "LMIA processing fee payment receipt ($1,000)", "Proof of payment of the LMIA processing fee.", true, 16),
            template(ServiceType.LMIA, "Fees", "Employer compliance fee receipt ($230)", "Proof of compliance fee payment.", true, 17)
        ).forEach(templateRepo::save);
    }

    // --- 4.7 Citizenship ---
    private void seedCitizenshipTemplates() {
        List.of(
            template(ServiceType.CITIZENSHIP, "PR Proof", "PR card (front and back)", "Clear scan of both sides of current or most recent PR card.", true, 1),
            template(ServiceType.CITIZENSHIP, "PR Proof", "Confirmation of Permanent Residence (COPR)", "Original COPR or Record of Landing.", true, 2),
            template(ServiceType.CITIZENSHIP, "Identity", "Passport biographical page", "Current passport biographical page.", true, 3),
            template(ServiceType.CITIZENSHIP, "Identity", "Two passport-size photos", "Photos meeting IRCC citizenship application specifications.", true, 4),
            template(ServiceType.CITIZENSHIP, "Travel History", "Travel journal / absence log", "Detailed log of all trips outside Canada during eligibility period with dates and destinations.", true, 5),
            template(ServiceType.CITIZENSHIP, "Travel History", "Passport stamps and boarding passes", "Evidence supporting declared travel dates.", false, 6),
            template(ServiceType.CITIZENSHIP, "Language", "Language proof (IELTS/CELPIP/TEF/TCF or equivalent)", "Official language test results or proof of CLB 4+ in English or French.", true, 7),
            template(ServiceType.CITIZENSHIP, "Tax Filing", "Notice of Assessment (NOA) - 3 tax years", "NOAs from CRA for 3 of the last 5 tax years within eligibility period.", true, 8),
            template(ServiceType.CITIZENSHIP, "Tax Filing", "T1 General tax returns", "T1 returns corresponding to the NOAs provided.", false, 9),
            template(ServiceType.CITIZENSHIP, "Residence", "Proof of address in Canada", "Utility bills, lease agreements, or bank statements showing Canadian address.", false, 10),
            conditionalTemplate(ServiceType.CITIZENSHIP, "Name Change", "Legal name change certificate", "Court order or official document showing legal name change.", false, 11, "Required if name has changed since becoming PR"),
            conditionalTemplate(ServiceType.CITIZENSHIP, "Criminal", "Court records or discharge documents", "Records of criminal charges, convictions, or absolute/conditional discharge.", false, 12, "Required if applicant has criminal history"),
            conditionalTemplate(ServiceType.CITIZENSHIP, "Minor Applicant", "Parent/guardian consent and ID", "Both parents' ID and signed consent for minor citizenship application.", false, 13, "Required for applicants under 18"),
            template(ServiceType.CITIZENSHIP, "Representative", "Use of Representative form", "Signed form authorizing consultant.", true, 14)
        ).forEach(templateRepo::save);
    }

    // --- 4.8 PR Card / PRTD ---
    private void seedPRCardTemplates() {
        List.of(
            template(ServiceType.PR_CARD_PRTD, "PR Proof", "Confirmation of Permanent Residence (COPR)", "Original COPR or Record of Landing.", true, 1),
            template(ServiceType.PR_CARD_PRTD, "PR Proof", "Previous PR card (if available)", "Most recent PR card, even if expired.", false, 2),
            template(ServiceType.PR_CARD_PRTD, "Identity", "Passport biographical page", "Current valid passport.", true, 3),
            template(ServiceType.PR_CARD_PRTD, "Identity", "Two passport-size photos", "Photos meeting IRCC specifications.", true, 4),
            template(ServiceType.PR_CARD_PRTD, "Residence", "Proof of Canadian address", "Utility bill, lease, or government correspondence showing current address.", true, 5),
            template(ServiceType.PR_CARD_PRTD, "Residence", "Travel history log (5 years)", "Detailed log of all absences from Canada in the last 5 years.", true, 6),
            template(ServiceType.PR_CARD_PRTD, "Residence", "Passport stamps and travel evidence", "Supporting evidence for declared travel dates.", false, 7),
            conditionalTemplate(ServiceType.PR_CARD_PRTD, "Lost/Stolen/Damaged", "Police report", "Police report or incident number for lost or stolen card.", false, 8, "Required if card was lost or stolen"),
            conditionalTemplate(ServiceType.PR_CARD_PRTD, "Lost/Stolen/Damaged", "Damaged card scan", "Scan of the damaged card.", false, 9, "Required if card is damaged"),
            conditionalTemplate(ServiceType.PR_CARD_PRTD, "Name Change", "Legal name or gender change documents", "Court order, marriage certificate, or official change document.", false, 10, "Required if name or gender changed since PR was granted"),
            conditionalTemplate(ServiceType.PR_CARD_PRTD, "PRTD", "Proof of ties to Canada", "Evidence of home, family, employment, or financial ties to Canada.", false, 11, "Required for PR Travel Document applications"),
            conditionalTemplate(ServiceType.PR_CARD_PRTD, "PRTD", "Explanation of absence from Canada", "Written explanation for extended absence from Canada.", false, 12, "Required for PRTD when outside Canada"),
            template(ServiceType.PR_CARD_PRTD, "Representative", "Use of Representative form", "Signed form authorizing consultant.", true, 13)
        ).forEach(templateRepo::save);
    }

    // --- 4.9 PGWP ---
    private void seedPGWPTemplates() {
        List.of(
            template(ServiceType.PGWP, "Identity", "Passport biographical page", "Current valid passport.", true, 1),
            template(ServiceType.PGWP, "Study Completion", "Official completion letter from DLI", "Letter from school confirming program completion and graduation date.", true, 2),
            template(ServiceType.PGWP, "Study Completion", "Final transcript", "Official final transcript showing all courses and grades.", true, 3),
            template(ServiceType.PGWP, "Study Completion", "Diploma or degree certificate", "Copy of diploma or degree, if already issued.", false, 4),
            template(ServiceType.PGWP, "School/Program", "Letter of acceptance (original)", "Original letter of acceptance from the DLI.", false, 5),
            template(ServiceType.PGWP, "Current Status", "Current study permit", "Copy of valid study permit.", true, 6),
            conditionalTemplate(ServiceType.PGWP, "Current Status", "Implied status proof", "Proof of implied status if study permit expired but PGWP applied within 90 days.", false, 7, "Required if study permit has expired"),
            conditionalTemplate(ServiceType.PGWP, "Language", "Language test results", "IELTS, CELPIP, TEF, or TCF results if required for field of study.", false, 8, "Required based on graduation date and program"),
            conditionalTemplate(ServiceType.PGWP, "Prior Study", "Transfer letters from previous institutions", "Letters from all institutions attended if programs were transferred.", false, 9, "Required if student transferred between programs or schools"),
            template(ServiceType.PGWP, "Photos", "Passport-size photos", "Photos meeting IRCC specifications.", true, 10),
            template(ServiceType.PGWP, "Representative", "Use of Representative form", "Signed form authorizing consultant.", true, 11)
        ).forEach(templateRepo::save);
    }

    // --- 4.10 Super Visa ---
    private void seedSuperVisaTemplates() {
        List.of(
            template(ServiceType.SUPER_VISA, "Identity - Applicant", "Passport biographical page", "Applicant's current valid passport.", true, 1),
            template(ServiceType.SUPER_VISA, "Relationship", "Proof of relationship to host", "Birth certificate, marriage certificate, or other documents proving parent/grandparent relationship.", true, 2),
            template(ServiceType.SUPER_VISA, "Host Documents", "Host proof of citizenship or PR", "Host's Canadian passport, citizenship certificate, or PR card.", true, 3),
            template(ServiceType.SUPER_VISA, "Host Documents", "Host invitation letter", "Formal invitation letter from host with details of stay.", true, 4),
            template(ServiceType.SUPER_VISA, "Host Finances", "Host Notice of Assessment (NOA)", "CRA Notice of Assessment meeting LICO requirement for household size.", true, 5),
            template(ServiceType.SUPER_VISA, "Host Finances", "Host employment letter", "Letter confirming host's employment, position, and salary.", true, 6),
            template(ServiceType.SUPER_VISA, "Host Finances", "Host bank statements (3 months)", "Host's recent bank statements.", false, 7),
            template(ServiceType.SUPER_VISA, "Medical Insurance", "Private medical insurance policy", "Proof of private medical insurance from a Canadian company, valid 1+ year, minimum $100,000 coverage.", true, 8),
            template(ServiceType.SUPER_VISA, "Medical Insurance", "Insurance coverage details", "Policy details showing coverage amount, period, and emergency medical coverage.", true, 9),
            template(ServiceType.SUPER_VISA, "Medical", "Immigration medical exam results", "Medical exam from IRCC-designated panel physician.", true, 10),
            template(ServiceType.SUPER_VISA, "Travel History", "Previous visas and travel stamps", "Copies of previous visas and passport stamps.", false, 11),
            conditionalTemplate(ServiceType.SUPER_VISA, "Immigration History", "Previous refusal explanation", "Written explanation for any prior visa refusals.", false, 12, "Required if previously refused a visa"),
            template(ServiceType.SUPER_VISA, "Photos", "Passport-size photos", "Photos meeting IRCC specifications.", true, 13),
            template(ServiceType.SUPER_VISA, "Representative", "Use of Representative form", "Signed form authorizing consultant.", true, 14)
        ).forEach(templateRepo::save);
    }

    private ChecklistTemplate template(ServiceType type, String category, String name,
                                        String desc, boolean required, int order) {
        return ChecklistTemplate.builder()
                .serviceType(type).category(category).documentName(name)
                .description(desc).required(required).sortOrder(order)
                .ruleVersion(1).approvedForUse(false)
                .build();
    }

    private ChecklistTemplate conditionalTemplate(ServiceType type, String category, String name,
                                                    String desc, boolean required, int order,
                                                    String conditionDescription) {
        return ChecklistTemplate.builder()
                .serviceType(type).category(category).documentName(name)
                .description(desc).required(required).sortOrder(order)
                .conditional(true).conditionDescription(conditionDescription)
                .ruleVersion(1).approvedForUse(false)
                .build();
    }
}
