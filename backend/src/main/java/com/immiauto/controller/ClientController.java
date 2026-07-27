package com.immiauto.controller;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.ClientDto;
import com.immiauto.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CLIENTS_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@PreAuthorize("@consultantAccess.canAccess(#consultantId)")
public class ClientController {

    private final ClientService clientService;

    @PostMapping(ApiPaths.CLIENTS_CREATE)
    public ResponseEntity<ClientDto> createClient(@PathVariable Long consultantId,
                                                   @Valid @RequestBody ClientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(consultantId, dto));
    }

    @GetMapping(ApiPaths.CLIENTS_GET_BY_ID)
    public ResponseEntity<ClientDto> getClient(@PathVariable Long consultantId,
                                                @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClient(consultantId, id));
    }

    @GetMapping(ApiPaths.CLIENTS_GET)
    public ResponseEntity<List<ClientDto>> getAllClients(@PathVariable Long consultantId) {
        return ResponseEntity.ok(clientService.getClientsByConsultant(consultantId));
    }

    @GetMapping(ApiPaths.CLIENTS_SEARCH)
    public ResponseEntity<List<ClientDto>> searchClients(@PathVariable Long consultantId,
                                                          @RequestParam String q) {
        return ResponseEntity.ok(clientService.searchClients(consultantId, q));
    }

    @GetMapping(ApiPaths.CLIENTS_SEARCH_BY_NUMBER)
    public ResponseEntity<ClientDto> getClientByClientNumber(@PathVariable Long consultantId,
                                                              @RequestParam String clientNumber) {
        return ResponseEntity.ok(clientService.getClientByClientNumber(consultantId, clientNumber));
    }

    @PutMapping(ApiPaths.CLIENTS_UPDATE)
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long consultantId,
                                                   @PathVariable Long id,
                                                   @RequestBody ClientDto dto) {
        return ResponseEntity.ok(clientService.updateClient(consultantId, id, dto));
    }

    @GetMapping(ApiPaths.CLIENTS_GET_ALL_ADMIN)
    public ResponseEntity<List<ClientDto>> getAllClientsAdmin(@PathVariable Long consultantId) {
        return ResponseEntity.ok(clientService.getAllClientsForAdmin(consultantId));
    }

    @GetMapping(ApiPaths.CLIENTS_GET_BY_TARGET_CONSULTANT)
    public ResponseEntity<List<ClientDto>> getClientsByTargetConsultant(
            @PathVariable Long consultantId,
            @PathVariable Long targetConsultantId) {
        return ResponseEntity.ok(clientService.getClientsByConsultantForAdmin(consultantId, targetConsultantId));
    }

    @GetMapping(ApiPaths.CLIENTS_SEARCH_ADMIN)
    public ResponseEntity<List<ClientDto>> searchClientsAdmin(@PathVariable Long consultantId,
                                                               @RequestParam String q) {
        return ResponseEntity.ok(clientService.searchClientsForAdmin(consultantId, q));
    }
}
