package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.RelationshipTimelineDto;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.RelationshipTimeline;
import com.immiauto.mapper.RelationshipTimelineMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.RelationshipTimelineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationshipTimelineService {

    private final RelationshipTimelineRepository timelineRepository;
    private final CaseRepository caseRepository;
    private final RelationshipTimelineMapper relationshipTimelineMapper;

    @Transactional
    public RelationshipTimelineDto addEntry(UUID caseId, RelationshipTimelineDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        RelationshipTimeline entry = RelationshipTimeline.builder()
                .immigrationCase(imCase)
                .milestoneType(dto.getMilestoneType())
                .milestoneDate(dto.getMilestoneDate())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .evidenceCategory(dto.getEvidenceCategory())
                .evidenceCount(dto.getEvidenceCount())
                .sortOrder(dto.getSortOrder())
                .build();
        return relationshipTimelineMapper.toDto(timelineRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<RelationshipTimelineDto> getEntries(UUID caseId) {
        return timelineRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)
                .stream().map(relationshipTimelineMapper::toDto).toList();
    }

    @Transactional
    public RelationshipTimelineDto updateEntry(UUID caseId, UUID id, RelationshipTimelineDto dto) {
        RelationshipTimeline entry = timelineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        if (dto.getMilestoneType() != null) entry.setMilestoneType(dto.getMilestoneType());
        if (dto.getMilestoneDate() != null) entry.setMilestoneDate(dto.getMilestoneDate());
        if (dto.getLocation() != null) entry.setLocation(dto.getLocation());
        if (dto.getDescription() != null) entry.setDescription(dto.getDescription());
        if (dto.getEvidenceCategory() != null) entry.setEvidenceCategory(dto.getEvidenceCategory());
        entry.setEvidenceCount(dto.getEvidenceCount());
        return relationshipTimelineMapper.toDto(timelineRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(UUID caseId, UUID id) {
        RelationshipTimeline entry = timelineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found"));
        if (!entry.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Entry not found");
        }
        timelineRepository.delete(entry);
    }
}
