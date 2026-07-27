package com.immiauto.mapper;

import com.immiauto.dto.ClientDto;
import com.immiauto.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "consultant.id", target = "consultantId")
    ClientDto toDto(Client entity);
}
