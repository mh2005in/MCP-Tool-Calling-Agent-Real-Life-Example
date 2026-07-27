package com.immiauto.mapper;

import com.immiauto.dto.WorkHistoryDto;
import com.immiauto.entity.WorkHistoryEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkHistoryMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    WorkHistoryDto toDto(WorkHistoryEntry entity);
}
