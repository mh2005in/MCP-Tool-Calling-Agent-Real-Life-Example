package com.immiauto.service.forms;

import com.immiauto.dto.forms.CanonicalDataConflictDto;
import com.immiauto.dto.forms.CanonicalDataSnapshotDto;
import com.immiauto.dto.forms.CanonicalValueDto;
import com.immiauto.dto.forms.CanonicalValueSourceDto;
import com.immiauto.entity.Client;
import com.immiauto.entity.Consultant;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.IntakeResponse;
import com.immiauto.entity.forms.CanonicalDataField;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.IntakeResponseRepository;
import com.immiauto.repository.TravelHistoryRepository;
import com.immiauto.repository.WorkHistoryRepository;
import com.immiauto.repository.forms.CanonicalDataFieldRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a normalized canonical data snapshot for a case from structured
 * entities, falling back to intake responses, while preserving provenance
 * and detecting conflicts. (Section 4.1 - Phase B)
 */
@Service
@RequiredArgsConstructor
public class CanonicalApplicantDataService {

    private static final String SRC_CLIENT = "CLIENT";
    private static final String SRC_INTAKE = "INTAKE";
    private static final String SRC_CASE = "CASE";
    private static final String SRC_CONSULTANT = "CONSULTANT";
    private static final String SRC_TRAVEL = "TRAVEL_HISTORY";
    private static final String SRC_WORK = "WORK_HISTORY";

    private final CaseRepository caseRepository;
    private final IntakeResponseRepository intakeResponseRepository;
    private final TravelHistoryRepository travelHistoryRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final CanonicalDataFieldRepository canonicalDataFieldRepository;

    @Transactional(readOnly = true)
    public CanonicalDataSnapshotDto buildSnapshot(Long caseId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        Client client = imCase.getClient();
        Consultant consultant = imCase.getConsultant();

        // Build a questionKey -> response map (first occurrence wins).
        Map<String, IntakeResponse> intake = new LinkedHashMap<>();
        for (IntakeResponse r : intakeResponseRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)) {
            intake.putIfAbsent(r.getQuestionKey(), r);
        }

