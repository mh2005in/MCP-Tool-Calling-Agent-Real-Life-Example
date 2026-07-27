package com.immiauto.repository;

import com.immiauto.entity.Reminder;
import com.immiauto.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByImmigrationCaseId(Long caseId);
    List<Reminder> findByStatus(ReminderStatus status);
    List<Reminder> findByStatusAndScheduledAtBefore(ReminderStatus status, LocalDateTime dateTime);

    @Query("SELECT r FROM Reminder r WHERE r.status = :status " +
           "AND r.immigrationCase.consultant.id = :consultantId")
    List<Reminder> findByStatusAndConsultantId(ReminderStatus status, Long consultantId);
}
