package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.ExpiryAlertDto;
import com.immiauto.entity.*;
import com.immiauto.enums.ServiceType;
import com.immiauto.mapper.ExpiryAlertMapper;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpiryAlertService {

    private final ExpiryAlertRepository expiryAlertRepository;
    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;
    private final ExpiryAlertMapper expiryAlertMapper;

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void scanForExpiringDocuments() {
        log.info("Running daily expiry scan");
        LocalDate today = LocalDate.now();
        LocalDate alertHorizon = today.plusMonths(3);

        List<ImmigrationCase> activeCases = caseRepository.findByCaseStatus(
                com.immiauto.enums.CaseStatus.DOCUMENTS_COLLECTING);
        activeCases.addAll(caseRepository.findByCaseStatus(
                com.immiauto.enums.CaseStatus.INTAKE_PENDING));
        activeCases.addAll(caseRepository.findByCaseStatus(
                com.immiauto.enums.CaseStatus.INTAKE_COMPLETED));

        for (ImmigrationCase c : activeCases) {
            scanCaseDocuments(c, today, alertHorizon);
            scanServiceSpecificExpiry(c, today);
        }
    }

    private void scanCaseDocuments(ImmigrationCase c, LocalDate today, LocalDate horizon) {
        List<Document> docs = documentRepository.findByImmigrationCaseId(c.getId());
        for (Document doc : docs) {
            if (doc.getExpiryDate() != null && doc.getExpiryDate().isBefore(horizon)) {
                long days = ChronoUnit.DAYS.between(today, doc.getExpiryDate());
                String severity = days <= 0 ? "CRITICAL" : days <= 30 ? "URGENT" : days <= 60 ? "WARNING" : "INFO";
                String alertType = inferAlertType(doc);

                List<ExpiryAlert> existing = expiryAlertRepository
                        .findByAlertTypeAndImmigrationCaseId(alertType, c.getId());
                if (existing.stream().noneMatch(a -> a.getLinkedDocumentId() != null
                        && a.getLinkedDocumentId().equals(doc.getId()) && !a.isAcknowledged())) {
                    expiryAlertRepository.save(ExpiryAlert.builder()
                            .immigrationCase(c)
                            .alertType(alertType)
                            .documentDescription(doc.getDocumentType() != null ? doc.getDocumentType() : doc.getOriginalFileName())
                            .expiryDate(doc.getExpiryDate())
                            .daysUntilExpiry((int) days)
                            .severity(severity)
                            .acknowledged(false)
                            .linkedDocumentId(doc.getId())
                            .build());
                }
            }
        }
    }

    private void scanServiceSpecificExpiry(ImmigrationCase c, LocalDate today) {
        ServiceType type = c.getServiceType();
        if (type == ServiceType.PGWP) {
            createServiceAlert(c, "STUDY_PERMIT", "Study permit — apply for PGWP within 180 days of expiry", today);
        } else if (type == ServiceType.PR_CARD_PRTD) {
            createServiceAlert(c, "PR_CARD", "PR card — renewal recommended 9 months before expiry", today);
        }
    }

    private void createServiceAlert(ImmigrationCase c, String alertType, String desc, LocalDate today) {
        List<ExpiryAlert> existing = expiryAlertRepository.findByAlertTypeAndImmigrationCaseId(alertType, c.getId());
        if (existing.stream().noneMatch(a -> !a.isAcknowledged())) {
            if (c.getDeadline() != null) {
                long days = ChronoUnit.DAYS.between(today, c.getDeadline());
                String severity = days <= 0 ? "CRITICAL" : days <= 30 ? "URGENT" : days <= 90 ? "WARNING" : "INFO";
                expiryAlertRepository.save(ExpiryAlert.builder()
                        .immigrationCase(c)
                        .alertType(alertType)
                        .documentDescription(desc)
                        .expiryDate(c.getDeadline())
                        .daysUntilExpiry((int) days)
                        .severity(severity)
                        .acknowledged(false)
                        .build());
            }
        }
    }

    private String inferAlertType(Document doc) {
        String type = doc.getDocumentType() != null ? doc.getDocumentType().toUpperCase() : "";
        if (type.contains("PASSPORT")) return "PASSPORT";
        if (type.contains("ECA") || type.contains("CREDENTIAL")) return "ECA";
        if (type.contains("LANGUAGE") || type.contains("IELTS") || type.contains("CELPIP") || type.contains("TEF")) return "LANGUAGE_TEST";
        if (type.contains("PR CARD")) return "PR_CARD";
        if (type.contains("MEDICAL")) return "MEDICAL";
        if (type.contains("POLICE")) return "POLICE_CERT";
        return "DOCUMENT";
    }

    @Transactional(readOnly = true)
    public List<ExpiryAlertDto> getAlertsByConsultant(UUID consultantId) {
        return expiryAlertRepository.findActiveAlertsByConsultant(consultantId)
                .stream().map(expiryAlertMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpiryAlertDto> getAlertsForCase(UUID caseId) {
        return expiryAlertRepository.findByImmigrationCaseId(caseId)
                .stream().map(expiryAlertMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ExpiryAlertDto acknowledgeAlert(UUID alertId, String acknowledgedBy) {
        ExpiryAlert alert = expiryAlertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found"));
        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(acknowledgedBy);
        return expiryAlertMapper.toDto(expiryAlertRepository.save(alert));
    }
}
