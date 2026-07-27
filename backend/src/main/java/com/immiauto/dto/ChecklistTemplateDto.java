package com.immiauto.dto;

import com.immiauto.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistTemplateDto {
    private Long id;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Document name is required")
    private String documentName;

    private String description;
    private boolean required;
    private boolean conditional;
    private String conditionDescription;
    private int sortOrder;
    private String sourceUrl;
    private LocalDate lastReviewedDate;
    private Long reviewedByConsultantId;
    private String reviewedByConsultantName;
    private int ruleVersion;
    private boolean approvedForUse;
    private Long approvedByConsultantId;
    private String approvedByConsultantName;
    private LocalDate approvedDate;
}
