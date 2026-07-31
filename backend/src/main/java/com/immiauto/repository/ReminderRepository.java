package com.immiauto.repository;

import com.immiauto.entity.Reminder;
import com.immiauto.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    List<Reminder> findByImmigrationCaseId(UUID caseId);
    List<Reminder> findByStatus(ReminderStatus status);
    List<Reminder> findByStatusAndScheduledAtBefore(ReminderStatus status, LocalDateTime dateTime);

    @Query("SELECT r FROM Reminder r WHERE r.status = :status " +
           "AND r.immigrationCase.consultant.id = :consultantId")
    List<Reminder> findByStatusAndConsultantId(ReminderStatus status, UUID consultantId);
}
