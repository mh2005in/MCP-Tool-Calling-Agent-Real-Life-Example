package com.immiauto.mapper;

import com.immiauto.dto.forms.PackageProfileSummaryDto;
import com.immiauto.entity.forms.PackageProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps {@link PackageProfile} entities to summary DTOs.
 * formCount / documentRequirementCount are computed in the service after mapping.
 */
@Mapper(componentModel = "spring")
public interface PackageProfileMapper {

    @Mapping(target = "formCount", ignore = true)
    @Mapping(target = "documentRequirementCount", ignore = true)
    PackageProfileSummaryDto toSummary(PackageProfile entity);
}
