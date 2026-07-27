package com.immiauto.mapper;

import com.immiauto.dto.TravelHistoryDto;
import com.immiauto.entity.TravelHistoryEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TravelHistoryMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    TravelHistoryDto toDto(TravelHistoryEntry entity);
}
