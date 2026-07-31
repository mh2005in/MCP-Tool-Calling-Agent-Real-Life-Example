package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.mcp.*;
import com.immiauto.entity.*;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class McpDataService {

    private final CaseRepository caseRepository;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final IntakeResponseRepository intakeResponseRepository;
    private final DocumentRepository documentRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ReminderRepository reminderRepository;
    private final TravelHistoryRepository travelHistoryRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final RelationshipTimelineRepository relationshipTimelineRepository;
    private final DataMaskingService dataMaskingService;
    private final DocumentMetadataSanitizer documentMetadataSanitizer;
    private final ConsistencyCheckService consistencyCheckService;
    private final DocumentClassificationService classificationService;
    private final AiBoundaryService aiBoundaryService;

    @Transactional(readOnly = true)
    public Map<String, List<MaskedIntakeResponseDto>> getIntakeSummary(UUID caseId, Consultant caller) {
        ImmigrationCase imCase = requireCaseWithAccess(caseId, caller);
        List<IntakeResponse> responses = intakeResponseRepository
                .findByImmigrationCaseIdOrderBySortOrder(caseId);
        return dataMaskingService.maskAndGroupBySection(responses);
    }

    @Transactional(readOnly = true)
    public List<SanitizedDocumentDto> getDocuments(UUID caseId, Consultant caller) {
        requireCaseWithAccess(caseId, caller);
        List<Document> docs = documentRepository.findByImmigrationCaseId(caseId);
        return documentMetadataSanitizer.sanitize(docs);
    }

    @Transactional(readOnly = true)
    public List<McpChecklistItemDto> getChecklistStatus(UUID caseId, Consultant caller) {
        requireCaseWithAccess(caseId, caller);
        return checklistItemRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream()
                .map(item -> McpChecklistItemDto.builder()
                        .category(item.getCategory())
                        .documentName(item.getDocumentName())
                        .status(item.getStatus())
                        .required(item.isRequired())
                        .conditional(item.isConditional())
                        .conditionDescription(item.getConditionDescription())
                        .sortOrder(item.getSortOrder())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> getConsistencyReport(UUID caseId, Consultant caller) {
        requireCaseWithAccess(caseId, caller);
        return consistencyCheckService.checkConsistency(caseId);
    }

    @Transactional(readOnly = true)
    public List<McpTimelineEntryDto> getTimeline(UUID caseId, Consultant caller) {
        requireCaseWithAccess(caseId, caller);
        List<McpTimelineEntryDto> entries = new ArrayList<>();

        travelHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .forEach(t -> entries.add(McpTimelineEntryDto.builder()
                        .category("TRAVEL")
                        .startDate(t.getEntryDate())
                        .endDate(t.getExitDate())
                        .description(t.getCountry() + " — " + t.getPurpose())
                        .build()));

        workHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .forEach(w -> entries.add(McpTimelineEntryDto.builder()
                        .category("WORK")
                        .startDate(w.getStartDate())
                        .endDate(w.getEndDate())
                        .description(w.getJobTitle() + " — " + w.getCountry())
                        .build()));

        relationshipTimelineRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .forEach(r -> entries.add(McpTimelineEntryDto.builder()
                        .category("RELATIONSHIP")
                        .startDate(r.getMilestoneDate())
                        .endDate(r.getMilestoneDate())
                        .description(r.getMilestoneType())
                        .build()));

        return entries;
    }

    public Map<String, String> classifyDocument(String filename, String mimeType, Long size) {
        return classificationService.suggestClassification(filename);
    }

    @Transactional(readOnly = true)
    public List<McpReminderDto> getPendingReminders(UUID caseId, Consultant caller) {
        requireCaseWithAccess(caseId, caller);
        return reminderRepository.findByImmigrationCaseId(caseId)
                .stream()
                .filter(r -> r.getStatus() == com.immiauto.enums.ReminderStatus.DRAFT
                          || r.getStatus() == com.immiauto.enums.ReminderStatus.APPROVED)
                .map(r -> McpReminderDto.builder()
                        .subject(r.getSubject())
                        .channel(r.getChannel())
                        .status(r.getStatus())
                        .scheduledAt(r.getScheduledAt())
                        .build())
                .toList();
    }

    public AiBoundaryService.AiBoundaryResult validateOutput(String text) {
        return aiBoundaryService.validateAndSanitize(text);
    }

    @Transactional(readOnly = true)
    public CaseSummaryProjection getCaseSummary(UUID caseId, Consultant caller) {
        ImmigrationCase imCase = requireCaseWithAccess(caseId, caller);

        long total = checklistItemRepository.countByImmigrationCaseId(caseId);
        long completed = checklistItemRepository.countByImmigrationCaseIdAndStatus(caseId, DocumentStatus.ACCEPTED);
        long missing = checklistItemRepository.countByImmigrationCaseIdAndStatus(caseId, DocumentStatus.NOT_UPLOADED);

        return CaseSummaryProjection.builder()
                .serviceType(imCase.getServiceType())
                .subtype(imCase.getSubtype())
                .caseStatus(imCase.getCaseStatus())
                .totalChecklistItems(total)
                .completedItems(completed)
                .missingItems(missing)
                .documents(getDocuments(caseId, caller))
                .intakeResponses(getIntakeSummary(caseId, caller))
                .build();
    }

    @Transactional(readOnly = true)
    public List<MaskedClientDto> getMaskedClients(Consultant caller) {
        requireAuthenticated(caller);
        List<Client> clients = clientRepository.findByConsultantId(caller.getId());
        return dataMaskingService.maskClients(clients);
    }

    @Transactional(readOnly = true)
    public MaskedClientDto getMaskedClient(UUID clientId, Consultant caller) {
        requireAuthenticated(caller);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + clientId));
        if (!caller.isAdmin() && !client.getConsultant().getId().equals(caller.getId())) {
            throw new SecurityException("Access denied: this client does not belong to you");
        }
        return dataMaskingService.maskClient(client);
    }

    @Transactional
    public MaskedClientDto createAndMaskClient(String fullName, String email, Consultant caller) {
        requireAuthenticated(caller);
        Client client = Client.builder()
                .fullName(fullName)
                .email(email)
                .consultant(caller)
                .build();
        client = clientRepository.save(client);
        return dataMaskingService.maskClient(client);
    }

    @Transactional(readOnly = true)
    public CaseSummaryProjection getCaseSummaryByNumber(String caseNumber, Consultant caller) {
        requireAuthenticated(caller);
        ImmigrationCase imCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new EntityNotFoundException("Case not found with number: " + caseNumber));
        return getCaseSummary(imCase.getId(), caller);
    }

    @Transactional(readOnly = true)
    public MaskedClientDto getMaskedClientByNumber(String clientNumber, Consultant caller) {
        requireAuthenticated(caller);
        Client client = clientRepository.findByClientNumber(clientNumber)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with number: " + clientNumber));
        if (!caller.isAdmin() && !client.getConsultant().getId().equals(caller.getId())) {
            throw new SecurityException("Access denied: this client does not belong to you");
        }
        return dataMaskingService.maskClient(client);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getConsultantByNumber(String consultantNumber, Consultant caller) {
        requireAuthenticated(caller);
        Consultant consultant = consultantRepository.findByConsultantNumber(consultantNumber)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found with number: " + consultantNumber));
        return Map.of(
                "id", consultant.getId(),
                "consultantNumber", consultant.getConsultantNumber(),
                "fullName", consultant.getFullName(),
                "email", consultant.getEmail(),
                "companyName", consultant.getCompanyName() != null ? consultant.getCompanyName() : "",
                "active", consultant.isActive()
        );
    }

    private void requireAuthenticated(Consultant caller) {
        if (caller == null) {
            throw new SecurityException("MCP authentication required");
        }
    }

    private ImmigrationCase requireCaseWithAccess(UUID caseId, Consultant caller) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));
        if (caller != null && !caller.isAdmin()) {
            if (imCase.getConsultant() == null || !imCase.getConsultant().getId().equals(caller.getId())) {
                throw new SecurityException("Access denied: this case does not belong to you");
            }
        }
        return imCase;
    }
}
