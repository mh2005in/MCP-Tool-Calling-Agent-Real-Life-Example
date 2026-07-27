-- ============================================================
-- Immigration Automation - Seed Data (Oracle)
-- V2: Initial reference data + demo consultant
-- ============================================================

-- ======================== DEMO CONSULTANT ========================

INSERT INTO consultants (consultant_number, full_name, email, license_number, company_name, is_admin, active, mcp_api_key, created_at)
VALUES ('CO0000000000001', 'Demo Consultant', 'demo@immiauto.ca', 'R000000', 'Demo Immigration Services', 1, 1, 'mcp_demo00000000000000000000000000', SYSTIMESTAMP);

-- ======================== CHECKLIST TEMPLATES ========================

-- ---- Study Permit ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Identity', 'Passport biographical page', 'Clear scan of your passport page showing your photo, name, and passport number.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Identity', 'National ID card', 'Front and back of your national identity card, if applicable.', 0, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Identity', 'Birth certificate', 'Official birth certificate with English or French translation if needed.', 0, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Education', 'Letter of acceptance from DLI', 'Original letter of acceptance from a Designated Learning Institution in Canada.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Education', 'Previous transcripts', 'Academic transcripts from your most recent educational institution.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Education', 'Previous diplomas or degrees', 'Copies of diplomas or degree certificates you have earned.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Attestation', 'Provincial Attestation Letter (PAL/TAL)', 'Attestation letter from the province where your DLI is located.', 1, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Attestation', 'Quebec Acceptance Certificate (CAQ)', 'Certificat d''acceptation du Québec, required for Quebec institutions.', 1, 1, 'Required if school is in Quebec', 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Financial', 'Proof of funds - bank statements', 'Bank statements from the last 4 months showing sufficient funds for tuition and living expenses.', 1, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Financial', 'Tuition payment receipt', 'Receipt showing tuition has been paid or deposit made to the school.', 0, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Financial', 'Scholarship or funding letter', 'Letter confirming scholarship, bursary, or other funding if applicable.', 0, 0, NULL, 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Financial', 'GIC certificate', 'Guaranteed Investment Certificate from a participating financial institution, if applicable.', 0, 0, NULL, 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Financial', 'Sponsor financial documents', 'Bank statements, employment letter, and income proof from your financial sponsor.', 0, 1, 'Required if a sponsor is providing financial support', 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Language', 'Language test results (IELTS/CELPIP/TEF/TCF)', 'Official language test results, if required by the program or for SDS.', 0, 0, NULL, 14, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Purpose', 'Study plan / statement of purpose', 'Written explanation of why you chose this program and your plans after graduation.', 1, 0, NULL, 15, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Ties to Home', 'Proof of ties to home country', 'Employment letter, property documents, family obligations, or other evidence of intent to return.', 0, 0, NULL, 16, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Immigration History', 'Previous visa refusal letter', 'If you have been refused a visa before, provide the refusal letter.', 0, 0, NULL, 17, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Immigration History', 'Current visa or permit (if in Canada)', 'Copy of current study or work permit, if applying from inside Canada.', 0, 0, NULL, 18, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Immigration History', 'Previous travel history', 'Copies of previous visas and travel stamps from past international travel.', 0, 0, NULL, 19, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Civil Status', 'Marriage certificate', 'If married, provide your marriage certificate.', 0, 0, NULL, 20, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Family', 'Spouse passport and documents', 'Passport and supporting documents for accompanying spouse.', 0, 1, 'Required if spouse is accompanying', 21, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Family', 'Children birth certificates and passports', 'Birth certificates and passports for accompanying dependent children.', 0, 1, 'Required if children are accompanying', 22, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Representative', 'Use of Representative form (IMM 5476)', 'Signed form authorizing your immigration consultant to act on your behalf.', 1, 0, NULL, 23, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Photos', 'Passport-size photos', 'Two recent passport-size photos meeting IRCC specifications.', 1, 0, NULL, 24, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Country-specific', 'Police clearance certificate', 'Police certificate from your country or any country you lived in for 6+ months since age 18.', 0, 0, NULL, 25, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('STUDY_PERMIT', 'Country-specific', 'Medical exam results', 'Results of immigration medical exam, if required based on your country.', 0, 0, NULL, 26, 1, 0, SYSTIMESTAMP);

-- ---- Visitor Visa ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Identity', 'Passport biographical page', 'Clear scan of your passport showing photo, name, and passport number.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Identity', 'National ID card', 'Front and back scan of national identity card.', 0, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Financial', 'Bank statements (3 months)', 'Bank statements from the last 3 months showing sufficient funds.', 1, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Financial', 'Employment letter', 'Letter from employer confirming your position, salary, and approved leave.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Financial', 'Property or asset documents', 'Documents showing property ownership, investments, or other ties to home country.', 0, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Host Documents', 'Host invitation letter', 'Formal letter from host inviting you to Canada, with dates, purpose, and accommodation details.', 0, 1, 'Required if visiting a host in Canada', 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Host Documents', 'Host proof of status', 'Copy of host''s Canadian citizenship, PR card, or valid immigration status.', 0, 1, 'Required if visiting a host in Canada', 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Host Documents', 'Host proof of income/finances', 'Host''s employment letter, NOA, or bank statements if host is financially supporting visit.', 0, 1, 'Required if host is funding the trip', 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Purpose of Visit', 'Travel itinerary', 'Flight booking or travel plan showing intended dates.', 0, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Purpose of Visit', 'Hotel reservations', 'Accommodation booking confirmations.', 0, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Purpose of Visit', 'Business invitation / conference registration', 'Business invitation letter or conference registration for business visitors.', 0, 1, 'Required for business visitors', 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Purpose of Visit', 'Event tickets or registration', 'Tickets, registration confirmation, or event details.', 0, 1, 'Required for event visitors', 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Status Documents', 'Current immigration status (if in Canada)', 'Copy of current visa, permit, or visitor record if applying from inside Canada.', 0, 0, NULL, 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Ties to Home Country', 'Business registration', 'Business registration or ownership documents if self-employed.', 0, 0, NULL, 14, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Ties to Home Country', 'Proof of family ties', 'Documents showing family members remaining in your home country.', 0, 0, NULL, 15, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Civil Status', 'Marriage certificate', 'If married, provide marriage certificate.', 0, 0, NULL, 16, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Civil Status', 'Children birth certificates', 'Birth certificates of dependent children, if applicable.', 0, 0, NULL, 17, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Travel History', 'Previous visas and travel stamps', 'Copies of previous visas and passport stamps from past travel.', 0, 0, NULL, 18, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Immigration History', 'Previous refusal explanation', 'Written explanation and supporting documents for any prior visa refusals.', 0, 1, 'Required if previously refused a visa', 19, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('VISITOR_VISA', 'Photos', 'Passport-size photos', 'Two recent passport-size photos meeting IRCC specifications.', 1, 0, NULL, 20, 1, 0, SYSTIMESTAMP);

