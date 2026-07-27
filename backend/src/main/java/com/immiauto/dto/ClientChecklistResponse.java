package com.immiauto.dto;

import com.immiauto.constants.ComplianceConstants;
import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientChecklistResponse {
    @Builder.Default
    private String disclaimer = ComplianceConstants.CLIENT_CHECKLIST_DISCLAIMER;

    private List<ChecklistItemDto> items;
    private long totalItems;
    private long completedItems;
    private long missingItems;
}
