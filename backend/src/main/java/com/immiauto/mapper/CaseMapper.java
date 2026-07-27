package com.immiauto.mapper;

import com.immiauto.dto.CaseDto;
import com.immiauto.entity.ImmigrationCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CaseMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.fullName", target = "clientName")
    @Mapping(source = "consultant.id", target = "consultantId")
    @Mapping(source = "consultant.fullName", target = "consultantName")
    @Mapping(target = "totalChecklistItems", ignore = true)
    @Mapping(target = "completedItems", ignore = true)
    @Mapping(target = "missingItems", ignore = true)
    CaseDto toDto(ImmigrationCase entity);
}