-- ---- Spousal Sponsorship ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Identity - Sponsor', 'Sponsor passport', 'Clear scan of sponsor''s Canadian passport or PR card.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Identity - Sponsor', 'Sponsor proof of status', 'PR card, citizenship certificate, or Canadian passport.', 1, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Identity - Sponsor', 'Sponsor birth certificate', 'Official birth certificate of the sponsor.', 0, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Identity - Applicant', 'Applicant passport', 'Clear scan of applicant''s passport biographical page.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Identity - Applicant', 'Applicant birth certificate', 'Official birth certificate of the applicant.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship', 'Marriage certificate', 'Official marriage certificate.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship', 'Statutory declaration of common-law union', 'Sworn declaration confirming 12+ months of continuous cohabitation.', 1, 1, 'Required for common-law relationships', 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship', 'Relationship timeline narrative', 'Written narrative of how you met, your relationship history, and milestones.', 1, 0, NULL, 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Photos', 'Photos together', 'Photos at different times, with family, at events, in different locations.', 1, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Communication', 'Chat/messaging history', 'Screenshots of text messages, WhatsApp, Facebook Messenger, or other messaging apps.', 1, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Communication', 'Call logs and video call history', 'Phone call logs and video call screenshots/history.', 1, 0, NULL, 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Travel', 'Travel records together', 'Flight bookings, boarding passes, hotel reservations, or passport stamps from trips together.', 0, 0, NULL, 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Financial', 'Joint bank accounts or shared finances', 'Joint bank account statements or evidence of shared financial responsibilities.', 0, 0, NULL, 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Financial', 'Joint lease, utilities, or insurance', 'Shared lease agreements, utility bills, or insurance policies showing both names.', 0, 0, NULL, 14, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Evidence - Third Party', 'Letters from friends and family', 'Sworn affidavits or letters from people who know the relationship.', 0, 0, NULL, 15, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Financial - Sponsor', 'Tax returns / NOA', 'Notice of Assessment from CRA for the most recent tax year.', 1, 0, NULL, 16, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Financial - Sponsor', 'Employment letter', 'Letter from sponsor''s employer confirming employment and salary.', 0, 0, NULL, 17, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Financial - Sponsor', 'Bank statements (3 months)', 'Sponsor''s bank statements for the last 3 months.', 0, 0, NULL, 18, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Dependents', 'Dependent children birth certificates', 'Birth certificates for all dependent children included in the application.', 0, 1, 'Required if there are dependent children', 19, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Dependents', 'Custody or adoption documents', 'Court orders, custody agreements, or adoption certificates.', 0, 1, 'Required if there are custody arrangements', 20, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Quebec', 'Quebec undertaking (CSQ forms)', 'Quebec-specific sponsorship undertaking forms.', 0, 1, 'Required if sponsor lives in Quebec', 21, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Previous Sponsorship', 'Previous sponsorship proof of completion', 'Proof that 3-year undertaking from a previous sponsorship has ended.', 0, 1, 'Required if sponsor has sponsored before', 22, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Country-specific', 'Police clearance - applicant', 'Police certificate from applicant''s country and any country lived in 6+ months since age 18.', 1, 0, NULL, 23, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Country-specific', 'Police clearance - sponsor', 'Police certificate for the sponsor, if applicable.', 0, 0, NULL, 24, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Country-specific', 'Medical exam results', 'Immigration medical exam results for the applicant and dependents.', 1, 0, NULL, 25, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Photos', 'Passport-size photos', 'Two recent passport-size photos meeting IRCC specifications.', 1, 0, NULL, 26, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Representative', 'Use of Representative form (IMM 5476)', 'Signed form authorizing your immigration consultant to act on your behalf.', 1, 0, NULL, 27, 1, 0, SYSTIMESTAMP);

-- ---- Express Entry ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Identity', 'Passport biographical page', 'Clear scan of passport.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Identity', 'Birth certificate', 'Official birth certificate.', 0, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Language', 'Language test results (IELTS/CELPIP/TEF/TCF)', 'Official language test results for primary applicant.', 1, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Language', 'Spouse language test results', 'Language test results for accompanying spouse, if applicable.', 0, 1, 'Required if spouse is accompanying', 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Education', 'Canadian transcripts and diplomas', 'Transcripts and diplomas from Canadian educational institutions.', 0, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Education', 'Educational Credential Assessment (ECA)', 'WES, IQAS, or other designated ECA report for foreign education.', 1, 1, 'Required for foreign education credentials', 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Education', 'Foreign transcripts and diplomas', 'Transcripts and diplomas from foreign educational institutions.', 0, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Work Experience', 'Employment reference letters', 'Detailed reference letters for each qualifying work period: duties, hours, dates, salary.', 1, 0, NULL, 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Work Experience', 'Pay stubs or tax documents', 'Pay stubs, T4s, or equivalent to corroborate employment claims.', 0, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Financial', 'Proof of settlement funds', 'Bank statements, investment statements, or GIC showing required funds.', 1, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Job Offer', 'Valid job offer letter', 'Signed employment offer from a Canadian employer.', 0, 1, 'Required if claiming job offer points', 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Job Offer', 'LMIA confirmation (if applicable)', 'Labour Market Impact Assessment for the job offer.', 0, 1, 'Required if job offer requires LMIA', 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'PNP', 'Provincial Nomination Certificate', 'Official nomination certificate from a Canadian province or territory.', 0, 1, 'Required for PNP Express Entry stream', 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Police Certificates', 'Police certificate - country of citizenship', 'Police certificate from your country of citizenship.', 1, 0, NULL, 14, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Police Certificates', 'Police certificate - countries lived 6+ months', 'Police certificates from every country you lived in 6+ months since age 18.', 1, 0, NULL, 15, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Medical', 'Immigration medical exam (IME)', 'Medical exam from an IRCC-designated panel physician.', 1, 0, NULL, 16, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Civil Status', 'Marriage certificate or divorce decree', 'Marriage certificate, or divorce/annulment decree if previously married.', 0, 0, NULL, 17, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Dependents', 'Dependent children documents', 'Birth certificates, passports, and custody documents for dependent children.', 0, 1, 'Required if including dependents', 18, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Photos', 'Passport-size photos', 'Photos meeting IRCC specifications for applicant and all family members.', 1, 0, NULL, 19, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('EXPRESS_ENTRY', 'Representative', 'Use of Representative form (IMM 5476)', 'Signed form authorizing consultant.', 1, 0, NULL, 20, 1, 0, SYSTIMESTAMP);

