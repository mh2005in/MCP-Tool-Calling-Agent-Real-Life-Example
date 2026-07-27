package com.immiauto.mapper;

import com.immiauto.dto.ReminderDto;
import com.immiauto.entity.Reminder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    ReminderDto toDto(Reminder entity);
}
