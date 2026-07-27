-- ============================================================
-- Immigration Automation - Seed Data (MSSQL)
-- V2: Initial reference data + demo consultant
-- ============================================================

-- ======================== DEMO CONSULTANT ========================

INSERT INTO consultants (consultant_number, full_name, email, license_number, company_name, [admin], active, mcp_api_key, created_at)
VALUES (N'CO0000000000001', N'Demo Consultant', N'demo@immiauto.ca', N'R000000', N'Demo Immigration Services', 1, 1, N'mcp_demo00000000000000000000000000', GETDATE());

-- ======================== CHECKLIST TEMPLATES ========================

-- ---- Study Permit ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'STUDY_PERMIT', N'Identity', N'Passport biographical page', N'Clear scan of your passport page showing your photo, name, and passport number.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Identity', N'National ID card', N'Front and back of your national identity card, if applicable.', 0, 0, NULL, 2, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Identity', N'Birth certificate', N'Official birth certificate with English or French translation if needed.', 0, 0, NULL, 3, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'Letter of acceptance from DLI', N'Original letter of acceptance from a Designated Learning Institution in Canada.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'Previous transcripts', N'Academic transcripts from your most recent educational institution.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'Previous diplomas or degrees', N'Copies of diplomas or degree certificates you have earned.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Attestation', N'Provincial Attestation Letter (PAL/TAL)', N'Attestation letter from the province where your DLI is located.', 1, 0, NULL, 7, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Attestation', N'Quebec Acceptance Certificate (CAQ)', N'Certificat d''acceptation du Québec, required for Quebec institutions.', 1, 1, N'Required if school is in Quebec', 8, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Financial', N'Proof of funds - bank statements', N'Bank statements from the last 4 months showing sufficient funds for tuition and living expenses.', 1, 0, NULL, 9, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Financial', N'Tuition payment receipt', N'Receipt showing tuition has been paid or deposit made to the school.', 0, 0, NULL, 10, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Financial', N'Scholarship or funding letter', N'Letter confirming scholarship, bursary, or other funding if applicable.', 0, 0, NULL, 11, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Financial', N'GIC certificate', N'Guaranteed Investment Certificate from a participating financial institution, if applicable.', 0, 0, NULL, 12, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Financial', N'Sponsor financial documents', N'Bank statements, employment letter, and income proof from your financial sponsor.', 0, 1, N'Required if a sponsor is providing financial support', 13, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Language', N'Language test results (IELTS/CELPIP/TEF/TCF)', N'Official language test results, if required by the program or for SDS.', 0, 0, NULL, 14, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Purpose', N'Study plan / statement of purpose', N'Written explanation of why you chose this program and your plans after graduation.', 1, 0, NULL, 15, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Ties to Home', N'Proof of ties to home country', N'Employment letter, property documents, family obligations, or other evidence of intent to return.', 0, 0, NULL, 16, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Immigration History', N'Previous visa refusal letter', N'If you have been refused a visa before, provide the refusal letter.', 0, 0, NULL, 17, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Immigration History', N'Current visa or permit (if in Canada)', N'Copy of current study or work permit, if applying from inside Canada.', 0, 0, NULL, 18, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Immigration History', N'Previous travel history', N'Copies of previous visas and travel stamps from past international travel.', 0, 0, NULL, 19, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Civil Status', N'Marriage certificate', N'If married, provide your marriage certificate.', 0, 0, NULL, 20, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Family', N'Spouse passport and documents', N'Passport and supporting documents for accompanying spouse.', 0, 1, N'Required if spouse is accompanying', 21, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Family', N'Children birth certificates and passports', N'Birth certificates and passports for accompanying dependent children.', 0, 1, N'Required if children are accompanying', 22, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Representative', N'Use of Representative form (IMM 5476)', N'Signed form authorizing your immigration consultant to act on your behalf.', 1, 0, NULL, 23, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Photos', N'Passport-size photos', N'Two recent passport-size photos meeting IRCC specifications.', 1, 0, NULL, 24, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Country-specific', N'Police clearance certificate', N'Police certificate from your country or any country you lived in for 6+ months since age 18.', 0, 0, NULL, 25, 1, 0, GETDATE()),
(N'STUDY_PERMIT', N'Country-specific', N'Medical exam results', N'Results of immigration medical exam, if required based on your country.', 0, 0, NULL, 26, 1, 0, GETDATE());

-- ---- Visitor Visa ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'VISITOR_VISA', N'Identity', N'Passport biographical page', N'Clear scan of your passport showing photo, name, and passport number.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Identity', N'National ID card', N'Front and back scan of national identity card.', 0, 0, NULL, 2, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Financial', N'Bank statements (3 months)', N'Bank statements from the last 3 months showing sufficient funds.', 1, 0, NULL, 3, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Financial', N'Employment letter', N'Letter from employer confirming your position, salary, and approved leave.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Financial', N'Property or asset documents', N'Documents showing property ownership, investments, or other ties to home country.', 0, 0, NULL, 5, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Host Documents', N'Host invitation letter', N'Formal letter from host inviting you to Canada, with dates, purpose, and accommodation details.', 0, 1, N'Required if visiting a host in Canada', 6, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Host Documents', N'Host proof of status', N'Copy of host''s Canadian citizenship, PR card, or valid immigration status.', 0, 1, N'Required if visiting a host in Canada', 7, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Host Documents', N'Host proof of income/finances', N'Host''s employment letter, NOA, or bank statements if host is financially supporting visit.', 0, 1, N'Required if host is funding the trip', 8, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Purpose of Visit', N'Travel itinerary', N'Flight booking or travel plan showing intended dates.', 0, 0, NULL, 9, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Purpose of Visit', N'Hotel reservations', N'Accommodation booking confirmations.', 0, 0, NULL, 10, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Purpose of Visit', N'Business invitation / conference registration', N'Business invitation letter or conference registration for business visitors.', 0, 1, N'Required for business visitors', 11, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Purpose of Visit', N'Event tickets or registration', N'Tickets, registration confirmation, or event details.', 0, 1, N'Required for event visitors', 12, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Status Documents', N'Current immigration status (if in Canada)', N'Copy of current visa, permit, or visitor record if applying from inside Canada.', 0, 0, NULL, 13, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Ties to Home Country', N'Business registration', N'Business registration or ownership documents if self-employed.', 0, 0, NULL, 14, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Ties to Home Country', N'Proof of family ties', N'Documents showing family members remaining in your home country.', 0, 0, NULL, 15, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Civil Status', N'Marriage certificate', N'If married, provide marriage certificate.', 0, 0, NULL, 16, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Civil Status', N'Children birth certificates', N'Birth certificates of dependent children, if applicable.', 0, 0, NULL, 17, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Travel History', N'Previous visas and travel stamps', N'Copies of previous visas and passport stamps from past travel.', 0, 0, NULL, 18, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Immigration History', N'Previous refusal explanation', N'Written explanation and supporting documents for any prior visa refusals.', 0, 1, N'Required if previously refused a visa', 19, 1, 0, GETDATE()),
(N'VISITOR_VISA', N'Photos', N'Passport-size photos', N'Two recent passport-size photos meeting IRCC specifications.', 1, 0, NULL, 20, 1, 0, GETDATE());

