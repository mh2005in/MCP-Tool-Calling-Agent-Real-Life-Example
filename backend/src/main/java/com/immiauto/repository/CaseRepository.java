package com.immiauto.repository;

import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.CaseStatus;
import com.immiauto.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<ImmigrationCase, UUID> {
    Optional<ImmigrationCase> findByCaseNumber(String caseNumber);
    List<ImmigrationCase> findByConsultantId(UUID consultantId);
    List<ImmigrationCase> findByClientId(UUID clientId);
    List<ImmigrationCase> findByCaseStatus(CaseStatus status);
    List<ImmigrationCase> findByServiceType(ServiceType serviceType);

    @Query("SELECT c FROM ImmigrationCase c WHERE c.consultant.id = :consultantId " +
           "AND c.caseStatus NOT IN ('CLOSED', 'DECISION_RECEIVED')")
    List<ImmigrationCase> findActiveCasesByConsultant(UUID consultantId);

    @Query("SELECT c FROM ImmigrationCase c WHERE c.deadline IS NOT NULL " +
           "AND c.deadline <= :date AND c.caseStatus NOT IN ('CLOSED', 'DECISION_RECEIVED')")
    List<ImmigrationCase> findCasesWithUpcomingDeadlines(LocalDate date);

    @Query("SELECT c FROM ImmigrationCase c WHERE c.consultant.id = :consultantId " +
           "AND c.deadline IS NOT NULL AND c.deadline <= :date " +
           "AND c.caseStatus NOT IN ('CLOSED', 'DECISION_RECEIVED')")
    List<ImmigrationCase> findCasesWithUpcomingDeadlinesByConsultant(UUID consultantId, LocalDate date);
}
