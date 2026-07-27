package com.immiauto.service;

import com.immiauto.dto.ChecklistItemDto;
import com.immiauto.dto.ClientChecklistResponse;
import com.immiauto.entity.ChecklistItem;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.mapper.ChecklistItemMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.ChecklistItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistItemRepository checklistItemRepository;
    private final CaseRepository caseRepository;
    private final ChecklistItemMapper checklistItemMapper;

    @Transactional
    public ChecklistItemDto addItem(Long caseId, ChecklistItemDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        ChecklistItem item = ChecklistItem.builder()
                .immigrationCase(imCase)
                .category(dto.getCategory())
                .documentName(dto.getDocumentName())
                .description(dto.getDescription())
                .status(DocumentStatus.NOT_UPLOADED)
                .required(dto.isRequired())
                .conditional(dto.isConditional())
                .conditionDescription(dto.getConditionDescription())
                .sortOrder(dto.getSortOrder())
                .build();
        return checklistItemMapper.toDto(checklistItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<ChecklistItemDto> getChecklistForCase(Long caseId) {
        return checklistItemRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(checklistItemMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChecklistItemDto> getMissingItems(Long caseId) {
        return checklistItemRepository.findByImmigrationCaseIdAndStatus(caseId, DocumentStatus.NOT_UPLOADED)
                .stream().map(checklistItemMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ChecklistItemDto updateItemStatus(Long itemId, DocumentStatus status, String reviewNote) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist item not found"));
        item.setStatus(status);
        if (reviewNote != null) item.setConsultantReviewNote(reviewNote);
        return checklistItemMapper.toDto(checklistItemRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long itemId) {
        checklistItemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    public ClientChecklistResponse getClientChecklist(Long caseId) {
        List<ChecklistItemDto> items = getChecklistForCase(caseId);
        long total = items.size();
        long completed = items.stream().filter(i -> i.getStatus() == DocumentStatus.ACCEPTED).count();
        long missing = items.stream().filter(i -> i.getStatus() == DocumentStatus.NOT_UPLOADED).count();

        return ClientChecklistResponse.builder()
                .items(items)
                .totalItems(total)
                .completedItems(completed)
                .missingItems(missing)
                .build();
    }
}
