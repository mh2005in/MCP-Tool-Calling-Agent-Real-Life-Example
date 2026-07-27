package com.immiauto.service;

import com.immiauto.dto.ReminderDto;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.Reminder;
import com.immiauto.enums.ReminderStatus;
import com.immiauto.mapper.ReminderMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.ReminderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final CaseRepository caseRepository;
    private final ReminderMapper reminderMapper;

    @Transactional
    public ReminderDto createReminder(Long caseId, ReminderDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        Reminder reminder = Reminder.builder()
                .immigrationCase(imCase)
                .subject(dto.getSubject())
                .messageBody(dto.getMessageBody())
                .channel(dto.getChannel() != null ? dto.getChannel() : "email")
                .status(ReminderStatus.DRAFT)
                .scheduledAt(dto.getScheduledAt())
                .build();
        return reminderMapper.toDto(reminderRepository.save(reminder));
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getRemindersForCase(Long caseId) {
        return reminderRepository.findByImmigrationCaseId(caseId)
                .stream().map(reminderMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ReminderDto approveReminder(Long reminderId, String approvedBy) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));
        reminder.setStatus(ReminderStatus.APPROVED);
        reminder.setApprovedAt(LocalDateTime.now());
        reminder.setApprovedBy(approvedBy);
        return reminderMapper.toDto(reminderRepository.save(reminder));
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getPendingReminders() {
        return reminderRepository.findByStatus(ReminderStatus.DRAFT)
                .stream().map(reminderMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getPendingRemindersByConsultant(Long consultantId) {
        return reminderRepository.findByStatusAndConsultantId(ReminderStatus.DRAFT, consultantId)
                .stream().map(reminderMapper::toDto).collect(Collectors.toList());
    }
}
