package com.immiauto.repository;

import com.immiauto.entity.TravelHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TravelHistoryRepository extends JpaRepository<TravelHistoryEntry, UUID> {
    List<TravelHistoryEntry> findByImmigrationCaseIdOrderBySortOrder(UUID caseId);

    @Query("SELECT COALESCE(SUM(t.daysAbsent), 0) FROM TravelHistoryEntry t WHERE t.immigrationCase.id = :caseId")
    int sumDaysAbsentByCase(UUID caseId);
}
