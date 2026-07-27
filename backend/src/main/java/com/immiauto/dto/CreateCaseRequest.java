package com.immiauto.dto;

import com.immiauto.enums.ApplicantRole;
import com.immiauto.enums.CaseSubtype;
import com.immiauto.enums.ServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateCaseRequest {
    @NotNull private ServiceType serviceType;
    private CaseSubtype subtype;
    private ApplicantRole applicantRole;
    @NotNull private Long clientId;
    @NotNull private Long consultantId;
    private LocalDate deadline;
    private String urgencyReason;
    private String consultantNotes;
    private boolean applyDefaultChecklist;
}
