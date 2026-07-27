package com.immiauto.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDto {
    private long totalActiveCases;
    private long casesAwaitingDocuments;
    private long casesFileReady;
    private long casesWithUpcomingDeadlines;
    private long pendingReminders;
    private Map<String, Long> casesByServiceType;
    private Map<String, Long> casesByStatus;
    private List<CaseDto> recentCases;
    private List<CaseDto> urgentCases;
}
