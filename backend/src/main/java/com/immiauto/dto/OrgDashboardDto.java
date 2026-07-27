package com.immiauto.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrgDashboardDto {
    private long totalConsultants;
    private long totalActiveCases;
    private long casesAwaitingDocuments;
    private long casesFileReady;
    private long casesWithUpcomingDeadlines;
    private long pendingReminders;
    private Map<String, Long> casesByServiceType;
    private Map<String, Long> casesByStatus;
    private List<ConsultantSummaryDto> consultantSummaries;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ConsultantSummaryDto {
        private ConsultantDto consultant;
        private long activeCases;
        private long upcomingDeadlines;
        private long pendingReminders;
        private long casesAwaitingDocuments;
    }
}
