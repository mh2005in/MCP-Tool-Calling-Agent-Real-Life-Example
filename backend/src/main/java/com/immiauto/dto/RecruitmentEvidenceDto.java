package com.immiauto.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immiauto.service.CommonService;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentEvidenceDto {
    private UUID id;
    private UUID caseId;

    @NotBlank
    private String evidenceType;

    @NotBlank
    private String platform;

    @NotNull
    @PastOrPresent
    private LocalDate postingDate;

    private LocalDate expiryDate;

    @PositiveOrZero
    private int daysPosted;

    @PositiveOrZero
    private int applicantsReceived;

    @PositiveOrZero
    private int interviewsConducted;

    private String nonHireReasons;
    private boolean screenshotAttached;
    private String notes;
    private int sortOrder;

    @AssertTrue(message = "Expiry date must be on or after posting date")
    @JsonIgnore
    public boolean isDateRangeValid() {
        return CommonService.isDateRangeValid(postingDate, expiryDate);
    }

    @AssertTrue(message = "Interviews conducted must not exceed applicants received")
    @JsonIgnore
    public boolean isInterviewCountValid() {
        return interviewsConducted <= applicantsReceived;
    }
}
