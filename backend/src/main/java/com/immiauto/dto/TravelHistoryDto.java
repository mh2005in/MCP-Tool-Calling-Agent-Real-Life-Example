package com.immiauto.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immiauto.service.CommonService;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TravelHistoryDto {
    private Long id;
    private Long caseId;

    @NotBlank
    private String country;

    @NotNull
    @PastOrPresent
    private LocalDate entryDate;

    @PastOrPresent
    private LocalDate exitDate;

    private String purpose;

    @PositiveOrZero
    private int daysAbsent;

    private String notes;
    private int sortOrder;

    @AssertTrue(message = "Exit date must be on or after entry date")
    @JsonIgnore
    public boolean isDateRangeValid() {
        return CommonService.isDateRangeValid(entryDate, exitDate);
    }
}