-- ---- Work Permit ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Identity', 'Passport biographical page', 'Clear scan of passport.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Job/Employer', 'Job offer letter', 'Signed employment offer with title, duties, wage, hours, and location.', 1, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Job/Employer', 'Employer LMIA confirmation', 'Positive LMIA from ESDC, if applicable.', 0, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Job/Employer', 'Employer compliance fee receipt', 'Proof employer paid the compliance fee ($230).', 0, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Job/Employer', 'Employment contract', 'Signed contract with terms and conditions.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Qualifications', 'Resume / CV', 'Current resume showing relevant experience.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Qualifications', 'Educational credentials', 'Diplomas, degrees, or certificates relevant to the job.', 0, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Qualifications', 'Professional licence or certification', 'Licence or certification required for the occupation.', 0, 0, NULL, 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Experience', 'Previous employment reference letters', 'Reference letters from previous employers detailing duties and dates.', 0, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Status', 'Current Canadian status documents', 'Current work permit, study permit, or visitor record.', 0, 1, 'Required if applying from inside Canada', 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Status', 'Status extension/restoration application', 'Proof of pending status application if status has expired.', 0, 1, 'Required if current status has expired', 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Family', 'Spouse open work permit documents', 'Spouse passport, marriage certificate, and SOWP application documents.', 0, 1, 'Required if spouse is applying for open work permit', 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Family', 'Dependent children documents', 'Birth certificates and passports for accompanying children.', 0, 1, 'Required if children are accompanying', 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Financial', 'Proof of funds for initial settlement', 'Bank statements showing funds for initial period in Canada.', 0, 0, NULL, 14, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Photos', 'Passport-size photos', 'Photos meeting IRCC specifications.', 1, 0, NULL, 15, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Country-specific', 'Police clearance certificate', 'Police certificate if required based on country.', 0, 0, NULL, 16, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Country-specific', 'Medical exam results', 'Immigration medical exam if required.', 0, 0, NULL, 17, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('WORK_PERMIT', 'Representative', 'Use of Representative form (IMM 5476)', 'Signed form authorizing consultant.', 1, 0, NULL, 18, 1, 0, SYSTIMESTAMP);

-- ---- LMIA ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Employer Identity', 'Business licence or registration', 'Provincial or federal business registration documents.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Employer Identity', 'CRA Business Number confirmation', 'Proof of CRA Business Number.', 1, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Employer Identity', 'Articles of incorporation', 'Corporate registration or articles of incorporation.', 0, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Business Legitimacy', 'T4 Summary (previous year)', 'T4 Summary showing payroll for Canadian employees.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Business Legitimacy', 'Financial statements or tax returns', 'Business financial statements or corporate tax returns.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Business Legitimacy', 'Commercial lease or property proof', 'Proof of business premises.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Business Legitimacy', 'Business insurance', 'Proof of WCB/WSIB or equivalent workplace insurance.', 0, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Recruitment', 'Job advertisement screenshots', 'Screenshots of all job ads with dates, platforms, and content.', 1, 0, NULL, 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Recruitment', 'Job Bank posting confirmation', 'Proof of posting on the Government of Canada Job Bank for minimum 4 weeks.', 1, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Recruitment', 'Recruitment summary report', 'Summary of number of applicants, interviews conducted, and reasons for non-hire.', 1, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Recruitment', 'Interview records', 'Records of interviews conducted with Canadian applicants.', 0, 0, NULL, 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Transition Plan', 'Transition plan document', 'Written plan to reduce reliance on temporary foreign workers.', 1, 0, NULL, 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Provincial', 'Provincial attestation or requirements', 'Province-specific attestation or documentation requirements.', 0, 1, 'Required by some provinces', 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Worker Documents', 'Candidate resume / CV', 'Resume of the foreign worker being hired.', 1, 0, NULL, 14, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Worker Documents', 'Candidate credentials and qualifications', 'Proof of qualifications relevant to the position.', 0, 0, NULL, 15, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Fees', 'LMIA processing fee payment receipt ($1,000)', 'Proof of payment of the LMIA processing fee.', 1, 0, NULL, 16, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('LMIA', 'Fees', 'Employer compliance fee receipt ($230)', 'Proof of compliance fee payment.', 1, 0, NULL, 17, 1, 0, SYSTIMESTAMP);

-- ---- Citizenship ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'PR Proof', 'PR card (front and back)', 'Clear scan of both sides of current or most recent PR card.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'PR Proof', 'Confirmation of Permanent Residence (COPR)', 'Original COPR or Record of Landing.', 1, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Identity', 'Passport biographical page', 'Current passport biographical page.', 1, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Identity', 'Two passport-size photos', 'Photos meeting IRCC citizenship application specifications.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Travel History', 'Travel journal / absence log', 'Detailed log of all trips outside Canada during eligibility period with dates and destinations.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Travel History', 'Passport stamps and boarding passes', 'Evidence supporting declared travel dates.', 0, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Language', 'Language proof (IELTS/CELPIP/TEF/TCF or equivalent)', 'Official language test results or proof of CLB 4+ in English or French.', 1, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Tax Filing', 'Notice of Assessment (NOA) - 3 tax years', 'NOAs from CRA for 3 of the last 5 tax years within eligibility period.', 1, 0, NULL, 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Tax Filing', 'T1 General tax returns', 'T1 returns corresponding to the NOAs provided.', 0, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Residence', 'Proof of address in Canada', 'Utility bills, lease agreements, or bank statements showing Canadian address.', 0, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Name Change', 'Legal name change certificate', 'Court order or official document showing legal name change.', 0, 1, 'Required if name has changed since becoming PR', 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Criminal', 'Court records or discharge documents', 'Records of criminal charges, convictions, or absolute/conditional discharge.', 0, 1, 'Required if applicant has criminal history', 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Minor Applicant', 'Parent/guardian consent and ID', 'Both parents'' ID and signed consent for minor citizenship application.', 0, 1, 'Required for applicants under 18', 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('CITIZENSHIP', 'Representative', 'Use of Representative form', 'Signed form authorizing consultant.', 1, 0, NULL, 14, 1, 0, SYSTIMESTAMP);

-- ---- PR Card / PRTD ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'PR Proof', 'Confirmation of Permanent Residence (COPR)', 'Original COPR or Record of Landing.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'PR Proof', 'Previous PR card (if available)', 'Most recent PR card, even if expired.', 0, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Identity', 'Passport biographical page', 'Current valid passport.', 1, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Identity', 'Two passport-size photos', 'Photos meeting IRCC specifications.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Residence', 'Proof of Canadian address', 'Utility bill, lease, or government correspondence showing current address.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Residence', 'Travel history log (5 years)', 'Detailed log of all absences from Canada in the last 5 years.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Residence', 'Passport stamps and travel evidence', 'Supporting evidence for declared travel dates.', 0, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Lost/Stolen/Damaged', 'Police report', 'Police report or incident number for lost or stolen card.', 0, 1, 'Required if card was lost or stolen', 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Lost/Stolen/Damaged', 'Damaged card scan', 'Scan of the damaged card.', 0, 1, 'Required if card is damaged', 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Name Change', 'Legal name or gender change documents', 'Court order, marriage certificate, or official change document.', 0, 1, 'Required if name or gender changed since PR was granted', 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'PRTD', 'Proof of ties to Canada', 'Evidence of home, family, employment, or financial ties to Canada.', 0, 1, 'Required for PR Travel Document applications', 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'PRTD', 'Explanation of absence from Canada', 'Written explanation for extended absence from Canada.', 0, 1, 'Required for PRTD when outside Canada', 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PR_CARD_PRTD', 'Representative', 'Use of Representative form', 'Signed form authorizing consultant.', 1, 0, NULL, 13, 1, 0, SYSTIMESTAMP);

