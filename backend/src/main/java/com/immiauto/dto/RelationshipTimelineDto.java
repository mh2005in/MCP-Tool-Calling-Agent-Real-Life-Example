package com.immiauto.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RelationshipTimelineDto {
    private Long id;
    private Long caseId;

    @NotBlank
    private String milestoneType;

    @NotNull
    @PastOrPresent
    private LocalDate milestoneDate;

    private String location;
    private String description;
    private String evidenceCategory;

    @PositiveOrZero
    private int evidenceCount;

    private int sortOrder;
}
