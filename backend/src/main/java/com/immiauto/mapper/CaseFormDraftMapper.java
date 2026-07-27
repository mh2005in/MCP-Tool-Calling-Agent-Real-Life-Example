package com.immiauto.mapper;

import com.immiauto.dto.forms.CaseFormDraftDto;
import com.immiauto.entity.forms.CaseFormDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps {@link CaseFormDraft} entities to DTOs. File paths are converted to a
 * bare file name; timestamps to ISO strings. Raw JSON snapshot/values are not exposed here.
 */
@Mapper(componentModel = "spring")
public interface CaseFormDraftMapper {

    @Mapping(target = "caseId", source = "immigrationCase.id")
    @Mapping(target = "formDefinitionId", source = "formDefinition.id")
    @Mapping(target = "formCode", source = "formDefinition.formCode")
    @Mapping(target = "formDisplayName", source = "formDefinition.displayName")
    @Mapping(target = "mappingVersionId", source = "mappingVersion.id")
    @Mapping(target = "mappingVersion", source = "mappingVersion.mappingVersion")
    @Mapping(target = "fileName", expression = "java(draft.getDraftFilePath() == null ? null : java.nio.file.Paths.get(draft.getDraftFilePath()).getFileName().toString())")
    @Mapping(target = "generatedAt", expression = "java(draft.getGeneratedAt() == null ? null : draft.getGeneratedAt().toString())")
    @Mapping(target = "approvedAt", expression = "java(draft.getApprovedAt() == null ? null : draft.getApprovedAt().toString())")
    CaseFormDraftDto toDto(CaseFormDraft draft);
}
