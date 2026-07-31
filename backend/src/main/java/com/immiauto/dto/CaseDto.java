package com.immiauto.dto;

import com.immiauto.enums.ApplicantRole;
import com.immiauto.enums.CaseStatus;
import com.immiauto.enums.CaseSubtype;
import com.immiauto.enums.LeadStatus;
import com.immiauto.enums.ServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseDto {
    private UUID id;
    private String caseNumber;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    private CaseSubtype subtype;
    private ApplicantRole applicantRole;

    private LeadStatus leadStatus;
    private CaseStatus caseStatus;

    @NotNull(message = "Client ID is required")
    private UUID clientId;
    private String clientName;

    @NotNull(message = "Consultant ID is required")
    private UUID consultantId;
    private String consultantName;

    private String intakeSummary;
    private String consultantNotes;
    private LocalDate deadline;
    private String urgencyReason;

    // Post-submission
    private LocalDate submissionDate;
    private String applicationNumber;
    private String portalUsed;
    private String biometricsStatus;
    private String medicalStatus;

    // Retainer tracking
    private LocalDate retainerSignedDate;
    private String retainerDocumentPath;
    private String engagementLetterStatus;

    // Final review / sign-off
    private String finalReviewedBy;
    private LocalDateTime finalReviewedAt;
    private String finalReviewNotes;
    private boolean consultantSignedOff;

    // Computed fields
    private long totalChecklistItems;
    private long completedItems;
    private long missingItems;
    private LocalDateTime createdAt;
}
