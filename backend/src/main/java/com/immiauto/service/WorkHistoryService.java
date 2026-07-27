package com.immiauto.service;

import com.immiauto.dto.WorkHistoryDto;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.WorkHistoryEntry;
import com.immiauto.mapper.WorkHistoryMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.WorkHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkHistoryService {

    private final WorkHistoryRepository workHistoryRepository;
    private final CaseRepository caseRepository;
    private final WorkHistoryMapper workHistoryMapper;

    @Transactional
    public WorkHistoryDto addEntry(Long caseId, WorkHistoryDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        WorkHistoryEntry entry = WorkHistoryEntry.builder()
                .immigrationCase(imCase)
                .employerName(dto.getEmployerName())
                .jobTitle(dto.getJobTitle())
                .nocTeerCode(dto.getNocTeerCode())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .currentJob(dto.isCurrentJob())
                .hoursPerWeek(dto.getHoursPerWeek())
                .employmentType(dto.getEmploymentType())
                .duties(dto.getDuties())
                .country(dto.getCountry())
                .city(dto.getCity())
                .referenceLetterReceived(dto.isReferenceLetterReceived())
                .referenceDatesMatch(dto.isReferenceDatesMatch())
                .referenceDutiesDescribed(dto.isReferenceDutiesDescribed())
                .sortOrder(dto.getSortOrder())
                .build();
        return workHistoryMapper.toDto(workHistoryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<WorkHistoryDto> getEntries(Long caseId) {
        return workHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(workHistoryMapper::toDto).toList();
    }

    @Transactional
    public WorkHistoryDto updateEntry(Long caseId, Long id, WorkHistoryDto dto) {
        WorkHistoryEntry entry = workHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        if (dto.getEmployerName() != null) entry.setEmployerName(dto.getEmployerName());
        if (dto.getJobTitle() != null) entry.setJobTitle(dto.getJobTitle());
        if (dto.getNocTeerCode() != null) entry.setNocTeerCode(dto.getNocTeerCode());
        if (dto.getStartDate() != null) entry.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entry.setEndDate(dto.getEndDate());
        entry.setCurrentJob(dto.isCurrentJob());
        entry.setHoursPerWeek(dto.getHoursPerWeek());
        if (dto.getEmploymentType() != null) entry.setEmploymentType(dto.getEmploymentType());
        if (dto.getDuties() != null) entry.setDuties(dto.getDuties());
        if (dto.getCountry() != null) entry.setCountry(dto.getCountry());
        if (dto.getCity() != null) entry.setCity(dto.getCity());
        entry.setReferenceLetterReceived(dto.isReferenceLetterReceived());
        entry.setReferenceDatesMatch(dto.isReferenceDatesMatch());
        entry.setReferenceDutiesDescribed(dto.isReferenceDutiesDescribed());
        return workHistoryMapper.toDto(workHistoryRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(Long caseId, Long id) {
        WorkHistoryEntry entry = workHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        workHistoryRepository.delete(entry);
    }
}
