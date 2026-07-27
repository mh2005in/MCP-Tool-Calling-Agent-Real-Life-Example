package com.immiauto.repository;

import com.immiauto.entity.ExpiryAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpiryAlertRepository extends JpaRepository<ExpiryAlert, Long> {
    List<ExpiryAlert> findByImmigrationCaseId(Long caseId);
    List<ExpiryAlert> findByAcknowledgedFalse();
    List<ExpiryAlert> findByExpiryDateBeforeAndAcknowledgedFalse(LocalDate date);
    List<ExpiryAlert> findByAlertTypeAndImmigrationCaseId(String alertType, Long caseId);

    @Query("SELECT e FROM ExpiryAlert e WHERE e.acknowledged = false " +
           "AND e.immigrationCase.consultant.id = :consultantId ORDER BY e.expiryDate ASC")
    List<ExpiryAlert> findActiveAlertsByConsultant(Long consultantId);
}
