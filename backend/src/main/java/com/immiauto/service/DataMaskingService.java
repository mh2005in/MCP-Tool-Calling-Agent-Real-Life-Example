package com.immiauto.service;

import com.immiauto.dto.mcp.MaskedClientDto;
import com.immiauto.dto.mcp.MaskedIntakeResponseDto;
import com.immiauto.entity.Client;
import com.immiauto.entity.IntakeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataMaskingService {

    private static final String MASKED = "***MASKED***";

    private final SensitiveFieldRegistry registry;

    public List<MaskedIntakeResponseDto> maskIntakeResponses(List<IntakeResponse> responses) {
        return responses.stream()
                .map(this::maskSingleResponse)
                .toList();
    }

    public Map<String, List<MaskedIntakeResponseDto>> maskAndGroupBySection(List<IntakeResponse> responses) {
        return maskIntakeResponses(responses).stream()
                .collect(Collectors.groupingBy(MaskedIntakeResponseDto::getSectionName));
    }

    private MaskedIntakeResponseDto maskSingleResponse(IntakeResponse response) {
        String maskedAnswer = registry.isSensitiveQuestionKey(response.getQuestionKey())
                ? maskValue(response.getQuestionKey(), response.getAnswer())
                : response.getAnswer();

        return MaskedIntakeResponseDto.builder()
                .sectionName(response.getSectionName())
                .questionKey(response.getQuestionKey())
                .questionLabel(response.getQuestionLabel())
                .answer(maskedAnswer)
                .sortOrder(response.getSortOrder())
                .flaggedForReview(response.isFlaggedForReview())
                .build();
    }

    private String maskValue(String questionKey, String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        return switch (questionKey) {
            case "full_name", "host_name", "applicant_name", "candidate_name",
                 "employer_legal_name", "employer_operating_name" -> maskName(value);
            case "dob" -> maskDateOfBirth(value);
            case "passport_number" -> maskIdentifier(value);
            case "cra_bn", "employer_cra_bn", "payroll_account",
                 "lmia_number", "police_report_number" -> maskIdentifier(value);
            case "insurance_policy_details" -> MASKED;
            default -> MASKED;
        };
    }

    private String maskName(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(".");
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }

    private String maskDateOfBirth(String dob) {
        try {
            LocalDate birthDate = LocalDate.parse(dob);
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            return age + "-" + (age + 4) + " years old";
        } catch (DateTimeParseException e) {
            return MASKED;
        }
    }

    private String maskIdentifier(String value) {
        if (value.length() <= 4) {
            return MASKED;
        }
        return "***" + value.substring(value.length() - 4);
    }

    public List<MaskedClientDto> maskClients(List<Client> clients) {
        return clients.stream().map(this::maskClient).toList();
    }

    public MaskedClientDto maskClient(Client client) {
        return MaskedClientDto.builder()
                .id(client.getId())
                .clientNumber(client.getClientNumber())
                .fullName(maskName(client.getFullName()))
                .email(maskEmail(client.getEmail()))
                .consultantId(client.getConsultant().getId())
                .phone(maskPhone(client.getPhone()))
                .whatsapp(maskPhone(client.getWhatsapp()))
                .dateOfBirth(client.getDateOfBirth() != null
                        ? maskDateOfBirth(client.getDateOfBirth().toString())
                        : null)
                .countryOfCitizenship(client.getCountryOfCitizenship())
                .currentLocation(client.getCurrentLocation())
                .currentStatus(client.getCurrentStatus())
                .passportNumber(client.getPassportNumber() != null
                        ? maskIdentifier(client.getPassportNumber())
                        : null)
                .maritalStatus(client.getMaritalStatus())
                .preferredLanguage(client.getPreferredLanguage())
                .notes(MASKED)
                .build();
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return MASKED;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        if (phone.length() <= 4) {
            return MASKED;
        }
        return "***" + phone.substring(phone.length() - 4);
    }
}
