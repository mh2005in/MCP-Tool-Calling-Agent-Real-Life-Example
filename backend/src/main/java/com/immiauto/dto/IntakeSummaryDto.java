package com.immiauto.dto;

import com.immiauto.enums.ServiceType;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeSummaryDto {
    private UUID caseId;
    private ServiceType serviceType;
    private Map<String, List<IntakeResponseDto>> responsesBySection;
    private String aiSummary;
    private List<IntakeResponseDto> flaggedResponses;
}
