package com.immiauto.service;

import com.immiauto.dto.TravelHistoryDto;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.TravelHistoryEntry;
import com.immiauto.mapper.TravelHistoryMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.TravelHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TravelHistoryService {

    private final TravelHistoryRepository travelHistoryRepository;
    private final CaseRepository caseRepository;
    private final TravelHistoryMapper travelHistoryMapper;

    @Transactional
    public TravelHistoryDto addEntry(Long caseId, TravelHistoryDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        int days = dto.getDaysAbsent();
        if (days == 0 && dto.getEntryDate() != null && dto.getExitDate() != null) {
            days = (int) ChronoUnit.DAYS.between(dto.getEntryDate(), dto.getExitDate());
        }
        TravelHistoryEntry entry = TravelHistoryEntry.builder()
                .immigrationCase(imCase)
                .country(dto.getCountry())
                .entryDate(dto.getEntryDate())
                .exitDate(dto.getExitDate())
                .purpose(dto.getPurpose())
                .daysAbsent(days)
                .notes(dto.getNotes())
                .sortOrder(dto.getSortOrder())
                .build();
        return travelHistoryMapper.toDto(travelHistoryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<TravelHistoryDto> getEntries(Long caseId) {
        return travelHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(travelHistoryMapper::toDto).toList();
    }

    @Transactional
    public TravelHistoryDto updateEntry(Long caseId, Long id, TravelHistoryDto dto) {
        TravelHistoryEntry entry = travelHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        if (dto.getCountry() != null) entry.setCountry(dto.getCountry());
        if (dto.getEntryDate() != null) entry.setEntryDate(dto.getEntryDate());
        if (dto.getExitDate() != null) entry.setExitDate(dto.getExitDate());
        if (dto.getPurpose() != null) entry.setPurpose(dto.getPurpose());
        if (dto.getNotes() != null) entry.setNotes(dto.getNotes());
        if (dto.getEntryDate() != null && dto.getExitDate() != null) {
            entry.setDaysAbsent((int) ChronoUnit.DAYS.between(dto.getEntryDate(), dto.getExitDate()));
        }
        return travelHistoryMapper.toDto(travelHistoryRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(Long caseId, Long id) {
        TravelHistoryEntry entry = travelHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        travelHistoryRepository.delete(entry);
    }

    private static final int CITIZENSHIP_REQUIRED_DAYS = 1095;
    private static final int MIN_PR_DAYS = 730;
    private static final int PRE_PR_CAP_DAYS = 365;

    @Transactional(readOnly = true)
    public Map<String, Object> getPhysicalPresenceSummary(Long caseId) {
        return getPhysicalPresenceSummary(caseId, null, null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPhysicalPresenceSummary(Long caseId,
                                                           LocalDate applicationDate,
                                                           LocalDate prStartDate) {
        List<TravelHistoryEntry> entries = travelHistoryRepository
                .findByImmigrationCaseIdOrderBySortOrder(caseId);

        LocalDate effectiveAppDate = applicationDate != null ? applicationDate : LocalDate.now();
        LocalDate fiveYearsAgo = effectiveAppDate.minusYears(5);

        int totalDaysAbsent = 0;
        int prDaysAbsent = 0;

        for (TravelHistoryEntry entry : entries) {
            LocalDate start = entry.getEntryDate();
            LocalDate end = entry.getExitDate() != null ? entry.getExitDate() : effectiveAppDate;

            if (end.isBefore(fiveYearsAgo) || start.isAfter(effectiveAppDate)) {
                continue;
            }

            LocalDate clampedStart = start.isBefore(fiveYearsAgo) ? fiveYearsAgo : start;
            LocalDate clampedEnd = end.isAfter(effectiveAppDate) ? effectiveAppDate : end;
            int absentDays = (int) ChronoUnit.DAYS.between(clampedStart, clampedEnd);
            totalDaysAbsent += absentDays;

            if (prStartDate != null && clampedEnd.isAfter(prStartDate)) {
                LocalDate prStart = clampedStart.isAfter(prStartDate) ? clampedStart : prStartDate;
                prDaysAbsent += (int) ChronoUnit.DAYS.between(prStart, clampedEnd);
            }
        }

        int totalEligibleWindow = (int) ChronoUnit.DAYS.between(fiveYearsAgo, effectiveAppDate);
        boolean hasPrDate = prStartDate != null && !prStartDate.isAfter(effectiveAppDate);

        int daysInCanadaAsPR;
        int prePrCreditDays = 0;

        if (hasPrDate) {
            LocalDate prWindowStart = prStartDate.isBefore(fiveYearsAgo) ? fiveYearsAgo : prStartDate;
            int totalPrWindow = (int) ChronoUnit.DAYS.between(prWindowStart, effectiveAppDate);
            daysInCanadaAsPR = totalPrWindow - prDaysAbsent;

            if (prStartDate.isAfter(fiveYearsAgo)) {
                int prePrWindow = (int) ChronoUnit.DAYS.between(fiveYearsAgo, prStartDate);
                int prePrAbsent = totalDaysAbsent - prDaysAbsent;
                int prePrPresent = prePrWindow - Math.max(0, prePrAbsent);
                prePrCreditDays = Math.min(Math.max(0, prePrPresent / 2), PRE_PR_CAP_DAYS);
            }
        } else {
            daysInCanadaAsPR = totalEligibleWindow - totalDaysAbsent;
        }

        int effectiveDays = daysInCanadaAsPR + prePrCreditDays;
        boolean meetsPrMinimum = !hasPrDate || daysInCanadaAsPR >= MIN_PR_DAYS;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalDaysAbsent", totalDaysAbsent);
        summary.put("daysInCanadaAsPR", Math.max(0, daysInCanadaAsPR));
        summary.put("prePrCreditDays", prePrCreditDays);
        summary.put("effectiveDaysInCanada", Math.max(0, effectiveDays));
        summary.put("requiredDays", CITIZENSHIP_REQUIRED_DAYS);
        summary.put("meetsPhysicalPresence", effectiveDays >= CITIZENSHIP_REQUIRED_DAYS && meetsPrMinimum);
        summary.put("meetsPrMinimum", meetsPrMinimum);
        summary.put("daysShort", Math.max(0, CITIZENSHIP_REQUIRED_DAYS - effectiveDays));
        summary.put("totalTrips", entries.size());
        summary.put("applicationDate", effectiveAppDate.toString());
        summary.put("prStartDate", prStartDate != null ? prStartDate.toString() : null);
        summary.put("disclaimer", "PRELIMINARY ESTIMATE — requires consultant review. Does not constitute a citizenship eligibility determination.");
        return summary;
    }
}
