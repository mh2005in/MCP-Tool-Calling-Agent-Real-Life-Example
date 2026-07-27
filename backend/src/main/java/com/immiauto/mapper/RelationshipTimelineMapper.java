package com.immiauto.mapper;

import com.immiauto.dto.RelationshipTimelineDto;
import com.immiauto.entity.RelationshipTimeline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RelationshipTimelineMapper {

    @Mapping(source = "immigrationCase.id", target = "caseId")
    RelationshipTimelineDto toDto(RelationshipTimeline entity);
}
