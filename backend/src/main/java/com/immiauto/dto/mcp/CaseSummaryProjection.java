package com.immiauto.dto.mcp;

import com.immiauto.enums.CaseStatus;
import com.immiauto.enums.CaseSubtype;
import com.immiauto.enums.ServiceType;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseSummaryProjection {

    private ServiceType serviceType;
    private CaseSubtype subtype;
    private CaseStatus caseStatus;

    private long totalChecklistItems;
    private long completedItems;
    private long missingItems;

    private List<SanitizedDocumentDto> documents;
    private Map<String, List<MaskedIntakeResponseDto>> intakeResponses;
}