-- ---- PGWP ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Identity', 'Passport biographical page', 'Current valid passport.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Study Completion', 'Official completion letter from DLI', 'Letter from school confirming program completion and graduation date.', 1, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Study Completion', 'Final transcript', 'Official final transcript showing all courses and grades.', 1, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Study Completion', 'Diploma or degree certificate', 'Copy of diploma or degree, if already issued.', 0, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'School/Program', 'Letter of acceptance (original)', 'Original letter of acceptance from the DLI.', 0, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Current Status', 'Current study permit', 'Copy of valid study permit.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Current Status', 'Implied status proof', 'Proof of implied status if study permit expired but PGWP applied within 90 days.', 0, 1, 'Required if study permit has expired', 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Language', 'Language test results', 'IELTS, CELPIP, TEF, or TCF results if required for field of study.', 0, 1, 'Required based on graduation date and program', 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Prior Study', 'Transfer letters from previous institutions', 'Letters from all institutions attended if programs were transferred.', 0, 1, 'Required if student transferred between programs or schools', 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Photos', 'Passport-size photos', 'Photos meeting IRCC specifications.', 1, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('PGWP', 'Representative', 'Use of Representative form', 'Signed form authorizing consultant.', 1, 0, NULL, 11, 1, 0, SYSTIMESTAMP);

-- ---- Super Visa ----
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Identity - Applicant', 'Passport biographical page', 'Applicant''s current valid passport.', 1, 0, NULL, 1, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Relationship', 'Proof of relationship to host', 'Birth certificate, marriage certificate, or other documents proving parent/grandparent relationship.', 1, 0, NULL, 2, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Host Documents', 'Host proof of citizenship or PR', 'Host''s Canadian passport, citizenship certificate, or PR card.', 1, 0, NULL, 3, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Host Documents', 'Host invitation letter', 'Formal invitation letter from host with details of stay.', 1, 0, NULL, 4, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Host Finances', 'Host Notice of Assessment (NOA)', 'CRA Notice of Assessment meeting LICO requirement for household size.', 1, 0, NULL, 5, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Host Finances', 'Host employment letter', 'Letter confirming host''s employment, position, and salary.', 1, 0, NULL, 6, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Host Finances', 'Host bank statements (3 months)', 'Host''s recent bank statements.', 0, 0, NULL, 7, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Medical Insurance', 'Private medical insurance policy', 'Proof of private medical insurance from a Canadian company, valid 1+ year, minimum $100,000 coverage.', 1, 0, NULL, 8, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Medical Insurance', 'Insurance coverage details', 'Policy details showing coverage amount, period, and emergency medical coverage.', 1, 0, NULL, 9, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Medical', 'Immigration medical exam results', 'Medical exam from IRCC-designated panel physician.', 1, 0, NULL, 10, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Travel History', 'Previous visas and travel stamps', 'Copies of previous visas and passport stamps.', 0, 0, NULL, 11, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Immigration History', 'Previous refusal explanation', 'Written explanation for any prior visa refusals.', 0, 1, 'Required if previously refused a visa', 12, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Photos', 'Passport-size photos', 'Photos meeting IRCC specifications.', 1, 0, NULL, 13, 1, 0, SYSTIMESTAMP);
INSERT INTO checklist_templates (service_type, category, document_name, description, required, conditional, condition_description, sort_order, rule_version, approved_for_use, created_at) VALUES
('SUPER_VISA', 'Representative', 'Use of Representative form', 'Signed form authorizing consultant.', 1, 0, NULL, 14, 1, 0, SYSTIMESTAMP);

-- ======================== INTAKE QUESTION TEMPLATES ========================

-- ---- Study Permit ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Applicant Identity', 'full_name', 'Full Legal Name (as on passport)', NULL, 'TEXT', NULL, 1, 0, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Applicant Identity', 'dob', 'Date of Birth', NULL, 'DATE', NULL, 1, 0, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Applicant Identity', 'citizenship', 'Country of Citizenship', NULL, 'TEXT', NULL, 1, 0, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Applicant Identity', 'passport_number', 'Passport Number', NULL, 'TEXT', NULL, 1, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Applicant Identity', 'passport_expiry', 'Passport Expiry Date', NULL, 'DATE', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Education', 'dli_name', 'Name of Designated Learning Institution (DLI)', NULL, 'TEXT', NULL, 1, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Education', 'program_name', 'Program Name', NULL, 'TEXT', NULL, 1, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Education', 'program_start_date', 'Program Start Date', NULL, 'DATE', NULL, 1, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Education', 'program_end_date', 'Program End Date', NULL, 'DATE', NULL, 1, 0, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Education', 'level_of_study', 'Level of Study', NULL, 'SELECT', 'Certificate|Diploma|Bachelor|Master|PhD|K-12|Language|Other', 1, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Location', 'school_province', 'Province of School', NULL, 'SELECT', 'AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT', 1, 1, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Location', 'is_quebec', 'Is this a Quebec institution?', 'If yes, CAQ process applies instead of PAL/TAL', 'SELECT', 'Yes|No', 1, 1, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'PAL/TAL/CAQ', 'has_pal_tal', 'Has the school issued a PAL/TAL?', NULL, 'SELECT', 'Yes|No|Not Sure', 0, 1, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'PAL/TAL/CAQ', 'has_caq', 'Has the school issued a CAQ? (Quebec only)', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 1, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Finances', 'funding_source', 'Who pays tuition and living expenses?', NULL, 'SELECT', 'Self|Parent/Family|Sponsor|Scholarship|Employer|GIC|Combination', 1, 1, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Finances', 'funds_available', 'Approximate funds available (CAD)', NULL, 'TEXT', NULL, 1, 0, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Finances', 'has_gic', 'Do you have a GIC?', NULL, 'SELECT', 'Yes|No', 0, 0, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Sponsor', 'has_sponsor', 'Is a sponsor providing financial support?', NULL, 'SELECT', 'Yes|No', 0, 1, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Sponsor', 'sponsor_relationship', 'Relationship to Sponsor', NULL, 'TEXT', NULL, 0, 0, 19, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Sponsor', 'sponsor_income_source', 'Sponsor''s Source of Income', NULL, 'TEXT', NULL, 0, 0, 20, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'History', 'prior_refusals', 'Have you had any prior visa/permit refusals?', NULL, 'SELECT', 'Yes|No', 1, 1, 21, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'History', 'prior_refusal_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 22, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'History', 'prior_overstays', 'Have you ever overstayed a visa?', NULL, 'SELECT', 'Yes|No', 1, 0, 23, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'History', 'previous_canadian_apps', 'Any previous Canadian immigration applications?', NULL, 'SELECT', 'Yes|No', 1, 0, 24, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Family', 'spouse_accompanying', 'Is your spouse/partner accompanying you?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 1, 25, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Family', 'children_accompanying', 'Are children accompanying you?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 1, 26, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('STUDY_PERMIT', 'Country-Specific', 'applying_from_country', 'Which country are you applying from?', NULL, 'TEXT', NULL, 1, 1, 27, SYSTIMESTAMP);

