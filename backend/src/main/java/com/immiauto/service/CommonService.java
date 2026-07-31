package com.immiauto.service;

import com.immiauto.entity.AuditLog;
import com.immiauto.entity.Consultant;
import com.immiauto.exception.AdminAccessRequiredException;
import com.immiauto.repository.AuditLogRepository;
import com.immiauto.repository.ConsultantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final AuditLogRepository auditLogRepository;
    private final ConsultantRepository consultantRepository;

    public void logAudit(String entityType, UUID entityId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .entityType(entityType).entityId(entityId)
                .action(action).details(details).build());
    }

    public void verifyOwnershipOrAdmin(UUID consultantId, UUID ownerConsultantId, String resourceName) {
        if (ownerConsultantId.equals(consultantId)) {
            return;
        }
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found: " + consultantId));
        if (!consultant.isAdmin()) {
            throw new AdminAccessRequiredException(
                    "You do not have access to this " + resourceName);
        }
    }

    public void requireAdmin(UUID consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found: " + consultantId));
        if (!consultant.isAdmin()) {
            throw new AdminAccessRequiredException(
                    "Only admin consultants can perform this action");
        }
    }

    public static boolean isDateRangeValid(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}
