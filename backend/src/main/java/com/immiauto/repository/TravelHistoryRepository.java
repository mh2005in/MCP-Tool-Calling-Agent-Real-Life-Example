package com.immiauto.repository;

import com.immiauto.entity.TravelHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelHistoryRepository extends JpaRepository<TravelHistoryEntry, Long> {
    List<TravelHistoryEntry> findByImmigrationCaseIdOrderBySortOrder(Long caseId);

    @Query("SELECT COALESCE(SUM(t.daysAbsent), 0) FROM TravelHistoryEntry t WHERE t.immigrationCase.id = :caseId")
    int sumDaysAbsentByCase(Long caseId);
}
