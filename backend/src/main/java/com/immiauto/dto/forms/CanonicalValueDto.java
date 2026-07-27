package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A resolved canonical value for one canonical field key, with provenance,
 * conflict, and review markers.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CanonicalValueDto {

    private String fieldKey;
    private String displayName;
    private String category;
    private String dataType;
    private String value;          // normalized/selected value (ISO dates), null if absent
    private boolean present;       // a non-blank value was resolved
    private boolean sensitive;
    private boolean reviewRequired; // conflict, flagged intake answer, or review-needed mapping
    private boolean conflict;       // multiple distinct candidate values found
    private String note;            // provenance/diagnostic note

    @Builder.Default
    private List<CanonicalValueSourceDto> sources = new ArrayList<>();
}