-- ---- Spousal Sponsorship ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'SPOUSAL_SPONSORSHIP', N'Identity - Sponsor', N'Sponsor passport', N'Clear scan of sponsor''s Canadian passport or PR card.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Identity - Sponsor', N'Sponsor proof of status', N'PR card, citizenship certificate, or Canadian passport.', 1, 0, NULL, 2, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Identity - Sponsor', N'Sponsor birth certificate', N'Official birth certificate of the sponsor.', 0, 0, NULL, 3, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Identity - Applicant', N'Applicant passport', N'Clear scan of applicant''s passport biographical page.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Identity - Applicant', N'Applicant birth certificate', N'Official birth certificate of the applicant.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship', N'Marriage certificate', N'Official marriage certificate.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship', N'Statutory declaration of common-law union', N'Sworn declaration confirming 12+ months of continuous cohabitation.', 1, 1, N'Required for common-law relationships', 7, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship', N'Relationship timeline narrative', N'Written narrative of how you met, your relationship history, and milestones.', 1, 0, NULL, 8, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Photos', N'Photos together', N'Photos at different times, with family, at events, in different locations.', 1, 0, NULL, 9, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Communication', N'Chat/messaging history', N'Screenshots of text messages, WhatsApp, Facebook Messenger, or other messaging apps.', 1, 0, NULL, 10, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Communication', N'Call logs and video call history', N'Phone call logs and video call screenshots/history.', 1, 0, NULL, 11, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Travel', N'Travel records together', N'Flight bookings, boarding passes, hotel reservations, or passport stamps from trips together.', 0, 0, NULL, 12, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Financial', N'Joint bank accounts or shared finances', N'Joint bank account statements or evidence of shared financial responsibilities.', 0, 0, NULL, 13, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Financial', N'Joint lease, utilities, or insurance', N'Shared lease agreements, utility bills, or insurance policies showing both names.', 0, 0, NULL, 14, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Evidence - Third Party', N'Letters from friends and family', N'Sworn affidavits or letters from people who know the relationship.', 0, 0, NULL, 15, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Financial - Sponsor', N'Tax returns / NOA', N'Notice of Assessment from CRA for the most recent tax year.', 1, 0, NULL, 16, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Financial - Sponsor', N'Employment letter', N'Letter from sponsor''s employer confirming employment and salary.', 0, 0, NULL, 17, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Financial - Sponsor', N'Bank statements (3 months)', N'Sponsor''s bank statements for the last 3 months.', 0, 0, NULL, 18, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Dependents', N'Dependent children birth certificates', N'Birth certificates for all dependent children included in the application.', 0, 1, N'Required if there are dependent children', 19, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Dependents', N'Custody or adoption documents', N'Court orders, custody agreements, or adoption certificates.', 0, 1, N'Required if there are custody arrangements', 20, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Quebec', N'Quebec undertaking (CSQ forms)', N'Quebec-specific sponsorship undertaking forms.', 0, 1, N'Required if sponsor lives in Quebec', 21, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Previous Sponsorship', N'Previous sponsorship proof of completion', N'Proof that 3-year undertaking from a previous sponsorship has ended.', 0, 1, N'Required if sponsor has sponsored before', 22, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Country-specific', N'Police clearance - applicant', N'Police certificate from applicant''s country and any country lived in 6+ months since age 18.', 1, 0, NULL, 23, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Country-specific', N'Police clearance - sponsor', N'Police certificate for the sponsor, if applicable.', 0, 0, NULL, 24, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Country-specific', N'Medical exam results', N'Immigration medical exam results for the applicant and dependents.', 1, 0, NULL, 25, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Photos', N'Passport-size photos', N'Two recent passport-size photos meeting IRCC specifications.', 1, 0, NULL, 26, 1, 0, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Representative', N'Use of Representative form (IMM 5476)', N'Signed form authorizing your immigration consultant to act on your behalf.', 1, 0, NULL, 27, 1, 0, GETDATE());

-- ---- Express Entry ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'EXPRESS_ENTRY', N'Identity', N'Passport biographical page', N'Clear scan of passport.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Identity', N'Birth certificate', N'Official birth certificate.', 0, 0, NULL, 2, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Language', N'Language test results (IELTS/CELPIP/TEF/TCF)', N'Official language test results for primary applicant.', 1, 0, NULL, 3, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Language', N'Spouse language test results', N'Language test results for accompanying spouse, if applicable.', 0, 1, N'Required if spouse is accompanying', 4, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Education', N'Canadian transcripts and diplomas', N'Transcripts and diplomas from Canadian educational institutions.', 0, 0, NULL, 5, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Education', N'Educational Credential Assessment (ECA)', N'WES, IQAS, or other designated ECA report for foreign education.', 1, 1, N'Required for foreign education credentials', 6, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Education', N'Foreign transcripts and diplomas', N'Transcripts and diplomas from foreign educational institutions.', 0, 0, NULL, 7, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Work Experience', N'Employment reference letters', N'Detailed reference letters for each qualifying work period: duties, hours, dates, salary.', 1, 0, NULL, 8, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Work Experience', N'Pay stubs or tax documents', N'Pay stubs, T4s, or equivalent to corroborate employment claims.', 0, 0, NULL, 9, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Financial', N'Proof of settlement funds', N'Bank statements, investment statements, or GIC showing required funds.', 1, 0, NULL, 10, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Job Offer', N'Valid job offer letter', N'Signed employment offer from a Canadian employer.', 0, 1, N'Required if claiming job offer points', 11, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Job Offer', N'LMIA confirmation (if applicable)', N'Labour Market Impact Assessment for the job offer.', 0, 1, N'Required if job offer requires LMIA', 12, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'PNP', N'Provincial Nomination Certificate', N'Official nomination certificate from a Canadian province or territory.', 0, 1, N'Required for PNP Express Entry stream', 13, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Police Certificates', N'Police certificate - country of citizenship', N'Police certificate from your country of citizenship.', 1, 0, NULL, 14, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Police Certificates', N'Police certificate - countries lived 6+ months', N'Police certificates from every country you lived in 6+ months since age 18.', 1, 0, NULL, 15, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Medical', N'Immigration medical exam (IME)', N'Medical exam from an IRCC-designated panel physician.', 1, 0, NULL, 16, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Civil Status', N'Marriage certificate or divorce decree', N'Marriage certificate, or divorce/annulment decree if previously married.', 0, 0, NULL, 17, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Dependents', N'Dependent children documents', N'Birth certificates, passports, and custody documents for dependent children.', 0, 1, N'Required if including dependents', 18, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Photos', N'Passport-size photos', N'Photos meeting IRCC specifications for applicant and all family members.', 1, 0, NULL, 19, 1, 0, GETDATE()),
(N'EXPRESS_ENTRY', N'Representative', N'Use of Representative form (IMM 5476)', N'Signed form authorizing consultant.', 1, 0, NULL, 20, 1, 0, GETDATE());

-- ---- Work Permit ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'WORK_PERMIT', N'Identity', N'Passport biographical page', N'Clear scan of passport.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Job/Employer', N'Job offer letter', N'Signed employment offer with title, duties, wage, hours, and location.', 1, 0, NULL, 2, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Job/Employer', N'Employer LMIA confirmation', N'Positive LMIA from ESDC, if applicable.', 0, 0, NULL, 3, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Job/Employer', N'Employer compliance fee receipt', N'Proof employer paid the compliance fee ($230).', 0, 0, NULL, 4, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Job/Employer', N'Employment contract', N'Signed contract with terms and conditions.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Qualifications', N'Resume / CV', N'Current resume showing relevant experience.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Qualifications', N'Educational credentials', N'Diplomas, degrees, or certificates relevant to the job.', 0, 0, NULL, 7, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Qualifications', N'Professional licence or certification', N'Licence or certification required for the occupation.', 0, 0, NULL, 8, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Experience', N'Previous employment reference letters', N'Reference letters from previous employers detailing duties and dates.', 0, 0, NULL, 9, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Status', N'Current Canadian status documents', N'Current work permit, study permit, or visitor record.', 0, 1, N'Required if applying from inside Canada', 10, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Status', N'Status extension/restoration application', N'Proof of pending status application if status has expired.', 0, 1, N'Required if current status has expired', 11, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Family', N'Spouse open work permit documents', N'Spouse passport, marriage certificate, and SOWP application documents.', 0, 1, N'Required if spouse is applying for open work permit', 12, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Family', N'Dependent children documents', N'Birth certificates and passports for accompanying children.', 0, 1, N'Required if children are accompanying', 13, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Financial', N'Proof of funds for initial settlement', N'Bank statements showing funds for initial period in Canada.', 0, 0, NULL, 14, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Photos', N'Passport-size photos', N'Photos meeting IRCC specifications.', 1, 0, NULL, 15, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Country-specific', N'Police clearance certificate', N'Police certificate if required based on country.', 0, 0, NULL, 16, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Country-specific', N'Medical exam results', N'Immigration medical exam if required.', 0, 0, NULL, 17, 1, 0, GETDATE()),
(N'WORK_PERMIT', N'Representative', N'Use of Representative form (IMM 5476)', N'Signed form authorizing consultant.', 1, 0, NULL, 18, 1, 0, GETDATE());

