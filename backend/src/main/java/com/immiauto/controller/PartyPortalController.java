package com.immiauto.controller;

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
    public PartyProfileDto createParty(@PathVariable Long caseId,
                                        @RequestBody PartyProfileDto dto) {
        return partyPortalService.createPartyProfile(caseId, dto);
    }

    @GetMapping("/cases/{caseId}/parties")
    public List<PartyProfileDto> getParties(@PathVariable Long caseId) {
        return partyPortalService.getPartiesForCase(caseId);
    }

    @PutMapping("/parties/{partyId}")
    public PartyProfileDto updateParty(@PathVariable Long partyId,
                                        @RequestBody PartyProfileDto dto) {
        return partyPortalService.updatePartyProfile(partyId, dto);
    }

    @DeleteMapping("/parties/{partyId}")
    public ResponseEntity<Void> deleteParty(@PathVariable Long partyId) {
        partyPortalService.deletePartyProfile(partyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/portal/{token}")
    public PartyProfileDto getPortalByToken(@PathVariable String token) {
        return partyPortalService.getPartyByToken(token);
    }
}
