package com.immiauto.mapper;

import com.immiauto.dto.ChecklistItemDto;
import com.immiauto.entity.ChecklistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChecklistItemMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    @Mapping(source = "linkedDocument.id", target = "linkedDocumentId")
    ChecklistItemDto toDto(ChecklistItem entity);
}
