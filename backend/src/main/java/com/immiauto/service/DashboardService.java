package com.immiauto.service;

import com.immiauto.dto.CaseDto;
import com.immiauto.dto.ConsultantDto;
import com.immiauto.dto.DashboardDto;
import com.immiauto.dto.OrgDashboardDto;
import com.immiauto.enums.CaseStatus;
import com.immiauto.enums.ReminderStatus;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CaseRepository caseRepository;
    private final ReminderRepository reminderRepository;
    private final CaseService caseService;
    private final ConsultantService consultantService;

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(Long consultantId) {
        List<CaseDto> activeCases = caseService.getActiveCasesByConsultant(consultantId);

        long awaitingDocs = activeCases.stream()
                .filter(c -> c.getCaseStatus() == CaseStatus.DOCUMENTS_COLLECTING).count();
        long fileReady = activeCases.stream()
                .filter(c -> c.getCaseStatus() == CaseStatus.FILE_READY).count();

        List<CaseDto> urgentCases = caseService.getCasesWithUpcomingDeadlinesByConsultant(consultantId, 14);
        long pendingReminders = reminderRepository.findByStatusAndConsultantId(ReminderStatus.DRAFT, consultantId).size();

        Map<String, Long> byServiceType = activeCases.stream()
                .collect(Collectors.groupingBy(c -> c.getServiceType().name(), Collectors.counting()));
        Map<String, Long> byStatus = activeCases.stream()
                .collect(Collectors.groupingBy(c -> c.getCaseStatus().name(), Collectors.counting()));

        return DashboardDto.builder()
                .totalActiveCases(activeCases.size())
                .casesAwaitingDocuments(awaitingDocs)
                .casesFileReady(fileReady)
                .casesWithUpcomingDeadlines(urgentCases.size())
                .pendingReminders(pendingReminders)
                .casesByServiceType(byServiceType)
                .casesByStatus(byStatus)
                .recentCases(activeCases.stream().limit(10).collect(Collectors.toList()))
                .urgentCases(urgentCases)
                .build();
    }

    @Transactional(readOnly = true)
    public OrgDashboardDto getOrganizationDashboard() {
        List<ConsultantDto> allConsultants = consultantService.getAllConsultants();

        long totalActive = 0;
        long totalAwaitingDocs = 0;
        long totalFileReady = 0;
        long totalDeadlines = 0;
        long totalReminders = 0;
        Map<String, Long> orgByServiceType = new LinkedHashMap<>();
        Map<String, Long> orgByStatus = new LinkedHashMap<>();
        List<OrgDashboardDto.ConsultantSummaryDto> summaries = new ArrayList<>();

        for (ConsultantDto c : allConsultants) {
            List<CaseDto> activeCases = caseService.getActiveCasesByConsultant(c.getId());
            List<CaseDto> deadlineCases = caseService.getCasesWithUpcomingDeadlinesByConsultant(c.getId(), 14);
            long reminders = reminderRepository.findByStatusAndConsultantId(ReminderStatus.DRAFT, c.getId()).size();

            long awaitingDocs = activeCases.stream()
                    .filter(cs -> cs.getCaseStatus() == CaseStatus.DOCUMENTS_COLLECTING).count();
            long fileReady = activeCases.stream()
                    .filter(cs -> cs.getCaseStatus() == CaseStatus.FILE_READY).count();

            totalActive += activeCases.size();
            totalAwaitingDocs += awaitingDocs;
            totalFileReady += fileReady;
            totalDeadlines += deadlineCases.size();
            totalReminders += reminders;

            activeCases.forEach(cs -> {
                orgByServiceType.merge(cs.getServiceType().name(), 1L, Long::sum);
                orgByStatus.merge(cs.getCaseStatus().name(), 1L, Long::sum);
            });

            summaries.add(OrgDashboardDto.ConsultantSummaryDto.builder()
                    .consultant(c)
                    .activeCases(activeCases.size())
                    .upcomingDeadlines(deadlineCases.size())
                    .pendingReminders(reminders)
                    .casesAwaitingDocuments(awaitingDocs)
                    .build());
        }

        return OrgDashboardDto.builder()
                .totalConsultants(allConsultants.size())
                .totalActiveCases(totalActive)
                .casesAwaitingDocuments(totalAwaitingDocs)
                .casesFileReady(totalFileReady)
                .casesWithUpcomingDeadlines(totalDeadlines)
                .pendingReminders(totalReminders)
                .casesByServiceType(orgByServiceType)
                .casesByStatus(orgByStatus)
                .consultantSummaries(summaries)
                .build();
    }
}
