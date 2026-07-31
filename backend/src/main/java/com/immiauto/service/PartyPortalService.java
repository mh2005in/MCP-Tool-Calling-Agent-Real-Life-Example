package com.immiauto.service;

import com.immiauto.dto.ChecklistItemDto;
import com.immiauto.dto.PartyProfileDto;
import com.immiauto.entity.*;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.mapper.PartyProfileMapper;
import com.immiauto.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyPortalService {

    private final PartyProfileRepository partyProfileRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CaseRepository caseRepository;
    private final PartyProfileMapper partyProfileMapper;

    @Transactional
    public PartyProfileDto createPartyProfile(UUID caseId, PartyProfileDto dto) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));
        PartyProfile profile = PartyProfile.builder()
                .immigrationCase(imCase)
                .partyType(dto.getPartyType())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .relationship(dto.getRelationship())
                .organization(dto.getOrganization())
                .portalEnabled(dto.isPortalEnabled())
                .accessToken(UUID.randomUUID().toString())
                .notes(dto.getNotes())
                .build();
        return partyProfileMapper.toDto(partyProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public List<PartyProfileDto> getPartiesForCase(UUID caseId) {
        return partyProfileRepository.findByImmigrationCaseId(caseId)
                .stream().map(partyProfileMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PartyProfileDto getPartyByToken(String token) {
        return partyProfileMapper.toDto(partyProfileRepository.findByAccessToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid portal token")));
    }

    @Transactional
    public PartyProfileDto updatePartyProfile(UUID id, PartyProfileDto dto) {
        PartyProfile profile = partyProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Party not found"));
        if (dto.getFullName() != null) profile.setFullName(dto.getFullName());
        if (dto.getEmail() != null) profile.setEmail(dto.getEmail());
        if (dto.getPhone() != null) profile.setPhone(dto.getPhone());
        if (dto.getRelationship() != null) profile.setRelationship(dto.getRelationship());
        if (dto.getOrganization() != null) profile.setOrganization(dto.getOrganization());
        if (dto.getNotes() != null) profile.setNotes(dto.getNotes());
        profile.setPortalEnabled(dto.isPortalEnabled());
        return partyProfileMapper.toDto(partyProfileRepository.save(profile));
    }

    @Transactional
    public void deletePartyProfile(UUID id) {
        partyProfileRepository.deleteById(id);
    }
}
