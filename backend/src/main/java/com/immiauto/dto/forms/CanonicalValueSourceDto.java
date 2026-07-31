package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * One candidate source for a canonical value, preserving provenance.
 * All candidate sources are recorded, not only the selected one.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CanonicalValueSourceDto {

    private String sourceType;  // CLIENT, INTAKE, CASE, CONSULTANT, WORK_HISTORY, TRAVEL_HISTORY, etc.
    private UUID sourceId;      // nullable - id of the originating record where applicable
    private String sourceLabel; // human-readable origin, e.g. "Client record" or "Intake: Passport Number"
    private String rawValue;    // value as found in the source
    private boolean selected;   // true if this candidate was chosen as the resolved value
    private int priority;       // lower = higher precedence
}
