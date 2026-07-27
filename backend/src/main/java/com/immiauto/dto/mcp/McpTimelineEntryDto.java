package com.immiauto.dto.mcp;

import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class McpTimelineEntryDto {

    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
