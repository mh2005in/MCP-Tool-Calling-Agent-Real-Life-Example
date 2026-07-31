package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.ClientDto;
import com.immiauto.entity.Client;
import com.immiauto.entity.Consultant;
import com.immiauto.exception.DuplicateClientException;
import com.immiauto.exception.DuplicateEmailException;
import com.immiauto.mapper.ClientMapper;
import com.immiauto.repository.ClientRepository;
import com.immiauto.repository.ConsultantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final CommonService commonService;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientDto createClient(UUID consultantId, ClientDto dto) {
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found: " + consultantId));

        if (dto.getEmail() != null) {
            Optional<Client> byEmail = clientRepository.findByEmail(dto.getEmail());
            if (byEmail.isPresent()) {
                throw new DuplicateEmailException(
                        "A client with email '" + dto.getEmail() + "' already exists");
            }
        }

        if (dto.getFullName() != null && dto.getDateOfBirth() != null && dto.getEmail() != null) {
            Optional<Client> existing = clientRepository
                    .findByFullNameIgnoreCaseAndDateOfBirthAndEmailIgnoreCase(
                            dto.getFullName(), dto.getDateOfBirth(), dto.getEmail());
            if (existing.isPresent()) {
                throw new DuplicateClientException(
                        "A client with this name, date of birth, and email already exists",
                        existing.get().getId());
            }
        }

        Client client = Client.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .whatsapp(dto.getWhatsapp())
                .dateOfBirth(dto.getDateOfBirth())
                .countryOfCitizenship(dto.getCountryOfCitizenship())
                .currentLocation(dto.getCurrentLocation())
                .currentStatus(dto.getCurrentStatus())
                .passportNumber(dto.getPassportNumber())
                .maritalStatus(dto.getMaritalStatus())
                .preferredLanguage(dto.getPreferredLanguage())
                .notes(dto.getNotes())
                .consultant(consultant)
                .build();
        client = clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    public ClientDto getClient(UUID consultantId, UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + clientId));
        commonService.verifyOwnershipOrAdmin(consultantId, client.getConsultant().getId(), "client");
        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    public List<ClientDto> getClientsByConsultant(UUID consultantId) {
        return clientRepository.findByConsultantId(consultantId).stream()
                .map(clientMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientDto> getClientsByConsultantForAdmin(UUID requestingConsultantId, UUID targetConsultantId) {
        commonService.requireAdmin(requestingConsultantId);
        return clientRepository.findByConsultantId(targetConsultantId).stream()
                .map(clientMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientDto> getAllClientsForAdmin(UUID requestingConsultantId) {
        commonService.requireAdmin(requestingConsultantId);
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientDto getClientForAdmin(UUID requestingConsultantId, UUID clientId) {
        commonService.requireAdmin(requestingConsultantId);
        return clientMapper.toDto(clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + clientId)));
    }

    @Transactional(readOnly = true)
    public List<ClientDto> searchClientsForAdmin(UUID requestingConsultantId, String query) {
        commonService.requireAdmin(requestingConsultantId);
        return clientRepository.search(query).stream()
                .map(clientMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientDto getClientByClientNumber(UUID consultantId, String clientNumber) {
        Client client = clientRepository.findByClientNumber(clientNumber)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with number: " + clientNumber));
        commonService.verifyOwnershipOrAdmin(consultantId, client.getConsultant().getId(), "client");
        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    public List<ClientDto> searchClients(UUID consultantId, String query) {
        return clientRepository.searchByConsultant(consultantId, query).stream()
                .map(clientMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ClientDto updateClient(UUID consultantId, UUID id, ClientDto dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + id));
        commonService.verifyOwnershipOrAdmin(consultantId, client.getConsultant().getId(), "client");
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(client.getEmail())) {
            Optional<Client> byEmail = clientRepository.findByEmail(dto.getEmail());
            if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
                throw new DuplicateEmailException(
                        "A client with email '" + dto.getEmail() + "' already exists");
            }
        }
        if (dto.getFullName() != null) client.setFullName(dto.getFullName());
        if (dto.getEmail() != null) client.setEmail(dto.getEmail());
        if (dto.getPhone() != null) client.setPhone(dto.getPhone());
        if (dto.getWhatsapp() != null) client.setWhatsapp(dto.getWhatsapp());
        if (dto.getDateOfBirth() != null) client.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getCountryOfCitizenship() != null) client.setCountryOfCitizenship(dto.getCountryOfCitizenship());
        if (dto.getCurrentLocation() != null) client.setCurrentLocation(dto.getCurrentLocation());
        if (dto.getCurrentStatus() != null) client.setCurrentStatus(dto.getCurrentStatus());
        if (dto.getMaritalStatus() != null) client.setMaritalStatus(dto.getMaritalStatus());
        if (dto.getPreferredLanguage() != null) client.setPreferredLanguage(dto.getPreferredLanguage());
        if (dto.getNotes() != null) client.setNotes(dto.getNotes());
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }
}