-- ---- Visitor Visa ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Visit Purpose', 'visit_purpose', 'Purpose of Visit', NULL, 'SELECT', 'Tourism|Family Visit|Business|Event|Medical|Other', 1, 1, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Visit Purpose', 'visit_purpose_details', 'Details of Visit Purpose', NULL, 'TEXTAREA', NULL, 0, 0, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Host', 'has_canadian_host', 'Is there a Canadian host?', NULL, 'SELECT', 'Yes|No', 1, 1, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Host', 'host_relationship', 'Relationship to Host', NULL, 'TEXT', NULL, 0, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Host', 'host_name', 'Host Full Name', NULL, 'TEXT', NULL, 0, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Host', 'host_status', 'Host''s Immigration Status', NULL, 'SELECT', 'Citizen|Permanent Resident|Temporary Resident|Other', 0, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Duration', 'planned_arrival', 'Planned Arrival Date', NULL, 'DATE', NULL, 1, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Duration', 'planned_departure', 'Planned Departure Date', NULL, 'DATE', NULL, 1, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Funding', 'funding_type', 'Who is funding the trip?', NULL, 'SELECT', 'Self-funded|Host-funded|Employer|Combination', 1, 1, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Employment', 'current_employer', 'Current Employer', NULL, 'TEXT', NULL, 0, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Employment', 'job_title', 'Job Title', NULL, 'TEXT', NULL, 0, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Employment', 'leave_approved', 'Has leave been approved?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Assets/Ties', 'property_owned', 'Do you own property in your home country?', NULL, 'SELECT', 'Yes|No', 0, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Assets/Ties', 'family_ties', 'Family members remaining in home country', NULL, 'TEXTAREA', NULL, 0, 0, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Assets/Ties', 'business_owned', 'Do you own a business?', NULL, 'SELECT', 'Yes|No', 0, 0, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Travel History', 'countries_visited', 'Countries visited in the last 10 years', NULL, 'TEXTAREA', NULL, 0, 0, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Travel History', 'prior_canadian_visits', 'Have you visited Canada before?', NULL, 'SELECT', 'Yes|No', 0, 0, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Refusals', 'prior_visa_refusals', 'Any previous visa refusals (any country)?', NULL, 'SELECT', 'Yes|No', 1, 1, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('VISITOR_VISA', 'Refusals', 'refusal_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 19, SYSTIMESTAMP);

-- ---- Spousal Sponsorship ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Sponsor', 'sponsor_status', 'Sponsor''s Immigration Status', NULL, 'SELECT', 'Canadian Citizen|Permanent Resident', 1, 1, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Sponsor', 'sponsor_in_canada', 'Is sponsor currently living in Canada?', NULL, 'SELECT', 'Yes|No', 1, 1, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Sponsor', 'sponsor_province', 'Sponsor''s Province of Residence', NULL, 'SELECT', 'AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT|Outside Canada', 1, 1, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Applicant', 'applicant_country', 'Applicant''s Country of Residence', NULL, 'TEXT', NULL, 1, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Applicant', 'applicant_current_status', 'Applicant''s Current Immigration Status', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Applicant', 'applicant_prior_marriages', 'Has applicant been previously married?', NULL, 'SELECT', 'Yes|No', 1, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Type', 'relationship_type', 'Relationship Type', NULL, 'SELECT', 'Married|Common-Law|Conjugal', 1, 1, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Timeline', 'first_contact_date', 'Date of First Contact', NULL, 'DATE', NULL, 1, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Timeline', 'first_meeting_date', 'Date of First In-Person Meeting', NULL, 'DATE', NULL, 1, 0, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Timeline', 'engagement_date', 'Engagement Date (if applicable)', NULL, 'DATE', NULL, 0, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Relationship Timeline', 'marriage_cohabitation_date', 'Marriage/Cohabitation Date', NULL, 'DATE', NULL, 1, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Cohabitation', 'shared_address', 'Do you share an address?', NULL, 'SELECT', 'Yes|No', 1, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Cohabitation', 'cohabitation_dates', 'Dates of Cohabitation', NULL, 'TEXT', NULL, 0, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Cohabitation', 'cohabitation_proof', 'What cohabitation proof is available?', NULL, 'TEXTAREA', NULL, 0, 0, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Dependents', 'has_children', 'Are there any children?', NULL, 'SELECT', 'Yes|No', 1, 1, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Dependents', 'custody_issues', 'Any custody issues?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 0, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Previous Sponsorship', 'sponsor_previously_sponsored', 'Has the sponsor sponsored before?', NULL, 'SELECT', 'Yes|No', 1, 1, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Criminal/Immigration History', 'prior_refusals_removals', 'Any prior refusals, removals, or criminality?', NULL, 'SELECT', 'Yes|No', 1, 1, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Criminal/Immigration History', 'refusal_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 19, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'Quebec', 'sponsor_in_quebec', 'Does the sponsor live in Quebec?', 'Quebec has a separate sponsorship process', 'SELECT', 'Yes|No', 1, 1, 20, SYSTIMESTAMP);

