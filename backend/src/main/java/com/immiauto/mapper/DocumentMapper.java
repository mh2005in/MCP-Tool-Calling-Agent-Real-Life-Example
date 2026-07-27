package com.immiauto.mapper;

import com.immiauto.dto.DocumentDto;
import com.immiauto.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    DocumentDto toDto(Document entity);
}
