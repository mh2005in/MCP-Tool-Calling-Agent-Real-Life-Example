package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.dto.PartyProfileDto;
import com.immiauto.service.PartyPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PartyPortalController {

    private final PartyPortalService partyPortalService;

    @PostMapping("/cases/{caseId}/parties")
    public PartyProfileDto createParty(@PathVariable UUID caseId,
                                        @RequestBody PartyProfileDto dto) {
        return partyPortalService.createPartyProfile(caseId, dto);
    }

    @GetMapping("/cases/{caseId}/parties")
    public List<PartyProfileDto> getParties(@PathVariable UUID caseId) {
        return partyPortalService.getPartiesForCase(caseId);
    }

    @PutMapping("/parties/{partyId}")
    public PartyProfileDto updateParty(@PathVariable UUID partyId,
                                        @RequestBody PartyProfileDto dto) {
        return partyPortalService.updatePartyProfile(partyId, dto);
    }

    @DeleteMapping("/parties/{partyId}")
    public ResponseEntity<Void> deleteParty(@PathVariable UUID partyId) {
        partyPortalService.deletePartyProfile(partyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/portal/{token}")
    public PartyProfileDto getPortalByToken(@PathVariable String token) {
        return partyPortalService.getPartyByToken(token);
    }
}