-- ---- Express Entry ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Program Path', 'program_path', 'Which Express Entry program?', NULL, 'SELECT', 'Federal Skilled Worker (FSW)|Canadian Experience Class (CEC)|Federal Skilled Trades (FST)|PNP Express Entry', 1, 1, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Work History', 'current_employer', 'Current/Most Recent Employer', NULL, 'TEXT', NULL, 1, 0, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Work History', 'job_title', 'Job Title', NULL, 'TEXT', NULL, 1, 0, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Work History', 'noc_teer', 'NOC/TEER Code (if known)', NULL, 'TEXT', NULL, 0, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Work History', 'employment_dates', 'Employment Start and End Dates', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Work History', 'hours_per_week', 'Hours Per Week', NULL, 'TEXT', NULL, 1, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Work History', 'job_duties', 'Main Job Duties', NULL, 'TEXTAREA', NULL, 1, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Education', 'education_canadian_or_foreign', 'Is your highest education Canadian or Foreign?', NULL, 'SELECT', 'Canadian|Foreign|Both', 1, 1, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Education', 'eca_completed', 'Has an Educational Credential Assessment (ECA) been completed?', NULL, 'SELECT', 'Yes|No|Not Applicable', 1, 1, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Education', 'highest_education', 'Highest Level of Education', NULL, 'SELECT', 'High School|1-Year Post-Secondary|2-Year Post-Secondary|3-Year Post-Secondary|Bachelor|Two or More Degrees|Master|PhD', 1, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Language', 'language_test_type', 'Language Test Type', NULL, 'SELECT', 'IELTS|CELPIP|TEF|TCF|None Yet', 1, 1, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Language', 'language_test_date', 'Test Date', NULL, 'DATE', NULL, 0, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Language', 'language_scores', 'Test Scores (L/R/W/S)', NULL, 'TEXT', NULL, 0, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Marital Status', 'marital_status', 'Marital Status', NULL, 'SELECT', 'Single|Married|Common-Law|Separated|Divorced|Widowed', 1, 1, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Marital Status', 'spouse_accompanying', 'Is spouse/partner accompanying?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 1, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Dependents', 'has_dependents', 'Any dependent children?', NULL, 'SELECT', 'Yes|No', 1, 1, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Dependents', 'custody_issues', 'Any custody issues?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 0, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Funds', 'funds_required', 'Are proof of funds required or exempt?', NULL, 'SELECT', 'Required|Exempt (have valid job offer)|Not Sure', 1, 1, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Travel/Residence', 'countries_lived_in', 'Countries lived in for 6+ months since age 18', 'Used to determine police certificate requirements', 'TEXTAREA', NULL, 1, 1, 19, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Prior Applications', 'prior_refusals', 'Any prior Canadian refusals, inadmissibility, or misrepresentation?', NULL, 'SELECT', 'Yes|No', 1, 1, 20, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('EXPRESS_ENTRY', 'Prior Applications', 'prior_refusal_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 21, SYSTIMESTAMP);

-- ---- Work Permit ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Application Location', 'application_location', 'Where are you applying from?', NULL, 'SELECT', 'Inside Canada|Outside Canada|Port of Entry', 1, 1, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Work Permit Type', 'wp_type', 'Type of Work Permit', NULL, 'SELECT', 'Employer-Specific|Open Work Permit', 1, 1, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'LMIA', 'lmia_status', 'LMIA Status', NULL, 'SELECT', 'LMIA Required (has LMIA)|LMIA Exempt|Unknown/Not Sure', 1, 1, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'LMIA', 'lmia_number', 'LMIA Number (if applicable)', NULL, 'TEXT', NULL, 0, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Employer', 'employer_name', 'Employer Legal Name', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Employer', 'employer_cra_bn', 'Employer CRA Business Number', NULL, 'TEXT', NULL, 0, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Employer', 'employer_job_title', 'Job Title', NULL, 'TEXT', NULL, 1, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Offer', 'wage', 'Offered Wage (hourly/annual)', NULL, 'TEXT', NULL, 1, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Offer', 'work_location', 'Work Location (city, province)', NULL, 'TEXT', NULL, 1, 0, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Offer', 'job_duties', 'Job Duties', NULL, 'TEXTAREA', NULL, 1, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Offer', 'noc_teer', 'NOC/TEER Code (if known)', NULL, 'TEXT', NULL, 0, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Applicant Status', 'current_status_in_canada', 'Current Immigration Status in Canada', NULL, 'SELECT', 'Study Permit|Work Permit|Visitor Record|No Status|Not in Canada', 0, 1, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Applicant Status', 'status_expiry_date', 'Current Status Expiry Date', NULL, 'DATE', NULL, 0, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Family', 'spouse_accompanying', 'Is spouse/partner accompanying?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 1, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Family', 'children_accompanying', 'Are children accompanying?', NULL, 'SELECT', 'Yes|No|Not Applicable', 0, 1, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Qualifications', 'highest_education', 'Highest Level of Education', NULL, 'TEXT', NULL, 0, 0, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Qualifications', 'professional_licence', 'Any professional licence/certification?', NULL, 'TEXT', NULL, 0, 0, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Qualifications', 'years_experience', 'Years of Relevant Work Experience', NULL, 'TEXT', NULL, 0, 0, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Prior Refusals', 'prior_refusals', 'Any prior work permit or visa refusals?', NULL, 'SELECT', 'Yes|No', 1, 1, 19, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('WORK_PERMIT', 'Prior Refusals', 'refusal_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 20, SYSTIMESTAMP);

-- ---- LMIA ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Employer', 'employer_legal_name', 'Employer Legal Name', NULL, 'TEXT', NULL, 1, 0, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Employer', 'employer_operating_name', 'Operating/Trade Name', NULL, 'TEXT', NULL, 0, 0, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Employer', 'cra_bn', 'CRA Business Number', NULL, 'TEXT', NULL, 1, 0, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Employer', 'payroll_account', 'CRA Payroll Account Number', NULL, 'TEXT', NULL, 1, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Business', 'industry', 'Industry/Sector', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Business', 'business_address', 'Business Address', NULL, 'TEXT', NULL, 1, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Business', 'num_employees', 'Number of Employees', NULL, 'TEXT', NULL, 1, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Business', 'revenue_proof_available', 'Is revenue/financial proof available?', NULL, 'SELECT', 'Yes|No', 1, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Job', 'job_title', 'Job Title', NULL, 'TEXT', NULL, 1, 0, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Job', 'wage', 'Offered Wage', NULL, 'TEXT', NULL, 1, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Job', 'hours_per_week', 'Hours Per Week', NULL, 'TEXT', NULL, 1, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Job', 'work_location', 'Work Location', NULL, 'TEXT', NULL, 1, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Job', 'job_duties', 'Main Duties', NULL, 'TEXTAREA', NULL, 1, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Job', 'noc_teer', 'NOC/TEER Code (if known)', NULL, 'TEXT', NULL, 0, 0, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Stream', 'lmia_stream', 'LMIA Stream', NULL, 'SELECT', 'High-Wage|Low-Wage|PR Support|Global Talent Stream|Agricultural|Caregiver|Seasonal', 1, 1, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Recruitment', 'where_advertised', 'Where were job ads posted?', NULL, 'TEXTAREA', NULL, 1, 0, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Recruitment', 'ad_start_date', 'Job Ad Start Date', NULL, 'DATE', NULL, 1, 0, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Recruitment', 'ad_end_date', 'Job Ad End Date', NULL, 'DATE', NULL, 1, 0, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Recruitment', 'num_applicants', 'Number of Applicants Received', NULL, 'TEXT', NULL, 0, 0, 19, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Recruitment', 'num_interviewed', 'Number of Applicants Interviewed', NULL, 'TEXT', NULL, 0, 0, 20, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Candidate', 'candidate_name', 'Candidate Full Name', NULL, 'TEXT', NULL, 1, 0, 21, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Candidate', 'candidate_qualifications', 'Candidate Qualifications', NULL, 'TEXTAREA', NULL, 0, 0, 22, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Candidate', 'candidate_experience', 'Candidate Relevant Experience', NULL, 'TEXTAREA', NULL, 0, 0, 23, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Compliance', 'prior_lmia_history', 'Any prior LMIA applications?', NULL, 'SELECT', 'Yes|No', 1, 1, 24, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Compliance', 'prior_inspections', 'Any prior ESDC inspections?', NULL, 'SELECT', 'Yes|No', 0, 0, 25, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('LMIA', 'Compliance', 'recent_layoffs', 'Any recent layoffs for this position?', NULL, 'SELECT', 'Yes|No', 1, 1, 26, SYSTIMESTAMP);

-- ---- Citizenship ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Age', 'applicant_age_category', 'Is the applicant an adult or minor?', NULL, 'SELECT', 'Adult (18+)|Minor (under 18)', 1, 1, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'PR Date', 'pr_date', 'Date Became Permanent Resident', NULL, 'DATE', NULL, 1, 0, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Absences', 'absences_outside_canada', 'Have you travelled outside Canada during the eligibility period?', NULL, 'SELECT', 'Yes|No', 1, 1, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Absences', 'absence_details', 'If yes, list all trips (country, dates)', NULL, 'TEXTAREA', NULL, 0, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Tax Years', 'tax_years_filed', 'For which years have you filed Canadian taxes?', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Tax Years', 'missing_tax_years', 'Are there any missing tax filing years?', NULL, 'SELECT', 'Yes|No|Not Sure', 0, 1, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Language', 'language_proof_available', 'Is language proof available?', 'Required for applicants 18-54 at time of signing', 'SELECT', 'Yes|No|Exempt (age)', 1, 1, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Language', 'language_test_type', 'Language Test/Proof Type', NULL, 'TEXT', NULL, 0, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Prohibitions', 'criminality', 'Any criminal charges, convictions, or ongoing proceedings?', NULL, 'SELECT', 'Yes|No', 1, 1, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Prohibitions', 'removal_order', 'Any removal orders?', NULL, 'SELECT', 'Yes|No', 1, 1, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Prohibitions', 'probation', 'Currently on probation?', NULL, 'SELECT', 'Yes|No', 1, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Identity', 'available_ids', 'What identification documents are available?', NULL, 'TEXTAREA', NULL, 1, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Identity', 'name_consistency', 'Is your name consistent across all documents?', NULL, 'SELECT', 'Yes|No', 1, 1, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Past Applications', 'prior_citizenship_app', 'Any prior citizenship application?', NULL, 'SELECT', 'Yes|No', 1, 1, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('CITIZENSHIP', 'Past Applications', 'prior_app_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 15, SYSTIMESTAMP);

-- ---- PR Card / PRTD ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Application Reason', 'application_reason', 'Reason for Application', NULL, 'SELECT', 'Renewal|Replacement|First Card|PR Travel Document (PRTD)', 1, 1, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Card Expiry', 'pr_card_expiry', 'Current PR Card Expiry Date', NULL, 'DATE', NULL, 0, 0, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Travel', 'days_outside_canada', 'Approximate days outside Canada in last 5 years', NULL, 'TEXT', NULL, 1, 1, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Travel', 'travel_details', 'List all trips outside Canada (country, dates)', NULL, 'TEXTAREA', NULL, 0, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Lost/Damaged', 'card_lost_stolen_damaged', 'Was the card lost, stolen, or damaged?', NULL, 'SELECT', 'Lost|Stolen|Damaged|Not Applicable', 0, 1, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Lost/Damaged', 'police_report_number', 'Police Report/Incident Number (if applicable)', NULL, 'TEXT', NULL, 0, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Urgency', 'urgent_processing', 'Do you need urgent processing?', NULL, 'SELECT', 'Yes|No', 0, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Urgency', 'urgency_reason', 'If yes, explain the urgency', NULL, 'TEXTAREA', NULL, 0, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Name Changes', 'name_gender_changes', 'Any legal name or gender changes since PR was granted?', NULL, 'SELECT', 'Yes|No', 1, 1, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Name Changes', 'change_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Documents', 'copr_available', 'Is your COPR (Confirmation of PR) available?', NULL, 'SELECT', 'Yes|No', 1, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Documents', 'record_of_landing_available', 'Is your Record of Landing available?', NULL, 'SELECT', 'Yes|No|Not Sure', 0, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PR_CARD_PRTD', 'Documents', 'passport_available', 'Is your current passport available?', NULL, 'SELECT', 'Yes|No', 1, 0, 13, SYSTIMESTAMP);

