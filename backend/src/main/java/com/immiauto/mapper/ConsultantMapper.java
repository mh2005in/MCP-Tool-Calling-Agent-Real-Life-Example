package com.immiauto.mapper;

import com.immiauto.dto.ConsultantDto;
import com.immiauto.entity.Consultant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsultantMapper {

    @Mapping(target = "activeCaseCount", ignore = true)
    ConsultantDto toDto(Consultant entity);
}