-- ---- LMIA ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'LMIA', N'Employer Identity', N'Business licence or registration', N'Provincial or federal business registration documents.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'LMIA', N'Employer Identity', N'CRA Business Number confirmation', N'Proof of CRA Business Number.', 1, 0, NULL, 2, 1, 0, GETDATE()),
(N'LMIA', N'Employer Identity', N'Articles of incorporation', N'Corporate registration or articles of incorporation.', 0, 0, NULL, 3, 1, 0, GETDATE()),
(N'LMIA', N'Business Legitimacy', N'T4 Summary (previous year)', N'T4 Summary showing payroll for Canadian employees.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'LMIA', N'Business Legitimacy', N'Financial statements or tax returns', N'Business financial statements or corporate tax returns.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'LMIA', N'Business Legitimacy', N'Commercial lease or property proof', N'Proof of business premises.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'LMIA', N'Business Legitimacy', N'Business insurance', N'Proof of WCB/WSIB or equivalent workplace insurance.', 0, 0, NULL, 7, 1, 0, GETDATE()),
(N'LMIA', N'Recruitment', N'Job advertisement screenshots', N'Screenshots of all job ads with dates, platforms, and content.', 1, 0, NULL, 8, 1, 0, GETDATE()),
(N'LMIA', N'Recruitment', N'Job Bank posting confirmation', N'Proof of posting on the Government of Canada Job Bank for minimum 4 weeks.', 1, 0, NULL, 9, 1, 0, GETDATE()),
(N'LMIA', N'Recruitment', N'Recruitment summary report', N'Summary of number of applicants, interviews conducted, and reasons for non-hire.', 1, 0, NULL, 10, 1, 0, GETDATE()),
(N'LMIA', N'Recruitment', N'Interview records', N'Records of interviews conducted with Canadian applicants.', 0, 0, NULL, 11, 1, 0, GETDATE()),
(N'LMIA', N'Transition Plan', N'Transition plan document', N'Written plan to reduce reliance on temporary foreign workers.', 1, 0, NULL, 12, 1, 0, GETDATE()),
(N'LMIA', N'Provincial', N'Provincial attestation or requirements', N'Province-specific attestation or documentation requirements.', 0, 1, N'Required by some provinces', 13, 1, 0, GETDATE()),
(N'LMIA', N'Worker Documents', N'Candidate resume / CV', N'Resume of the foreign worker being hired.', 1, 0, NULL, 14, 1, 0, GETDATE()),
(N'LMIA', N'Worker Documents', N'Candidate credentials and qualifications', N'Proof of qualifications relevant to the position.', 0, 0, NULL, 15, 1, 0, GETDATE()),
(N'LMIA', N'Fees', N'LMIA processing fee payment receipt ($1,000)', N'Proof of payment of the LMIA processing fee.', 1, 0, NULL, 16, 1, 0, GETDATE()),
(N'LMIA', N'Fees', N'Employer compliance fee receipt ($230)', N'Proof of compliance fee payment.', 1, 0, NULL, 17, 1, 0, GETDATE());

-- ---- Citizenship ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'CITIZENSHIP', N'PR Proof', N'PR card (front and back)', N'Clear scan of both sides of current or most recent PR card.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'PR Proof', N'Confirmation of Permanent Residence (COPR)', N'Original COPR or Record of Landing.', 1, 0, NULL, 2, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Identity', N'Passport biographical page', N'Current passport biographical page.', 1, 0, NULL, 3, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Identity', N'Two passport-size photos', N'Photos meeting IRCC citizenship application specifications.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Travel History', N'Travel journal / absence log', N'Detailed log of all trips outside Canada during eligibility period with dates and destinations.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Travel History', N'Passport stamps and boarding passes', N'Evidence supporting declared travel dates.', 0, 0, NULL, 6, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Language', N'Language proof (IELTS/CELPIP/TEF/TCF or equivalent)', N'Official language test results or proof of CLB 4+ in English or French.', 1, 0, NULL, 7, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Tax Filing', N'Notice of Assessment (NOA) - 3 tax years', N'NOAs from CRA for 3 of the last 5 tax years within eligibility period.', 1, 0, NULL, 8, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Tax Filing', N'T1 General tax returns', N'T1 returns corresponding to the NOAs provided.', 0, 0, NULL, 9, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Residence', N'Proof of address in Canada', N'Utility bills, lease agreements, or bank statements showing Canadian address.', 0, 0, NULL, 10, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Name Change', N'Legal name change certificate', N'Court order or official document showing legal name change.', 0, 1, N'Required if name has changed since becoming PR', 11, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Criminal', N'Court records or discharge documents', N'Records of criminal charges, convictions, or absolute/conditional discharge.', 0, 1, N'Required if applicant has criminal history', 12, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Minor Applicant', N'Parent/guardian consent and ID', N'Both parents'' ID and signed consent for minor citizenship application.', 0, 1, N'Required for applicants under 18', 13, 1, 0, GETDATE()),
(N'CITIZENSHIP', N'Representative', N'Use of Representative form', N'Signed form authorizing consultant.', 1, 0, NULL, 14, 1, 0, GETDATE());

-- ---- PR Card / PRTD ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'PR_CARD_PRTD', N'PR Proof', N'Confirmation of Permanent Residence (COPR)', N'Original COPR or Record of Landing.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'PR Proof', N'Previous PR card (if available)', N'Most recent PR card, even if expired.', 0, 0, NULL, 2, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Identity', N'Passport biographical page', N'Current valid passport.', 1, 0, NULL, 3, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Identity', N'Two passport-size photos', N'Photos meeting IRCC specifications.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Residence', N'Proof of Canadian address', N'Utility bill, lease, or government correspondence showing current address.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Residence', N'Travel history log (5 years)', N'Detailed log of all absences from Canada in the last 5 years.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Residence', N'Passport stamps and travel evidence', N'Supporting evidence for declared travel dates.', 0, 0, NULL, 7, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Lost/Stolen/Damaged', N'Police report', N'Police report or incident number for lost or stolen card.', 0, 1, N'Required if card was lost or stolen', 8, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Lost/Stolen/Damaged', N'Damaged card scan', N'Scan of the damaged card.', 0, 1, N'Required if card is damaged', 9, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Name Change', N'Legal name or gender change documents', N'Court order, marriage certificate, or official change document.', 0, 1, N'Required if name or gender changed since PR was granted', 10, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'PRTD', N'Proof of ties to Canada', N'Evidence of home, family, employment, or financial ties to Canada.', 0, 1, N'Required for PR Travel Document applications', 11, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'PRTD', N'Explanation of absence from Canada', N'Written explanation for extended absence from Canada.', 0, 1, N'Required for PRTD when outside Canada', 12, 1, 0, GETDATE()),
(N'PR_CARD_PRTD', N'Representative', N'Use of Representative form', N'Signed form authorizing consultant.', 1, 0, NULL, 13, 1, 0, GETDATE());

