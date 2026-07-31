package com.immiauto.service;

import java.util.UUID;

import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.TravelHistoryEntry;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.TravelHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoliceCertificateService {

    private static final int CONTINUOUS_DAYS_THRESHOLD = 183;
    private static final int LOOKBACK_YEARS = 10;
    private static final int MINIMUM_AGE = 18;

    private final TravelHistoryRepository travelHistoryRepository;
    private final CaseRepository caseRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> determineRequiredCertificates(UUID caseId, LocalDate dateOfBirth) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        List<TravelHistoryEntry> entries = travelHistoryRepository
                .findByImmigrationCaseIdOrderBySortOrder(caseId);

        LocalDate today = LocalDate.now();
        LocalDate windowStart = today.minusYears(LOOKBACK_YEARS);
        LocalDate age18Date = dateOfBirth != null ? dateOfBirth.plusYears(MINIMUM_AGE) : null;

        if (age18Date != null && age18Date.isAfter(windowStart)) {
            windowStart = age18Date;
        }

        Map<String, List<long[]>> countryIntervals = new LinkedHashMap<>();

        for (TravelHistoryEntry entry : entries) {
            if ("Canada".equalsIgnoreCase(entry.getCountry())) {
                continue;
            }

            LocalDate start = entry.getEntryDate();
            LocalDate end = entry.getExitDate() != null ? entry.getExitDate() : today;

            if (end.isBefore(windowStart) || start.isAfter(today)) {
                continue;
            }

            LocalDate clampedStart = start.isBefore(windowStart) ? windowStart : start;
            LocalDate clampedEnd = end.isAfter(today) ? today : end;

            long startEpoch = clampedStart.toEpochDay();
            long endEpoch = clampedEnd.toEpochDay();

            countryIntervals
                    .computeIfAbsent(entry.getCountry(), k -> new ArrayList<>())
                    .add(new long[]{startEpoch, endEpoch});
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (Map.Entry<String, List<long[]>> countryEntry : countryIntervals.entrySet()) {
            String country = countryEntry.getKey();
            List<long[]> intervals = countryEntry.getValue();

            intervals.sort(Comparator.comparingLong(a -> a[0]));

            long maxContinuous = 0;
            long mergedStart = intervals.get(0)[0];
            long mergedEnd = intervals.get(0)[1];

            for (int i = 1; i < intervals.size(); i++) {
                long[] interval = intervals.get(i);
                if (interval[0] <= mergedEnd + 1) {
                    mergedEnd = Math.max(mergedEnd, interval[1]);
                } else {
                    maxContinuous = Math.max(maxContinuous, mergedEnd - mergedStart);
                    mergedStart = interval[0];
                    mergedEnd = interval[1];
                }
            }
            maxContinuous = Math.max(maxContinuous, mergedEnd - mergedStart);

            Map<String, Object> cert = new LinkedHashMap<>();
            cert.put("country", country);
            cert.put("longestContinuousStayDays", maxContinuous);
            cert.put("required", maxContinuous >= CONTINUOUS_DAYS_THRESHOLD);
            if (maxContinuous >= CONTINUOUS_DAYS_THRESHOLD) {
                cert.put("reason", "Continuous stay of 6+ months (" + maxContinuous + " days) within the last 10 years after age 18");
            } else {
                cert.put("reason", "Longest continuous stay was " + maxContinuous + " days (below 183-day threshold)");
            }
            results.add(cert);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("evaluationWindowStart", windowStart.toString());
        response.put("evaluationDate", today.toString());
        response.put("dateOfBirth", dateOfBirth != null ? dateOfBirth.toString() : null);
        response.put("countries", results);
        response.put("requiredCertificates", results.stream()
                .filter(r -> (boolean) r.get("required"))
                .collect(Collectors.toList()));
        response.put("disclaimer", "PRELIMINARY REVIEW — Police certificate requirements must be confirmed by a licensed consultant based on the specific immigration program.");
        return response;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> determineRequiredCertificates(UUID caseId) {
        Map<String, Object> full = determineRequiredCertificates(caseId, null);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> required = (List<Map<String, Object>>) full.get("requiredCertificates");
        return required;
    }
}
