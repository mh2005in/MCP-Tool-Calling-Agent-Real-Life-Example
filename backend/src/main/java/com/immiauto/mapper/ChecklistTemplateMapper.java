package com.immiauto.mapper;

import com.immiauto.dto.ChecklistTemplateDto;
import com.immiauto.entity.ChecklistTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChecklistTemplateMapper {

    ChecklistTemplateDto toDto(ChecklistTemplate entity);

    ChecklistTemplate toEntity(ChecklistTemplateDto dto);
}
