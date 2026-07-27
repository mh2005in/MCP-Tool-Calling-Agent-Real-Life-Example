package com.immiauto.dto.mcp;

import com.immiauto.enums.DocumentStatus;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class McpChecklistItemDto {

    private String category;
    private String documentName;
    private DocumentStatus status;
    private boolean required;
    private boolean conditional;
    private String conditionDescription;
    private int sortOrder;
}
