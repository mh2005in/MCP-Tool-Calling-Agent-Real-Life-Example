package com.immiauto.repository;

import com.immiauto.entity.RelationshipTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipTimelineRepository extends JpaRepository<RelationshipTimeline, Long> {
    List<RelationshipTimeline> findByImmigrationCaseIdOrderBySortOrder(Long caseId);
}