-- ---- PGWP ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'PGWP', N'Identity', N'Passport biographical page', N'Current valid passport.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'PGWP', N'Study Completion', N'Official completion letter from DLI', N'Letter from school confirming program completion and graduation date.', 1, 0, NULL, 2, 1, 0, GETDATE()),
(N'PGWP', N'Study Completion', N'Final transcript', N'Official final transcript showing all courses and grades.', 1, 0, NULL, 3, 1, 0, GETDATE()),
(N'PGWP', N'Study Completion', N'Diploma or degree certificate', N'Copy of diploma or degree, if already issued.', 0, 0, NULL, 4, 1, 0, GETDATE()),
(N'PGWP', N'School/Program', N'Letter of acceptance (original)', N'Original letter of acceptance from the DLI.', 0, 0, NULL, 5, 1, 0, GETDATE()),
(N'PGWP', N'Current Status', N'Current study permit', N'Copy of valid study permit.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'PGWP', N'Current Status', N'Implied status proof', N'Proof of implied status if study permit expired but PGWP applied within 90 days.', 0, 1, N'Required if study permit has expired', 7, 1, 0, GETDATE()),
(N'PGWP', N'Language', N'Language test results', N'IELTS, CELPIP, TEF, or TCF results if required for field of study.', 0, 1, N'Required based on graduation date and program', 8, 1, 0, GETDATE()),
(N'PGWP', N'Prior Study', N'Transfer letters from previous institutions', N'Letters from all institutions attended if programs were transferred.', 0, 1, N'Required if student transferred between programs or schools', 9, 1, 0, GETDATE()),
(N'PGWP', N'Photos', N'Passport-size photos', N'Photos meeting IRCC specifications.', 1, 0, NULL, 10, 1, 0, GETDATE()),
(N'PGWP', N'Representative', N'Use of Representative form', N'Signed form authorizing consultant.', 1, 0, NULL, 11, 1, 0, GETDATE());

-- ---- Super Visa ----
INSERT INTO checklist_templates (service_type, category, document_name, [description], required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
(N'SUPER_VISA', N'Identity - Applicant', N'Passport biographical page', N'Applicant''s current valid passport.', 1, 0, NULL, 1, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Relationship', N'Proof of relationship to host', N'Birth certificate, marriage certificate, or other documents proving parent/grandparent relationship.', 1, 0, NULL, 2, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Host Documents', N'Host proof of citizenship or PR', N'Host''s Canadian passport, citizenship certificate, or PR card.', 1, 0, NULL, 3, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Host Documents', N'Host invitation letter', N'Formal invitation letter from host with details of stay.', 1, 0, NULL, 4, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Host Finances', N'Host Notice of Assessment (NOA)', N'CRA Notice of Assessment meeting LICO requirement for household size.', 1, 0, NULL, 5, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Host Finances', N'Host employment letter', N'Letter confirming host''s employment, position, and salary.', 1, 0, NULL, 6, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Host Finances', N'Host bank statements (3 months)', N'Host''s recent bank statements.', 0, 0, NULL, 7, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Medical Insurance', N'Private medical insurance policy', N'Proof of private medical insurance from a Canadian company, valid 1+ year, minimum $100,000 coverage.', 1, 0, NULL, 8, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Medical Insurance', N'Insurance coverage details', N'Policy details showing coverage amount, period, and emergency medical coverage.', 1, 0, NULL, 9, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Medical', N'Immigration medical exam results', N'Medical exam from IRCC-designated panel physician.', 1, 0, NULL, 10, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Travel History', N'Previous visas and travel stamps', N'Copies of previous visas and passport stamps.', 0, 0, NULL, 11, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Immigration History', N'Previous refusal explanation', N'Written explanation for any prior visa refusals.', 0, 1, N'Required if previously refused a visa', 12, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Photos', N'Passport-size photos', N'Photos meeting IRCC specifications.', 1, 0, NULL, 13, 1, 0, GETDATE()),
(N'SUPER_VISA', N'Representative', N'Use of Representative form', N'Signed form authorizing consultant.', 1, 0, NULL, 14, 1, 0, GETDATE());

-- ======================== INTAKE QUESTION TEMPLATES ========================

-- ---- Study Permit ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'STUDY_PERMIT', N'Applicant Identity', N'full_name', N'Full Legal Name (as on passport)', NULL, N'TEXT', NULL, 1, 0, 1, GETDATE()),
(N'STUDY_PERMIT', N'Applicant Identity', N'dob', N'Date of Birth', NULL, N'DATE', NULL, 1, 0, 2, GETDATE()),
(N'STUDY_PERMIT', N'Applicant Identity', N'citizenship', N'Country of Citizenship', NULL, N'TEXT', NULL, 1, 0, 3, GETDATE()),
(N'STUDY_PERMIT', N'Applicant Identity', N'passport_number', N'Passport Number', NULL, N'TEXT', NULL, 1, 0, 4, GETDATE()),
(N'STUDY_PERMIT', N'Applicant Identity', N'passport_expiry', N'Passport Expiry Date', NULL, N'DATE', NULL, 1, 0, 5, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'dli_name', N'Name of Designated Learning Institution (DLI)', NULL, N'TEXT', NULL, 1, 0, 6, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'program_name', N'Program Name', NULL, N'TEXT', NULL, 1, 0, 7, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'program_start_date', N'Program Start Date', NULL, N'DATE', NULL, 1, 0, 8, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'program_end_date', N'Program End Date', NULL, N'DATE', NULL, 1, 0, 9, GETDATE()),
(N'STUDY_PERMIT', N'Education', N'level_of_study', N'Level of Study', NULL, N'SELECT', N'Certificate|Diploma|Bachelor|Master|PhD|K-12|Language|Other', 1, 0, 10, GETDATE()),
(N'STUDY_PERMIT', N'Location', N'school_province', N'Province of School', NULL, N'SELECT', N'AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT', 1, 1, 11, GETDATE()),
(N'STUDY_PERMIT', N'Location', N'is_quebec', N'Is this a Quebec institution?', N'If yes, CAQ process applies instead of PAL/TAL', N'SELECT', N'Yes|No', 1, 1, 12, GETDATE()),
(N'STUDY_PERMIT', N'PAL/TAL/CAQ', N'has_pal_tal', N'Has the school issued a PAL/TAL?', NULL, N'SELECT', N'Yes|No|Not Sure', 0, 1, 13, GETDATE()),
(N'STUDY_PERMIT', N'PAL/TAL/CAQ', N'has_caq', N'Has the school issued a CAQ? (Quebec only)', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 1, 14, GETDATE()),
(N'STUDY_PERMIT', N'Finances', N'funding_source', N'Who pays tuition and living expenses?', NULL, N'SELECT', N'Self|Parent/Family|Sponsor|Scholarship|Employer|GIC|Combination', 1, 1, 15, GETDATE()),
(N'STUDY_PERMIT', N'Finances', N'funds_available', N'Approximate funds available (CAD)', NULL, N'TEXT', NULL, 1, 0, 16, GETDATE()),
(N'STUDY_PERMIT', N'Finances', N'has_gic', N'Do you have a GIC?', NULL, N'SELECT', N'Yes|No', 0, 0, 17, GETDATE()),
(N'STUDY_PERMIT', N'Sponsor', N'has_sponsor', N'Is a sponsor providing financial support?', NULL, N'SELECT', N'Yes|No', 0, 1, 18, GETDATE()),
(N'STUDY_PERMIT', N'Sponsor', N'sponsor_relationship', N'Relationship to Sponsor', NULL, N'TEXT', NULL, 0, 0, 19, GETDATE()),
(N'STUDY_PERMIT', N'Sponsor', N'sponsor_income_source', N'Sponsor''s Source of Income', NULL, N'TEXT', NULL, 0, 0, 20, GETDATE()),
(N'STUDY_PERMIT', N'History', N'prior_refusals', N'Have you had any prior visa/permit refusals?', NULL, N'SELECT', N'Yes|No', 1, 1, 21, GETDATE()),
(N'STUDY_PERMIT', N'History', N'prior_refusal_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 22, GETDATE()),
(N'STUDY_PERMIT', N'History', N'prior_overstays', N'Have you ever overstayed a visa?', NULL, N'SELECT', N'Yes|No', 1, 0, 23, GETDATE()),
(N'STUDY_PERMIT', N'History', N'previous_canadian_apps', N'Any previous Canadian immigration applications?', NULL, N'SELECT', N'Yes|No', 1, 0, 24, GETDATE()),
(N'STUDY_PERMIT', N'Family', N'spouse_accompanying', N'Is your spouse/partner accompanying you?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 1, 25, GETDATE()),
(N'STUDY_PERMIT', N'Family', N'children_accompanying', N'Are children accompanying you?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 1, 26, GETDATE()),
(N'STUDY_PERMIT', N'Country-Specific', N'applying_from_country', N'Which country are you applying from?', NULL, N'TEXT', NULL, 1, 1, 27, GETDATE());

-- ---- Visitor Visa ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'VISITOR_VISA', N'Visit Purpose', N'visit_purpose', N'Purpose of Visit', NULL, N'SELECT', N'Tourism|Family Visit|Business|Event|Medical|Other', 1, 1, 1, GETDATE()),
(N'VISITOR_VISA', N'Visit Purpose', N'visit_purpose_details', N'Details of Visit Purpose', NULL, N'TEXTAREA', NULL, 0, 0, 2, GETDATE()),
(N'VISITOR_VISA', N'Host', N'has_canadian_host', N'Is there a Canadian host?', NULL, N'SELECT', N'Yes|No', 1, 1, 3, GETDATE()),
(N'VISITOR_VISA', N'Host', N'host_relationship', N'Relationship to Host', NULL, N'TEXT', NULL, 0, 0, 4, GETDATE()),
(N'VISITOR_VISA', N'Host', N'host_name', N'Host Full Name', NULL, N'TEXT', NULL, 0, 0, 5, GETDATE()),
(N'VISITOR_VISA', N'Host', N'host_status', N'Host''s Immigration Status', NULL, N'SELECT', N'Citizen|Permanent Resident|Temporary Resident|Other', 0, 0, 6, GETDATE()),
(N'VISITOR_VISA', N'Duration', N'planned_arrival', N'Planned Arrival Date', NULL, N'DATE', NULL, 1, 0, 7, GETDATE()),
(N'VISITOR_VISA', N'Duration', N'planned_departure', N'Planned Departure Date', NULL, N'DATE', NULL, 1, 0, 8, GETDATE()),
(N'VISITOR_VISA', N'Funding', N'funding_type', N'Who is funding the trip?', NULL, N'SELECT', N'Self-funded|Host-funded|Employer|Combination', 1, 1, 9, GETDATE()),
(N'VISITOR_VISA', N'Employment', N'current_employer', N'Current Employer', NULL, N'TEXT', NULL, 0, 0, 10, GETDATE()),
(N'VISITOR_VISA', N'Employment', N'job_title', N'Job Title', NULL, N'TEXT', NULL, 0, 0, 11, GETDATE()),
(N'VISITOR_VISA', N'Employment', N'leave_approved', N'Has leave been approved?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 0, 12, GETDATE()),
(N'VISITOR_VISA', N'Assets/Ties', N'property_owned', N'Do you own property in your home country?', NULL, N'SELECT', N'Yes|No', 0, 0, 13, GETDATE()),
(N'VISITOR_VISA', N'Assets/Ties', N'family_ties', N'Family members remaining in home country', NULL, N'TEXTAREA', NULL, 0, 0, 14, GETDATE()),
(N'VISITOR_VISA', N'Assets/Ties', N'business_owned', N'Do you own a business?', NULL, N'SELECT', N'Yes|No', 0, 0, 15, GETDATE()),
(N'VISITOR_VISA', N'Travel History', N'countries_visited', N'Countries visited in the last 10 years', NULL, N'TEXTAREA', NULL, 0, 0, 16, GETDATE()),
(N'VISITOR_VISA', N'Travel History', N'prior_canadian_visits', N'Have you visited Canada before?', NULL, N'SELECT', N'Yes|No', 0, 0, 17, GETDATE()),
(N'VISITOR_VISA', N'Refusals', N'prior_visa_refusals', N'Any previous visa refusals (any country)?', NULL, N'SELECT', N'Yes|No', 1, 1, 18, GETDATE()),
(N'VISITOR_VISA', N'Refusals', N'refusal_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 19, GETDATE());

-- ---- Spousal Sponsorship ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'SPOUSAL_SPONSORSHIP', N'Sponsor', N'sponsor_status', N'Sponsor''s Immigration Status', NULL, N'SELECT', N'Canadian Citizen|Permanent Resident', 1, 1, 1, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Sponsor', N'sponsor_in_canada', N'Is sponsor currently living in Canada?', NULL, N'SELECT', N'Yes|No', 1, 1, 2, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Sponsor', N'sponsor_province', N'Sponsor''s Province of Residence', NULL, N'SELECT', N'AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT|Outside Canada', 1, 1, 3, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Applicant', N'applicant_country', N'Applicant''s Country of Residence', NULL, N'TEXT', NULL, 1, 0, 4, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Applicant', N'applicant_current_status', N'Applicant''s Current Immigration Status', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Applicant', N'applicant_prior_marriages', N'Has applicant been previously married?', NULL, N'SELECT', N'Yes|No', 1, 0, 6, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Type', N'relationship_type', N'Relationship Type', NULL, N'SELECT', N'Married|Common-Law|Conjugal', 1, 1, 7, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Timeline', N'first_contact_date', N'Date of First Contact', NULL, N'DATE', NULL, 1, 0, 8, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Timeline', N'first_meeting_date', N'Date of First In-Person Meeting', NULL, N'DATE', NULL, 1, 0, 9, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Timeline', N'engagement_date', N'Engagement Date (if applicable)', NULL, N'DATE', NULL, 0, 0, 10, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Relationship Timeline', N'marriage_cohabitation_date', N'Marriage/Cohabitation Date', NULL, N'DATE', NULL, 1, 0, 11, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Cohabitation', N'shared_address', N'Do you share an address?', NULL, N'SELECT', N'Yes|No', 1, 0, 12, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Cohabitation', N'cohabitation_dates', N'Dates of Cohabitation', NULL, N'TEXT', NULL, 0, 0, 13, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Cohabitation', N'cohabitation_proof', N'What cohabitation proof is available?', NULL, N'TEXTAREA', NULL, 0, 0, 14, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Dependents', N'has_children', N'Are there any children?', NULL, N'SELECT', N'Yes|No', 1, 1, 15, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Dependents', N'custody_issues', N'Any custody issues?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 0, 16, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Previous Sponsorship', N'sponsor_previously_sponsored', N'Has the sponsor sponsored before?', NULL, N'SELECT', N'Yes|No', 1, 1, 17, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Criminal/Immigration History', N'prior_refusals_removals', N'Any prior refusals, removals, or criminality?', NULL, N'SELECT', N'Yes|No', 1, 1, 18, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Criminal/Immigration History', N'refusal_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 19, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'Quebec', N'sponsor_in_quebec', N'Does the sponsor live in Quebec?', N'Quebec has a separate sponsorship process', N'SELECT', N'Yes|No', 1, 1, 20, GETDATE());

-- ---- Express Entry ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'EXPRESS_ENTRY', N'Program Path', N'program_path', N'Which Express Entry program?', NULL, N'SELECT', N'Federal Skilled Worker (FSW)|Canadian Experience Class (CEC)|Federal Skilled Trades (FST)|PNP Express Entry', 1, 1, 1, GETDATE()),
(N'EXPRESS_ENTRY', N'Work History', N'current_employer', N'Current/Most Recent Employer', NULL, N'TEXT', NULL, 1, 0, 2, GETDATE()),
(N'EXPRESS_ENTRY', N'Work History', N'job_title', N'Job Title', NULL, N'TEXT', NULL, 1, 0, 3, GETDATE()),
(N'EXPRESS_ENTRY', N'Work History', N'noc_teer', N'NOC/TEER Code (if known)', NULL, N'TEXT', NULL, 0, 0, 4, GETDATE()),
(N'EXPRESS_ENTRY', N'Work History', N'employment_dates', N'Employment Start and End Dates', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'EXPRESS_ENTRY', N'Work History', N'hours_per_week', N'Hours Per Week', NULL, N'TEXT', NULL, 1, 0, 6, GETDATE()),
(N'EXPRESS_ENTRY', N'Work History', N'job_duties', N'Main Job Duties', NULL, N'TEXTAREA', NULL, 1, 0, 7, GETDATE()),
(N'EXPRESS_ENTRY', N'Education', N'education_canadian_or_foreign', N'Is your highest education Canadian or Foreign?', NULL, N'SELECT', N'Canadian|Foreign|Both', 1, 1, 8, GETDATE()),
(N'EXPRESS_ENTRY', N'Education', N'eca_completed', N'Has an Educational Credential Assessment (ECA) been completed?', NULL, N'SELECT', N'Yes|No|Not Applicable', 1, 1, 9, GETDATE()),
(N'EXPRESS_ENTRY', N'Education', N'highest_education', N'Highest Level of Education', NULL, N'SELECT', N'High School|1-Year Post-Secondary|2-Year Post-Secondary|3-Year Post-Secondary|Bachelor|Two or More Degrees|Master|PhD', 1, 0, 10, GETDATE()),
(N'EXPRESS_ENTRY', N'Language', N'language_test_type', N'Language Test Type', NULL, N'SELECT', N'IELTS|CELPIP|TEF|TCF|None Yet', 1, 1, 11, GETDATE()),
(N'EXPRESS_ENTRY', N'Language', N'language_test_date', N'Test Date', NULL, N'DATE', NULL, 0, 0, 12, GETDATE()),
(N'EXPRESS_ENTRY', N'Language', N'language_scores', N'Test Scores (L/R/W/S)', NULL, N'TEXT', NULL, 0, 0, 13, GETDATE()),
(N'EXPRESS_ENTRY', N'Marital Status', N'marital_status', N'Marital Status', NULL, N'SELECT', N'Single|Married|Common-Law|Separated|Divorced|Widowed', 1, 1, 14, GETDATE()),
(N'EXPRESS_ENTRY', N'Marital Status', N'spouse_accompanying', N'Is spouse/partner accompanying?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 1, 15, GETDATE()),
(N'EXPRESS_ENTRY', N'Dependents', N'has_dependents', N'Any dependent children?', NULL, N'SELECT', N'Yes|No', 1, 1, 16, GETDATE()),
(N'EXPRESS_ENTRY', N'Dependents', N'custody_issues', N'Any custody issues?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 0, 17, GETDATE()),
(N'EXPRESS_ENTRY', N'Funds', N'funds_required', N'Are proof of funds required or exempt?', NULL, N'SELECT', N'Required|Exempt (have valid job offer)|Not Sure', 1, 1, 18, GETDATE()),
(N'EXPRESS_ENTRY', N'Travel/Residence', N'countries_lived_in', N'Countries lived in for 6+ months since age 18', N'Used to determine police certificate requirements', N'TEXTAREA', NULL, 1, 1, 19, GETDATE()),
(N'EXPRESS_ENTRY', N'Prior Applications', N'prior_refusals', N'Any prior Canadian refusals, inadmissibility, or misrepresentation?', NULL, N'SELECT', N'Yes|No', 1, 1, 20, GETDATE()),
(N'EXPRESS_ENTRY', N'Prior Applications', N'prior_refusal_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 21, GETDATE());

-- ---- Work Permit ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'WORK_PERMIT', N'Application Location', N'application_location', N'Where are you applying from?', NULL, N'SELECT', N'Inside Canada|Outside Canada|Port of Entry', 1, 1, 1, GETDATE()),
(N'WORK_PERMIT', N'Work Permit Type', N'wp_type', N'Type of Work Permit', NULL, N'SELECT', N'Employer-Specific|Open Work Permit', 1, 1, 2, GETDATE()),
(N'WORK_PERMIT', N'LMIA', N'lmia_status', N'LMIA Status', NULL, N'SELECT', N'LMIA Required (has LMIA)|LMIA Exempt|Unknown/Not Sure', 1, 1, 3, GETDATE()),
(N'WORK_PERMIT', N'LMIA', N'lmia_number', N'LMIA Number (if applicable)', NULL, N'TEXT', NULL, 0, 0, 4, GETDATE()),
(N'WORK_PERMIT', N'Employer', N'employer_name', N'Employer Legal Name', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'WORK_PERMIT', N'Employer', N'employer_cra_bn', N'Employer CRA Business Number', NULL, N'TEXT', NULL, 0, 0, 6, GETDATE()),
(N'WORK_PERMIT', N'Employer', N'employer_job_title', N'Job Title', NULL, N'TEXT', NULL, 1, 0, 7, GETDATE()),
(N'WORK_PERMIT', N'Offer', N'wage', N'Offered Wage (hourly/annual)', NULL, N'TEXT', NULL, 1, 0, 8, GETDATE()),
(N'WORK_PERMIT', N'Offer', N'work_location', N'Work Location (city, province)', NULL, N'TEXT', NULL, 1, 0, 9, GETDATE()),
(N'WORK_PERMIT', N'Offer', N'job_duties', N'Job Duties', NULL, N'TEXTAREA', NULL, 1, 0, 10, GETDATE()),
(N'WORK_PERMIT', N'Offer', N'noc_teer', N'NOC/TEER Code (if known)', NULL, N'TEXT', NULL, 0, 0, 11, GETDATE()),
(N'WORK_PERMIT', N'Applicant Status', N'current_status_in_canada', N'Current Immigration Status in Canada', NULL, N'SELECT', N'Study Permit|Work Permit|Visitor Record|No Status|Not in Canada', 0, 1, 12, GETDATE()),
(N'WORK_PERMIT', N'Applicant Status', N'status_expiry_date', N'Current Status Expiry Date', NULL, N'DATE', NULL, 0, 0, 13, GETDATE()),
(N'WORK_PERMIT', N'Family', N'spouse_accompanying', N'Is spouse/partner accompanying?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 1, 14, GETDATE()),
(N'WORK_PERMIT', N'Family', N'children_accompanying', N'Are children accompanying?', NULL, N'SELECT', N'Yes|No|Not Applicable', 0, 1, 15, GETDATE()),
(N'WORK_PERMIT', N'Qualifications', N'highest_education', N'Highest Level of Education', NULL, N'TEXT', NULL, 0, 0, 16, GETDATE()),
(N'WORK_PERMIT', N'Qualifications', N'professional_licence', N'Any professional licence/certification?', NULL, N'TEXT', NULL, 0, 0, 17, GETDATE()),
(N'WORK_PERMIT', N'Qualifications', N'years_experience', N'Years of Relevant Work Experience', NULL, N'TEXT', NULL, 0, 0, 18, GETDATE()),
(N'WORK_PERMIT', N'Prior Refusals', N'prior_refusals', N'Any prior work permit or visa refusals?', NULL, N'SELECT', N'Yes|No', 1, 1, 19, GETDATE()),
(N'WORK_PERMIT', N'Prior Refusals', N'refusal_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 20, GETDATE());

-- ---- LMIA ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'LMIA', N'Employer', N'employer_legal_name', N'Employer Legal Name', NULL, N'TEXT', NULL, 1, 0, 1, GETDATE()),
(N'LMIA', N'Employer', N'employer_operating_name', N'Operating/Trade Name', NULL, N'TEXT', NULL, 0, 0, 2, GETDATE()),
(N'LMIA', N'Employer', N'cra_bn', N'CRA Business Number', NULL, N'TEXT', NULL, 1, 0, 3, GETDATE()),
(N'LMIA', N'Employer', N'payroll_account', N'CRA Payroll Account Number', NULL, N'TEXT', NULL, 1, 0, 4, GETDATE()),
(N'LMIA', N'Business', N'industry', N'Industry/Sector', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'LMIA', N'Business', N'business_address', N'Business Address', NULL, N'TEXT', NULL, 1, 0, 6, GETDATE()),
(N'LMIA', N'Business', N'num_employees', N'Number of Employees', NULL, N'TEXT', NULL, 1, 0, 7, GETDATE()),
(N'LMIA', N'Business', N'revenue_proof_available', N'Is revenue/financial proof available?', NULL, N'SELECT', N'Yes|No', 1, 0, 8, GETDATE()),
(N'LMIA', N'Job', N'job_title', N'Job Title', NULL, N'TEXT', NULL, 1, 0, 9, GETDATE()),
(N'LMIA', N'Job', N'wage', N'Offered Wage', NULL, N'TEXT', NULL, 1, 0, 10, GETDATE()),
(N'LMIA', N'Job', N'hours_per_week', N'Hours Per Week', NULL, N'TEXT', NULL, 1, 0, 11, GETDATE()),
(N'LMIA', N'Job', N'work_location', N'Work Location', NULL, N'TEXT', NULL, 1, 0, 12, GETDATE()),
(N'LMIA', N'Job', N'job_duties', N'Main Duties', NULL, N'TEXTAREA', NULL, 1, 0, 13, GETDATE()),
(N'LMIA', N'Job', N'noc_teer', N'NOC/TEER Code (if known)', NULL, N'TEXT', NULL, 0, 0, 14, GETDATE()),
(N'LMIA', N'Stream', N'lmia_stream', N'LMIA Stream', NULL, N'SELECT', N'High-Wage|Low-Wage|PR Support|Global Talent Stream|Agricultural|Caregiver|Seasonal', 1, 1, 15, GETDATE()),
(N'LMIA', N'Recruitment', N'where_advertised', N'Where were job ads posted?', NULL, N'TEXTAREA', NULL, 1, 0, 16, GETDATE()),
(N'LMIA', N'Recruitment', N'ad_start_date', N'Job Ad Start Date', NULL, N'DATE', NULL, 1, 0, 17, GETDATE()),
(N'LMIA', N'Recruitment', N'ad_end_date', N'Job Ad End Date', NULL, N'DATE', NULL, 1, 0, 18, GETDATE()),
(N'LMIA', N'Recruitment', N'num_applicants', N'Number of Applicants Received', NULL, N'TEXT', NULL, 0, 0, 19, GETDATE()),
(N'LMIA', N'Recruitment', N'num_interviewed', N'Number of Applicants Interviewed', NULL, N'TEXT', NULL, 0, 0, 20, GETDATE()),
(N'LMIA', N'Candidate', N'candidate_name', N'Candidate Full Name', NULL, N'TEXT', NULL, 1, 0, 21, GETDATE()),
(N'LMIA', N'Candidate', N'candidate_qualifications', N'Candidate Qualifications', NULL, N'TEXTAREA', NULL, 0, 0, 22, GETDATE()),
(N'LMIA', N'Candidate', N'candidate_experience', N'Candidate Relevant Experience', NULL, N'TEXTAREA', NULL, 0, 0, 23, GETDATE()),
(N'LMIA', N'Compliance', N'prior_lmia_history', N'Any prior LMIA applications?', NULL, N'SELECT', N'Yes|No', 1, 1, 24, GETDATE()),
(N'LMIA', N'Compliance', N'prior_inspections', N'Any prior ESDC inspections?', NULL, N'SELECT', N'Yes|No', 0, 0, 25, GETDATE()),
(N'LMIA', N'Compliance', N'recent_layoffs', N'Any recent layoffs for this position?', NULL, N'SELECT', N'Yes|No', 1, 1, 26, GETDATE());

-- ---- Citizenship ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'CITIZENSHIP', N'Age', N'applicant_age_category', N'Is the applicant an adult or minor?', NULL, N'SELECT', N'Adult (18+)|Minor (under 18)', 1, 1, 1, GETDATE()),
(N'CITIZENSHIP', N'PR Date', N'pr_date', N'Date Became Permanent Resident', NULL, N'DATE', NULL, 1, 0, 2, GETDATE()),
(N'CITIZENSHIP', N'Absences', N'absences_outside_canada', N'Have you travelled outside Canada during the eligibility period?', NULL, N'SELECT', N'Yes|No', 1, 1, 3, GETDATE()),
(N'CITIZENSHIP', N'Absences', N'absence_details', N'If yes, list all trips (country, dates)', NULL, N'TEXTAREA', NULL, 0, 0, 4, GETDATE()),
(N'CITIZENSHIP', N'Tax Years', N'tax_years_filed', N'For which years have you filed Canadian taxes?', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'CITIZENSHIP', N'Tax Years', N'missing_tax_years', N'Are there any missing tax filing years?', NULL, N'SELECT', N'Yes|No|Not Sure', 0, 1, 6, GETDATE()),
(N'CITIZENSHIP', N'Language', N'language_proof_available', N'Is language proof available?', N'Required for applicants 18-54 at time of signing', N'SELECT', N'Yes|No|Exempt (age)', 1, 1, 7, GETDATE()),
(N'CITIZENSHIP', N'Language', N'language_test_type', N'Language Test/Proof Type', NULL, N'TEXT', NULL, 0, 0, 8, GETDATE()),
(N'CITIZENSHIP', N'Prohibitions', N'criminality', N'Any criminal charges, convictions, or ongoing proceedings?', NULL, N'SELECT', N'Yes|No', 1, 1, 9, GETDATE()),
(N'CITIZENSHIP', N'Prohibitions', N'removal_order', N'Any removal orders?', NULL, N'SELECT', N'Yes|No', 1, 1, 10, GETDATE()),
(N'CITIZENSHIP', N'Prohibitions', N'probation', N'Currently on probation?', NULL, N'SELECT', N'Yes|No', 1, 0, 11, GETDATE()),
(N'CITIZENSHIP', N'Identity', N'available_ids', N'What identification documents are available?', NULL, N'TEXTAREA', NULL, 1, 0, 12, GETDATE()),
(N'CITIZENSHIP', N'Identity', N'name_consistency', N'Is your name consistent across all documents?', NULL, N'SELECT', N'Yes|No', 1, 1, 13, GETDATE()),
(N'CITIZENSHIP', N'Past Applications', N'prior_citizenship_app', N'Any prior citizenship application?', NULL, N'SELECT', N'Yes|No', 1, 1, 14, GETDATE()),
(N'CITIZENSHIP', N'Past Applications', N'prior_app_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 15, GETDATE());

-- ---- PR Card / PRTD ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'PR_CARD_PRTD', N'Application Reason', N'application_reason', N'Reason for Application', NULL, N'SELECT', N'Renewal|Replacement|First Card|PR Travel Document (PRTD)', 1, 1, 1, GETDATE()),
(N'PR_CARD_PRTD', N'Card Expiry', N'pr_card_expiry', N'Current PR Card Expiry Date', NULL, N'DATE', NULL, 0, 0, 2, GETDATE()),
(N'PR_CARD_PRTD', N'Travel', N'days_outside_canada', N'Approximate days outside Canada in last 5 years', NULL, N'TEXT', NULL, 1, 1, 3, GETDATE()),
(N'PR_CARD_PRTD', N'Travel', N'travel_details', N'List all trips outside Canada (country, dates)', NULL, N'TEXTAREA', NULL, 0, 0, 4, GETDATE()),
(N'PR_CARD_PRTD', N'Lost/Damaged', N'card_lost_stolen_damaged', N'Was the card lost, stolen, or damaged?', NULL, N'SELECT', N'Lost|Stolen|Damaged|Not Applicable', 0, 1, 5, GETDATE()),
(N'PR_CARD_PRTD', N'Lost/Damaged', N'police_report_number', N'Police Report/Incident Number (if applicable)', NULL, N'TEXT', NULL, 0, 0, 6, GETDATE()),
(N'PR_CARD_PRTD', N'Urgency', N'urgent_processing', N'Do you need urgent processing?', NULL, N'SELECT', N'Yes|No', 0, 0, 7, GETDATE()),
(N'PR_CARD_PRTD', N'Urgency', N'urgency_reason', N'If yes, explain the urgency', NULL, N'TEXTAREA', NULL, 0, 0, 8, GETDATE()),
(N'PR_CARD_PRTD', N'Name Changes', N'name_gender_changes', N'Any legal name or gender changes since PR was granted?', NULL, N'SELECT', N'Yes|No', 1, 1, 9, GETDATE()),
(N'PR_CARD_PRTD', N'Name Changes', N'change_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 10, GETDATE()),
(N'PR_CARD_PRTD', N'Documents', N'copr_available', N'Is your COPR (Confirmation of PR) available?', NULL, N'SELECT', N'Yes|No', 1, 0, 11, GETDATE()),
(N'PR_CARD_PRTD', N'Documents', N'record_of_landing_available', N'Is your Record of Landing available?', NULL, N'SELECT', N'Yes|No|Not Sure', 0, 0, 12, GETDATE()),
(N'PR_CARD_PRTD', N'Documents', N'passport_available', N'Is your current passport available?', NULL, N'SELECT', N'Yes|No', 1, 0, 13, GETDATE());

-- ---- PGWP ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'PGWP', N'Graduation', N'completion_date', N'Program Completion Date', NULL, N'DATE', NULL, 1, 0, 1, GETDATE()),
(N'PGWP', N'Graduation', N'transcript_available', N'Is your final transcript available?', NULL, N'SELECT', N'Yes|No|Pending', 1, 1, 2, GETDATE()),
(N'PGWP', N'Graduation', N'completion_letter_available', N'Is your completion/graduation letter available?', NULL, N'SELECT', N'Yes|No|Pending', 1, 1, 3, GETDATE()),
(N'PGWP', N'School', N'dli_name', N'Designated Learning Institution Name', NULL, N'TEXT', NULL, 1, 0, 4, GETDATE()),
(N'PGWP', N'School', N'program_length', N'Program Length (months)', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'PGWP', N'School', N'program_level', N'Program Level', NULL, N'SELECT', N'Certificate|Diploma|Associate|Bachelor|Master|PhD', 1, 0, 6, GETDATE()),
(N'PGWP', N'Status', N'study_permit_expiry', N'Study Permit Expiry Date', NULL, N'DATE', NULL, 1, 1, 7, GETDATE()),
(N'PGWP', N'Status', N'current_status', N'Current Immigration Status', NULL, N'SELECT', N'Valid Study Permit|Implied Status|Visitor Record|Other', 1, 1, 8, GETDATE()),
(N'PGWP', N'Language', N'language_test_taken', N'Have you taken a language test?', N'Required for some PGWP applications', N'SELECT', N'Yes|No|Exempt', 1, 1, 9, GETDATE()),
(N'PGWP', N'Language', N'language_test_date', N'Language Test Date', NULL, N'DATE', NULL, 0, 0, 10, GETDATE()),
(N'PGWP', N'Language', N'language_scores', N'Language Test Scores', NULL, N'TEXT', NULL, 0, 0, 11, GETDATE()),
(N'PGWP', N'Study History', N'full_time_all_terms', N'Were all terms full-time?', NULL, N'SELECT', N'Yes|No', 1, 1, 12, GETDATE()),
(N'PGWP', N'Study History', N'authorized_leaves', N'Any authorized leaves or part-time terms?', NULL, N'SELECT', N'Yes|No', 0, 0, 13, GETDATE()),
(N'PGWP', N'Study History', N'program_transfers', N'Any program or school transfers?', NULL, N'SELECT', N'Yes|No', 0, 1, 14, GETDATE()),
(N'PGWP', N'Passport', N'passport_expiry', N'Passport Expiry Date', NULL, N'DATE', NULL, 1, 1, 15, GETDATE());

-- ---- Super Visa ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, [options], required, is_trigger_question, sort_order, created_at) VALUES
(N'SUPER_VISA', N'Applicant', N'applicant_name', N'Applicant Full Name', NULL, N'TEXT', NULL, 1, 0, 1, GETDATE()),
(N'SUPER_VISA', N'Applicant', N'applicant_relationship', N'Relationship to Host', NULL, N'SELECT', N'Parent|Grandparent', 1, 1, 2, GETDATE()),
(N'SUPER_VISA', N'Applicant', N'applicant_passport_expiry', N'Passport Expiry Date', NULL, N'DATE', NULL, 1, 0, 3, GETDATE()),
(N'SUPER_VISA', N'Applicant', N'applicant_country_residence', N'Country of Residence', NULL, N'TEXT', NULL, 1, 0, 4, GETDATE()),
(N'SUPER_VISA', N'Host', N'host_name', N'Host Full Name', NULL, N'TEXT', NULL, 1, 0, 5, GETDATE()),
(N'SUPER_VISA', N'Host', N'host_status', N'Host Immigration Status', NULL, N'SELECT', N'Canadian Citizen|Permanent Resident', 1, 1, 6, GETDATE()),
(N'SUPER_VISA', N'Host', N'host_relationship_proof', N'What proof of relationship is available?', NULL, N'TEXT', NULL, 1, 0, 7, GETDATE()),
(N'SUPER_VISA', N'Host', N'household_size', N'Total Household Size (including applicant)', NULL, N'TEXT', NULL, 1, 0, 8, GETDATE()),
(N'SUPER_VISA', N'Finances', N'host_income_docs_available', N'Are host income documents available (NOA, employment letter)?', NULL, N'SELECT', N'Yes|No|Partial', 1, 1, 9, GETDATE()),
(N'SUPER_VISA', N'Finances', N'host_support_evidence', N'What financial support evidence is available?', NULL, N'TEXTAREA', NULL, 0, 0, 10, GETDATE()),
(N'SUPER_VISA', N'Insurance', N'has_medical_insurance', N'Has qualifying medical insurance been obtained?', NULL, N'SELECT', N'Yes|No|In Progress', 1, 1, 11, GETDATE()),
(N'SUPER_VISA', N'Insurance', N'insurance_policy_details', N'Insurance Provider and Policy Number', NULL, N'TEXT', NULL, 0, 0, 12, GETDATE()),
(N'SUPER_VISA', N'Insurance', N'insurance_coverage_amount', N'Coverage Amount', NULL, N'TEXT', NULL, 0, 0, 13, GETDATE()),
(N'SUPER_VISA', N'Purpose/Duration', N'planned_arrival', N'Planned Arrival Date', NULL, N'DATE', NULL, 0, 0, 14, GETDATE()),
(N'SUPER_VISA', N'Purpose/Duration', N'planned_stay_location', N'Where in Canada will applicant stay?', NULL, N'TEXT', NULL, 0, 0, 15, GETDATE()),
(N'SUPER_VISA', N'History', N'prior_refusals', N'Any prior visa refusals?', NULL, N'SELECT', N'Yes|No', 1, 1, 16, GETDATE()),
(N'SUPER_VISA', N'History', N'refusal_details', N'If yes, provide details', NULL, N'TEXTAREA', NULL, 0, 0, 17, GETDATE()),
(N'SUPER_VISA', N'History', N'previous_travel_history', N'Previous travel history', NULL, N'TEXTAREA', NULL, 0, 0, 18, GETDATE()),
(N'SUPER_VISA', N'History', N'medical_concerns', N'Any medical concerns?', NULL, N'SELECT', N'Yes|No', 0, 0, 19, GETDATE());

-- ======================== CONDITIONAL RULES ========================

-- Study Permit rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'STUDY_PERMIT', N'is_quebec', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include CAQ requirement when school is in Quebec', 1, GETDATE()),
(N'STUDY_PERMIT', N'school_province', N'QC', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include CAQ requirement when province is Quebec', 1, GETDATE()),
(N'STUDY_PERMIT', N'has_sponsor', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include sponsor financial documents when sponsor is involved', 1, GETDATE()),
(N'STUDY_PERMIT', N'spouse_accompanying', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include spouse/family documents when family is accompanying', 1, GETDATE()),
(N'STUDY_PERMIT', N'children_accompanying', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include dependent children documents when children are accompanying', 1, GETDATE()),
(N'STUDY_PERMIT', N'prior_refusals', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include prior refusal explanation letter when refusals exist', 1, GETDATE()),
(N'STUDY_PERMIT', N'funding_source', N'Scholarship', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include scholarship award letter when scholarship-funded', 1, GETDATE()),
(N'STUDY_PERMIT', N'has_gic', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include GIC confirmation when GIC is available', 1, GETDATE());

-- Visitor Visa rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'VISITOR_VISA', N'has_canadian_host', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include invitation letter and host documents when host exists', 1, GETDATE()),
(N'VISITOR_VISA', N'visit_purpose', N'Business', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include business invitation/conference documents for business visits', 1, GETDATE()),
(N'VISITOR_VISA', N'prior_visa_refusals', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include refusal explanation when prior refusals exist', 1, GETDATE()),
(N'VISITOR_VISA', N'funding_type', N'Host-funded', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include host financial proof when host is funding trip', 1, GETDATE());

-- Spousal Sponsorship rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'SPOUSAL_SPONSORSHIP', N'relationship_type', N'Common-Law', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include statutory declaration of common-law union', 1, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'relationship_type', N'Conjugal', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include conjugal relationship evidence and barrier explanation', 1, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'has_children', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include children''s identity/custody documents', 1, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'sponsor_previously_sponsored', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include proof that 3-year undertaking from previous sponsorship has ended', 1, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'sponsor_in_quebec', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include Quebec CSQ/undertaking forms for Quebec sponsorship', 1, GETDATE()),
(N'SPOUSAL_SPONSORSHIP', N'prior_refusals_removals', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include rehabilitation/explanation for prior refusals or inadmissibility', 1, GETDATE());

-- Express Entry rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'EXPRESS_ENTRY', N'education_canadian_or_foreign', N'Foreign', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include ECA report for foreign education credentials', 1, GETDATE()),
(N'EXPRESS_ENTRY', N'education_canadian_or_foreign', N'Both', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include ECA report when applicant has both Canadian and foreign education', 1, GETDATE()),
(N'EXPRESS_ENTRY', N'has_dependents', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include dependent children documents', 1, GETDATE()),
(N'EXPRESS_ENTRY', N'spouse_accompanying', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include spouse documents and language test', 1, GETDATE()),
(N'EXPRESS_ENTRY', N'prior_refusals', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include prior refusal explanation letter', 1, GETDATE()),
(N'EXPRESS_ENTRY', N'program_path', N'PNP Express Entry', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include PNP nomination certificate', 1, GETDATE());

-- Work Permit rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'WORK_PERMIT', N'lmia_status', N'LMIA Required (has LMIA)', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include LMIA confirmation letter', 1, GETDATE()),
(N'WORK_PERMIT', N'wp_type', N'Open Work Permit', N'EQUALS', N'EXCLUDE', NULL, NULL, N'Exclude employer-specific documents for open work permits', 1, GETDATE()),
(N'WORK_PERMIT', N'spouse_accompanying', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include spouse/family documents', 1, GETDATE()),
(N'WORK_PERMIT', N'prior_refusals', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include prior refusal explanation letter', 1, GETDATE()),
(N'WORK_PERMIT', N'application_location', N'Inside Canada', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include current status documents for in-Canada applications', 1, GETDATE());

-- LMIA rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'LMIA', N'lmia_stream', N'Global Talent Stream', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include ESDC referral partner letter for Global Talent Stream', 1, GETDATE()),
(N'LMIA', N'lmia_stream', N'Agricultural', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include seasonal agricultural worker program documents', 1, GETDATE()),
(N'LMIA', N'lmia_stream', N'Caregiver', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include caregiver-specific employer requirements', 1, GETDATE()),
(N'LMIA', N'recent_layoffs', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include layoff justification when recent layoffs occurred', 1, GETDATE());

-- Citizenship rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'CITIZENSHIP', N'applicant_age_category', N'Minor (under 18)', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include parent/guardian documents for minor applicants', 1, GETDATE()),
(N'CITIZENSHIP', N'applicant_age_category', N'Minor (under 18)', N'EQUALS', N'EXCLUDE', NULL, NULL, N'Exclude language test for minor applicants', 1, GETDATE()),
(N'CITIZENSHIP', N'absences_outside_canada', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include absence log with proof of travel dates', 1, GETDATE()),
(N'CITIZENSHIP', N'criminality', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include court records and rehabilitation documents', 1, GETDATE()),
(N'CITIZENSHIP', N'name_consistency', N'No', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include legal name change certificates', 1, GETDATE());

-- PR Card rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'PR_CARD_PRTD', N'application_reason', N'PR Travel Document (PRTD)', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include PRTD-specific documents and proof of ties to Canada', 1, GETDATE()),
(N'PR_CARD_PRTD', N'card_lost_stolen_damaged', N'Stolen', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include police report for stolen card', 1, GETDATE()),
(N'PR_CARD_PRTD', N'name_gender_changes', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include legal name/gender change documents', 1, GETDATE());

-- PGWP rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'PGWP', N'transcript_available', N'Pending', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include interim transcript and expected date of final transcript', 1, GETDATE()),
(N'PGWP', N'program_transfers', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include transfer letters from all institutions attended', 1, GETDATE()),
(N'PGWP', N'language_test_taken', N'No', N'EQUALS', N'INCLUDE', NULL, NULL, N'Flag: language test may be required depending on graduation date', 1, GETDATE());

-- Super Visa rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, [description], active, created_at) VALUES
(N'SUPER_VISA', N'has_medical_insurance', N'No', N'EQUALS', N'INCLUDE', NULL, NULL, N'Flag: medical insurance is mandatory for Super Visa', 1, GETDATE()),
(N'SUPER_VISA', N'prior_refusals', N'Yes', N'EQUALS', N'INCLUDE', NULL, NULL, N'Include prior refusal explanation letter', 1, GETDATE());