-- ---- PGWP ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Graduation', 'completion_date', 'Program Completion Date', NULL, 'DATE', NULL, 1, 0, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Graduation', 'transcript_available', 'Is your final transcript available?', NULL, 'SELECT', 'Yes|No|Pending', 1, 1, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Graduation', 'completion_letter_available', 'Is your completion/graduation letter available?', NULL, 'SELECT', 'Yes|No|Pending', 1, 1, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'School', 'dli_name', 'Designated Learning Institution Name', NULL, 'TEXT', NULL, 1, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'School', 'program_length', 'Program Length (months)', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'School', 'program_level', 'Program Level', NULL, 'SELECT', 'Certificate|Diploma|Associate|Bachelor|Master|PhD', 1, 0, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Status', 'study_permit_expiry', 'Study Permit Expiry Date', NULL, 'DATE', NULL, 1, 1, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Status', 'current_status', 'Current Immigration Status', NULL, 'SELECT', 'Valid Study Permit|Implied Status|Visitor Record|Other', 1, 1, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Language', 'language_test_taken', 'Have you taken a language test?', 'Required for some PGWP applications', 'SELECT', 'Yes|No|Exempt', 1, 1, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Language', 'language_test_date', 'Language Test Date', NULL, 'DATE', NULL, 0, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Language', 'language_scores', 'Language Test Scores', NULL, 'TEXT', NULL, 0, 0, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Study History', 'full_time_all_terms', 'Were all terms full-time?', NULL, 'SELECT', 'Yes|No', 1, 1, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Study History', 'authorized_leaves', 'Any authorized leaves or part-time terms?', NULL, 'SELECT', 'Yes|No', 0, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Study History', 'program_transfers', 'Any program or school transfers?', NULL, 'SELECT', 'Yes|No', 0, 1, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('PGWP', 'Passport', 'passport_expiry', 'Passport Expiry Date', NULL, 'DATE', NULL, 1, 1, 15, SYSTIMESTAMP);

-- ---- Super Visa ----
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Applicant', 'applicant_name', 'Applicant Full Name', NULL, 'TEXT', NULL, 1, 0, 1, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Applicant', 'applicant_relationship', 'Relationship to Host', NULL, 'SELECT', 'Parent|Grandparent', 1, 1, 2, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Applicant', 'applicant_passport_expiry', 'Passport Expiry Date', NULL, 'DATE', NULL, 1, 0, 3, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Applicant', 'applicant_country_residence', 'Country of Residence', NULL, 'TEXT', NULL, 1, 0, 4, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Host', 'host_name', 'Host Full Name', NULL, 'TEXT', NULL, 1, 0, 5, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Host', 'host_status', 'Host Immigration Status', NULL, 'SELECT', 'Canadian Citizen|Permanent Resident', 1, 1, 6, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Host', 'host_relationship_proof', 'What proof of relationship is available?', NULL, 'TEXT', NULL, 1, 0, 7, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Host', 'household_size', 'Total Household Size (including applicant)', NULL, 'TEXT', NULL, 1, 0, 8, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Finances', 'host_income_docs_available', 'Are host income documents available (NOA, employment letter)?', NULL, 'SELECT', 'Yes|No|Partial', 1, 1, 9, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Finances', 'host_support_evidence', 'What financial support evidence is available?', NULL, 'TEXTAREA', NULL, 0, 0, 10, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Insurance', 'has_medical_insurance', 'Has qualifying medical insurance been obtained?', NULL, 'SELECT', 'Yes|No|In Progress', 1, 1, 11, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Insurance', 'insurance_policy_details', 'Insurance Provider and Policy Number', NULL, 'TEXT', NULL, 0, 0, 12, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Insurance', 'insurance_coverage_amount', 'Coverage Amount', NULL, 'TEXT', NULL, 0, 0, 13, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Purpose/Duration', 'planned_arrival', 'Planned Arrival Date', NULL, 'DATE', NULL, 0, 0, 14, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'Purpose/Duration', 'planned_stay_location', 'Where in Canada will applicant stay?', NULL, 'TEXT', NULL, 0, 0, 15, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'History', 'prior_refusals', 'Any prior visa refusals?', NULL, 'SELECT', 'Yes|No', 1, 1, 16, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'History', 'refusal_details', 'If yes, provide details', NULL, 'TEXTAREA', NULL, 0, 0, 17, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'History', 'previous_travel_history', 'Previous travel history', NULL, 'TEXTAREA', NULL, 0, 0, 18, SYSTIMESTAMP);
INSERT INTO intake_question_templates (service_type, section_name, question_key, question_label, help_text, input_type, options, required, is_trigger_question, sort_order, created_at) VALUES
('SUPER_VISA', 'History', 'medical_concerns', 'Any medical concerns?', NULL, 'SELECT', 'Yes|No', 0, 0, 19, SYSTIMESTAMP);

