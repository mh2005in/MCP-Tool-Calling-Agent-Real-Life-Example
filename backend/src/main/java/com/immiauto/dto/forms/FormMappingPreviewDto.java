package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mapping preview for one form within a package profile.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormMappingPreviewDto {

    private UUID formDefinitionId;
    private String formCode;
    private String displayName;
    private String editionLabel;
    private boolean supportsFill;
    private UUID mappingVersionId;
    private Integer mappingVersion;
    private String mappingStatus;          // APPROVED, or null when no approved mapping exists
    private String diagnostic;             // set when the form cannot be previewed (e.g. no approved mapping)

    @Builder.Default
    private List<MappedFieldPreviewDto> fields = new ArrayList<>();

    @Builder.Default
    private List<String> unmappedRequiredFieldNames = new ArrayList<>();
}
