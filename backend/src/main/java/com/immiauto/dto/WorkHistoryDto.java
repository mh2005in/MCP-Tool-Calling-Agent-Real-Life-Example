package com.immiauto.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immiauto.service.CommonService;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkHistoryDto {
    private Long id;
    private Long caseId;

    @NotBlank
    private String employerName;

    private String jobTitle;
    private String nocTeerCode;

    @NotNull
    @PastOrPresent
    private LocalDate startDate;

    @PastOrPresent
    private LocalDate endDate;

    private boolean currentJob;

    @Positive
    @DecimalMax("168.0")
    private double hoursPerWeek;

    private String employmentType;
    private String duties;
    private String country;
    private String city;
    private boolean referenceLetterReceived;
    private boolean referenceDatesMatch;
    private boolean referenceDutiesDescribed;
    private int sortOrder;

    @AssertTrue(message = "End date must be on or after start date")
    @JsonIgnore
    public boolean isDateRangeValid() {
        return CommonService.isDateRangeValid(startDate, endDate);
    }

    @AssertTrue(message = "Current job must not have an end date")
    @JsonIgnore
    public boolean isCurrentJobEndDateValid() {
        if (!currentJob) {
            return true;
        }
        return endDate == null;
    }
}