-- ======================== CONDITIONAL RULES ========================

-- Study Permit rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'is_quebec', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include CAQ requirement when school is in Quebec', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'school_province', 'QC', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include CAQ requirement when province is Quebec', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'has_sponsor', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include sponsor financial documents when sponsor is involved', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'spouse_accompanying', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include spouse/family documents when family is accompanying', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'children_accompanying', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include dependent children documents when children are accompanying', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'prior_refusals', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include prior refusal explanation letter when refusals exist', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'funding_source', 'Scholarship', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include scholarship award letter when scholarship-funded', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('STUDY_PERMIT', 'has_gic', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include GIC confirmation when GIC is available', 1, SYSTIMESTAMP);

-- Visitor Visa rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('VISITOR_VISA', 'has_canadian_host', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include invitation letter and host documents when host exists', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('VISITOR_VISA', 'visit_purpose', 'Business', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include business invitation/conference documents for business visits', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('VISITOR_VISA', 'prior_visa_refusals', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include refusal explanation when prior refusals exist', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('VISITOR_VISA', 'funding_type', 'Host-funded', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include host financial proof when host is funding trip', 1, SYSTIMESTAMP);

-- Spousal Sponsorship rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'relationship_type', 'Common-Law', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include statutory declaration of common-law union', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'relationship_type', 'Conjugal', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include conjugal relationship evidence and barrier explanation', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'has_children', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include children''s identity/custody documents', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'sponsor_previously_sponsored', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include proof that 3-year undertaking from previous sponsorship has ended', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'sponsor_in_quebec', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include Quebec CSQ/undertaking forms for Quebec sponsorship', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SPOUSAL_SPONSORSHIP', 'prior_refusals_removals', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include rehabilitation/explanation for prior refusals or inadmissibility', 1, SYSTIMESTAMP);

-- Express Entry rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('EXPRESS_ENTRY', 'education_canadian_or_foreign', 'Foreign', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include ECA report for foreign education credentials', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('EXPRESS_ENTRY', 'education_canadian_or_foreign', 'Both', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include ECA report when applicant has both Canadian and foreign education', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('EXPRESS_ENTRY', 'has_dependents', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include dependent children documents', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('EXPRESS_ENTRY', 'spouse_accompanying', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include spouse documents and language test', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('EXPRESS_ENTRY', 'prior_refusals', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include prior refusal explanation letter', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('EXPRESS_ENTRY', 'program_path', 'PNP Express Entry', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include PNP nomination certificate', 1, SYSTIMESTAMP);

-- Work Permit rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('WORK_PERMIT', 'lmia_status', 'LMIA Required (has LMIA)', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include LMIA confirmation letter', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('WORK_PERMIT', 'wp_type', 'Open Work Permit', 'EQUALS', 'EXCLUDE', NULL, NULL, 'Exclude employer-specific documents for open work permits', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('WORK_PERMIT', 'spouse_accompanying', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include spouse/family documents', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('WORK_PERMIT', 'prior_refusals', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include prior refusal explanation letter', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('WORK_PERMIT', 'application_location', 'Inside Canada', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include current status documents for in-Canada applications', 1, SYSTIMESTAMP);

-- LMIA rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('LMIA', 'lmia_stream', 'Global Talent Stream', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include ESDC referral partner letter for Global Talent Stream', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('LMIA', 'lmia_stream', 'Agricultural', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include seasonal agricultural worker program documents', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('LMIA', 'lmia_stream', 'Caregiver', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include caregiver-specific employer requirements', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('LMIA', 'recent_layoffs', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include layoff justification when recent layoffs occurred', 1, SYSTIMESTAMP);

-- Citizenship rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('CITIZENSHIP', 'applicant_age_category', 'Minor (under 18)', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include parent/guardian documents for minor applicants', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('CITIZENSHIP', 'applicant_age_category', 'Minor (under 18)', 'EQUALS', 'EXCLUDE', NULL, NULL, 'Exclude language test for minor applicants', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('CITIZENSHIP', 'absences_outside_canada', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include absence log with proof of travel dates', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('CITIZENSHIP', 'criminality', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include court records and rehabilitation documents', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('CITIZENSHIP', 'name_consistency', 'No', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include legal name change certificates', 1, SYSTIMESTAMP);

-- PR Card rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('PR_CARD_PRTD', 'application_reason', 'PR Travel Document (PRTD)', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include PRTD-specific documents and proof of ties to Canada', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('PR_CARD_PRTD', 'card_lost_stolen_damaged', 'Stolen', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include police report for stolen card', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('PR_CARD_PRTD', 'name_gender_changes', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include legal name/gender change documents', 1, SYSTIMESTAMP);

-- PGWP rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('PGWP', 'transcript_available', 'Pending', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include interim transcript and expected date of final transcript', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('PGWP', 'program_transfers', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include transfer letters from all institutions attended', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('PGWP', 'language_test_taken', 'No', 'EQUALS', 'INCLUDE', NULL, NULL, 'Flag: language test may be required depending on graduation date', 1, SYSTIMESTAMP);

-- Super Visa rules
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SUPER_VISA', 'has_medical_insurance', 'No', 'EQUALS', 'INCLUDE', NULL, NULL, 'Flag: medical insurance is mandatory for Super Visa', 1, SYSTIMESTAMP);
INSERT INTO conditional_rules (service_type, trigger_question_key, trigger_value, operator, action_type, target_checklist_template_id, target_question_key, description, active, created_at) VALUES
('SUPER_VISA', 'prior_refusals', 'Yes', 'EQUALS', 'INCLUDE', NULL, NULL, 'Include prior refusal explanation letter', 1, SYSTIMESTAMP);
