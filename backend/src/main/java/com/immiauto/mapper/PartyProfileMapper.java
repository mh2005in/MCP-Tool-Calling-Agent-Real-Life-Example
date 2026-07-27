package com.immiauto.mapper;

import com.immiauto.dto.PartyProfileDto;
import com.immiauto.entity.PartyProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartyProfileMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    PartyProfileDto toDto(PartyProfile entity);
}
