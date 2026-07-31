package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.ConsultantDto;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.enums.CaseStatus;
import com.immiauto.exception.DuplicateEmailException;
import com.immiauto.mapper.ConsultantMapper;
import com.immiauto.repository.AppUserRepository;
import com.immiauto.repository.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultantService {

    private final ConsultantRepository consultantRepository;
    private final AppUserRepository appUserRepository;
    private final ConsultantMapper consultantMapper;

    @Transactional(readOnly = true)
    public List<ConsultantDto> getAllConsultants() {
        return consultantRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsultantDto getConsultant(UUID id) {
        return toDto(findById(id));
    }

    @Transactional(readOnly = true)
    public ConsultantDto getOwnProfile(UUID id) {
        return toDto(findById(id));
    }

    @Transactional(readOnly = true)
    public ConsultantDto getConsultantByNumber(String consultantNumber) {
        Consultant consultant = consultantRepository.findByConsultantNumber(consultantNumber)
                .orElseThrow(() -> new RuntimeException("Consultant not found with number: " + consultantNumber));
        return toDto(consultant);
    }

    @Transactional
    public ConsultantDto createConsultant(ConsultantDto dto) {
        if (consultantRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(
                    "A consultant with email '" + dto.getEmail() + "' already exists");
        }

        Consultant consultant = Consultant.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .licenseNumber(dto.getLicenseNumber())
                .companyName(dto.getCompanyName())
                .admin(dto.isAdmin())
                .active(true)
                .build();

        return toDto(consultantRepository.save(consultant));
    }

    @Transactional
    public ConsultantDto updateConsultant(UUID id, ConsultantDto dto) {
        Consultant consultant = findById(id);

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(consultant.getEmail())) {
            if (consultantRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateEmailException(
                        "A consultant with email '" + dto.getEmail() + "' already exists");
            }
        }

        consultant.setFullName(dto.getFullName());
        consultant.setEmail(dto.getEmail());
        consultant.setPhone(dto.getPhone());
        consultant.setLicenseNumber(dto.getLicenseNumber());
        consultant.setCompanyName(dto.getCompanyName());
        consultant.setAdmin(dto.isAdmin());
        consultant.setActive(dto.isActive());

        return toDto(consultantRepository.save(consultant));
    }

    /**
     * Enables/disables a consultant's login. Disabling also sets the linked app_users to DISABLED so
     * the user is blocked on every request (see DisabledUserFilter) even with an unexpired token.
     */
    @Transactional
    public ConsultantDto setActive(UUID id, boolean active) {
        Consultant consultant = findById(id);
        consultant.setActive(active);
        consultantRepository.save(consultant);

        AppUserStatus status = active ? AppUserStatus.ACTIVE : AppUserStatus.DISABLED;
        appUserRepository.findByConsultantId(id).forEach(user -> {
            user.setStatus(status);
            appUserRepository.save(user);
        });

        return toDto(consultant);
    }

    private Consultant findById(UUID id) {
        return consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found: " + id));
    }

    private ConsultantDto toDto(Consultant entity) {
        ConsultantDto dto = consultantMapper.toDto(entity);
        dto.setActiveCaseCount(countActiveCases(entity));
        return dto;
    }

    private int countActiveCases(Consultant entity) {
        return entity.getCases() != null ? (int) entity.getCases().stream()
                .filter(c -> c.getCaseStatus() != null && c.getCaseStatus() != CaseStatus.CLOSED)
                .count() : 0;
    }
}
