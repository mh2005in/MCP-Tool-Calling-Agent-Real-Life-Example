package com.immiauto.mapper;

import com.immiauto.dto.forms.FormDefinitionDto;
import com.immiauto.dto.forms.FormFieldDefinitionDto;
import com.immiauto.dto.forms.FormMappingVersionDto;
import com.immiauto.dto.forms.PackageProfileDto;
import com.immiauto.entity.forms.FormDefinition;
import com.immiauto.entity.forms.FormFieldDefinition;
import com.immiauto.entity.forms.FormMappingVersion;
import com.immiauto.entity.forms.PackageProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Read mappings for the form catalogue admin (Section 4.1 - Milestone 6).
 * Computed counts are populated by the service after mapping.
 */
@Mapper(componentModel = "spring")
public interface FormCatalogueMapper {

    @Mapping(target = "fieldCount", ignore = true)
    FormDefinitionDto toDto(FormDefinition entity);

    FormFieldDefinitionDto toDto(FormFieldDefinition entity);

    @Mapping(target = "formCount", ignore = true)
    @Mapping(target = "documentRequirementCount", ignore = true)
    PackageProfileDto toDto(PackageProfile entity);

    @Mapping(target = "formDefinitionId", source = "formDefinition.id")
    @Mapping(target = "approvedAt", expression = "java(entity.getApprovedAt() == null ? null : entity.getApprovedAt().toString())")
    FormMappingVersionDto toDto(FormMappingVersion entity);
}
