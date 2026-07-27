package com.immiauto.mapper;

import com.immiauto.dto.MeDto;
import com.immiauto.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    // consultantAdmin/consultantName/companyName are resolved from the linked Consultant
    // in MeController (AppUser only stores consultantId).
    @Mapping(target = "consultantAdmin", ignore = true)
    @Mapping(target = "consultantName", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    MeDto toMeDto(AppUser entity);
}
