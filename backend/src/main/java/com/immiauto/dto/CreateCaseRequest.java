package com.immiauto.dto;

import com.immiauto.enums.ApplicantRole;
import com.immiauto.enums.CaseSubtype;
import com.immiauto.enums.ServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateCaseRequest {
    @NotNull private ServiceType serviceType;
    private CaseSubtype subtype;
    private ApplicantRole applicantRole;
    @NotNull private UUID clientId;
    @NotNull private UUID consultantId;
    private LocalDate deadline;
    private String urgencyReason;
    private String consultantNotes;
    private boolean applyDefaultChecklist;
}
