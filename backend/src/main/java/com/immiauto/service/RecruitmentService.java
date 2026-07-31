package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.CandidateComparisonDto;
import com.immiauto.dto.RecruitmentEvidenceDto;
import com.immiauto.entity.*;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentEvidenceRepository evidenceRepository;
    private final CandidateComparisonRepository candidateRepository;
    private final CaseRepository caseRepository;

    // --- Recruitment Evidence ---

    @Transactional
    public RecruitmentEvidenceDto addEvidence(UUID caseId, RecruitmentEvidenceDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        RecruitmentEvidence entry = RecruitmentEvidence.builder()
                .immigrationCase(imCase)
                .evidenceType(dto.getEvidenceType())
                .platform(dto.getPlatform())
                .postingDate(dto.getPostingDate())
                .expiryDate(dto.getExpiryDate())
                .daysPosted(dto.getDaysPosted())
                .applicantsReceived(dto.getApplicantsReceived())
                .interviewsConducted(dto.getInterviewsConducted())
                .nonHireReasons(dto.getNonHireReasons())
                .screenshotAttached(dto.isScreenshotAttached())
                .notes(dto.getNotes())
                .sortOrder(dto.getSortOrder())
                .build();
        return toEvidenceDto(evidenceRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<RecruitmentEvidenceDto> getEvidence(UUID caseId) {
        return evidenceRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(this::toEvidenceDto).toList();
    }

    @Transactional
    public void deleteEvidence(UUID caseId, UUID id) {
        RecruitmentEvidence entry = evidenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        evidenceRepository.delete(entry);
    }

    // --- Candidate Comparison ---

    @Transactional
    public CandidateComparisonDto addCandidate(UUID caseId, CandidateComparisonDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        CandidateComparison entry = CandidateComparison.builder()
                .immigrationCase(imCase)
                .candidateName(dto.getCandidateName())
                .candidateType(dto.getCandidateType())
                .qualifications(dto.getQualifications())
                .yearsExperience(dto.getYearsExperience())
                .educationLevel(dto.getEducationLevel())
                .languageSkills(dto.getLanguageSkills())
                .interviewed(dto.isInterviewed())
                .interviewNotes(dto.getInterviewNotes())
                .outcome(dto.getOutcome())
                .nonHireReason(dto.getNonHireReason())
                .sortOrder(dto.getSortOrder())
                .build();
        return toCandidateDto(candidateRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<CandidateComparisonDto> getCandidates(UUID caseId) {
        return candidateRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(this::toCandidateDto).toList();
    }

    @Transactional
    public void deleteCandidate(UUID caseId, UUID id) {
        CandidateComparison entry = candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        candidateRepository.delete(entry);
    }

    private RecruitmentEvidenceDto toEvidenceDto(RecruitmentEvidence e) {
        return RecruitmentEvidenceDto.builder()
                .id(e.getId()).caseId(e.getImmigrationCase().getId())
                .evidenceType(e.getEvidenceType()).platform(e.getPlatform())
                .postingDate(e.getPostingDate()).expiryDate(e.getExpiryDate())
                .daysPosted(e.getDaysPosted()).applicantsReceived(e.getApplicantsReceived())
                .interviewsConducted(e.getInterviewsConducted())
                .nonHireReasons(e.getNonHireReasons())
                .screenshotAttached(e.isScreenshotAttached())
                .notes(e.getNotes()).sortOrder(e.getSortOrder())
                .build();
    }

    private CandidateComparisonDto toCandidateDto(CandidateComparison c) {
        return CandidateComparisonDto.builder()
                .id(c.getId()).caseId(c.getImmigrationCase().getId())
                .candidateName(c.getCandidateName()).candidateType(c.getCandidateType())
                .qualifications(c.getQualifications()).yearsExperience(c.getYearsExperience())
                .educationLevel(c.getEducationLevel()).languageSkills(c.getLanguageSkills())
                .interviewed(c.isInterviewed()).interviewNotes(c.getInterviewNotes())
                .outcome(c.getOutcome()).nonHireReason(c.getNonHireReason())
                .sortOrder(c.getSortOrder())
                .build();
    }
}
