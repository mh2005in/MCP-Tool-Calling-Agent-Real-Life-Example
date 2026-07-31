package com.immiauto.repository;

import com.immiauto.entity.RelationshipTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelationshipTimelineRepository extends JpaRepository<RelationshipTimeline, UUID> {
    List<RelationshipTimeline> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);
}