        int travelCount = travelHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId).size();
        int workCount = workHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId).size();

        List<CanonicalValueDto> values = new ArrayList<>();
        List<CanonicalDataConflictDto> conflicts = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (CanonicalDataField field : canonicalDataFieldRepository.findByActiveTrue()) {
            List<CanonicalValueSourceDto> candidates =
                    resolveCandidates(field.getFieldKey(), imCase, client, consultant, intake, travelCount, workCount);

            CanonicalValueDto value = buildValue(field, candidates);
            values.add(value);

            if (!value.isPresent()) {
                missing.add(field.getFieldKey());
            }
            if (value.isConflict()) {
                conflicts.add(CanonicalDataConflictDto.builder()
                        .fieldKey(field.getFieldKey())
                        .displayName(field.getDisplayName())
                        .message("Sources disagree on this value. Consultant review required.")
                        .candidateValues(distinctValues(candidates))
                        .sources(candidates)
                        .build());
            }
        }

        return CanonicalDataSnapshotDto.builder()
                .caseId(caseId)
                .caseNumber(imCase.getCaseNumber())
                .generatedAt(LocalDateTime.now().toString())
                .values(values)
                .conflicts(conflicts)
                .missingFieldKeys(missing)
                .build();
    }

    private CanonicalValueDto buildValue(CanonicalDataField field, List<CanonicalValueSourceDto> candidates) {
        boolean present = !candidates.isEmpty();
        String selectedValue = null;
        if (present) {
            // candidates already ordered by priority; first one is selected.
            candidates.get(0).setSelected(true);
            selectedValue = normalize(field.getDataType(), candidates.get(0).getRawValue());
        }

        List<String> distinct = distinctValues(candidates);
        boolean conflict = distinct.size() > 1;

        boolean intakeFlagged = present
                && SRC_INTAKE.equals(candidates.get(0).getSourceType())
                && candidates.get(0).getSourceLabel() != null
                && candidates.get(0).getSourceLabel().contains("[flagged]");

        String note;
        if (!present) {
            note = "No value found in client record or intake responses.";
        } else if (conflict) {
            note = "Conflicting values across " + distinct.size() + " sources.";
        } else {
            note = "Resolved from " + candidates.get(0).getSourceLabel() + ".";
        }

        return CanonicalValueDto.builder()
                .fieldKey(field.getFieldKey())
                .displayName(field.getDisplayName())
                .category(field.getCategory())
                .dataType(field.getDataType())
                .value(selectedValue)
                .present(present)
                .sensitive(field.isSensitive())
                .reviewRequired(conflict || intakeFlagged)
                .conflict(conflict)
                .note(note)
                .sources(candidates)
                .build();
    }

    private List<CanonicalValueSourceDto> resolveCandidates(String fieldKey,
                                                            ImmigrationCase imCase,
                                                            Client client,
                                                            Consultant consultant,
                                                            Map<String, IntakeResponse> intake,
                                                            int travelCount,
                                                            int workCount) {
        List<CanonicalValueSourceDto> list = new ArrayList<>();
        switch (fieldKey) {
            case "primaryApplicant.fullName" -> {
                addClient(list, client, "Full Name", client == null ? null : client.getFullName(), 1);
                addIntake(list, intake, "full_name", 2);
            }
            case "primaryApplicant.dateOfBirth" -> {
                addClient(list, client, "Date of Birth", client == null ? null : asString(client.getDateOfBirth()), 1);
                addIntake(list, intake, "dob", 2);
            }
            case "primaryApplicant.passport.number" -> {
                addClient(list, client, "Passport Number", client == null ? null : client.getPassportNumber(), 1);
                addIntake(list, intake, "passport_number", 2);
            }
            case "primaryApplicant.passport.expiryDate" ->
                    addIntake(list, intake, "passport_expiry", 1);
            case "primaryApplicant.currentAddress.country" -> {
                addClient(list, client, "Current Location", client == null ? null : client.getCurrentLocation(), 1);
                addIntake(list, intake, "applicant_country", 2);
                addIntake(list, intake, "applying_from_country", 3);
            }
            case "primaryApplicant.email" ->
                    addClient(list, client, "Email", client == null ? null : client.getEmail(), 1);
            case "primaryApplicant.phone" ->
                    addClient(list, client, "Phone", client == null ? null : client.getPhone(), 1);
            case "case.serviceType" -> {
                if (imCase.getServiceType() != null) {
                    add(list, SRC_CASE, imCase.getId(), "Case service type",
                            imCase.getServiceType().name(), 1);
                }
            }
            case "representative.fullName" -> {
                if (consultant != null) {
                    add(list, SRC_CONSULTANT, consultant.getId(), "Consultant profile",
                            consultant.getFullName(), 1);
                }
            }
            case "representative.licenseNumber" -> {
                if (consultant != null) {
                    add(list, SRC_CONSULTANT, consultant.getId(), "Consultant profile",
                            consultant.getLicenseNumber(), 1);
                }
            }
            case "travelHistory.entries" -> {
                if (travelCount > 0) {
                    add(list, SRC_TRAVEL, imCase.getId(), "Travel history",
                            travelCount + " entr" + (travelCount == 1 ? "y" : "ies"), 1);
                }
            }
            case "workHistory.entries" -> {
                if (workCount > 0) {
                    add(list, SRC_WORK, imCase.getId(), "Work history",
                            workCount + " entr" + (workCount == 1 ? "y" : "ies"), 1);
                }
            }
            default -> { /* unknown canonical key: no resolver yet */ }
        }
        return list;
    }

    private void addClient(List<CanonicalValueSourceDto> list, Client client, String fieldLabel, String value, int priority) {
        if (client == null) return;
        add(list, SRC_CLIENT, client.getId(), "Client record (" + fieldLabel + ")", value, priority);
    }

    private void addIntake(List<CanonicalValueSourceDto> list, Map<String, IntakeResponse> intake, String questionKey, int priority) {
        IntakeResponse r = intake.get(questionKey);
        if (r == null) return;
        String label = "Intake: " + r.getQuestionLabel() + (r.isFlaggedForReview() ? " [flagged]" : "");
        add(list, SRC_INTAKE, r.getId(), label, r.getAnswer(), priority);
    }

    private void add(List<CanonicalValueSourceDto> list, String sourceType, Long sourceId, String label, String value, int priority) {
        if (!StringUtils.hasText(value)) return;
        list.add(CanonicalValueSourceDto.builder()
                .sourceType(sourceType)
                .sourceId(sourceId)
                .sourceLabel(label)
                .rawValue(value.trim())
                .selected(false)
                .priority(priority)
                .build());
    }

    private List<String> distinctValues(List<CanonicalValueSourceDto> candidates) {
        List<String> distinct = new ArrayList<>();
        for (CanonicalValueSourceDto c : candidates) {
            String v = c.getRawValue() == null ? "" : c.getRawValue().trim();
            boolean found = distinct.stream().anyMatch(d -> d.equalsIgnoreCase(v));
            if (!found && StringUtils.hasText(v)) {
                distinct.add(v);
            }
        }
        return distinct;
    }

    private String asString(LocalDate date) {
        return date == null ? null : date.toString(); // ISO yyyy-MM-dd
    }

    /** Normalize dates to ISO yyyy-MM-dd where possible; pass through otherwise. */
    private String normalize(String dataType, String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if ("date".equalsIgnoreCase(dataType)) {
            try {
                return LocalDate.parse(trimmed).toString();
            } catch (DateTimeParseException ignored) {
                // leave as-is; validation engine (Phase E) will flag bad dates
            }
        }
        return trimmed;
    }
}
