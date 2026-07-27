package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a detected conflict where multiple sources disagree on a value
 * for one canonical field.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CanonicalDataConflictDto {

    private String fieldKey;
    private String displayName;
    private String message;

    @Builder.Default
    private List<String> candidateValues = new ArrayList<>();

    @Builder.Default
    private List<CanonicalValueSourceDto> sources = new ArrayList<>();
}
