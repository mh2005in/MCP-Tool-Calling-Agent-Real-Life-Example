package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A normalized snapshot of canonical applicant/case data for one case,
 * with provenance, conflicts, and the list of missing field keys.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CanonicalDataSnapshotDto {

    private Long caseId;
    private String caseNumber;
    private String generatedAt; // ISO-8601 timestamp

    @Builder.Default
    private List<CanonicalValueDto> values = new ArrayList<>();

    @Builder.Default
    private List<CanonicalDataConflictDto> conflicts = new ArrayList<>();

    @Builder.Default
    private List<String> missingFieldKeys = new ArrayList<>();
}
