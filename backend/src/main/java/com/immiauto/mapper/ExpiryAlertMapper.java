package com.immiauto.mapper;

import com.immiauto.dto.ExpiryAlertDto;
import com.immiauto.entity.ExpiryAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpiryAlertMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    @Mapping(source = "immigrationCase.caseNumber", target = "caseNumber")
    @Mapping(source = "immigrationCase.client.fullName", target = "clientName")
    ExpiryAlertDto toDto(ExpiryAlert entity);
}
