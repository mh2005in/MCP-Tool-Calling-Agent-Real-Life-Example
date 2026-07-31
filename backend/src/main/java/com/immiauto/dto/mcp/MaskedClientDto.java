package com.immiauto.dto.mcp;

import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MaskedClientDto {

    private UUID id;
    private String clientNumber;
    private String fullName;
    private String email;
    private UUID consultantId;
    private String phone;
    private String whatsapp;
    private String dateOfBirth;
    private String countryOfCitizenship;
    private String currentLocation;
    private String currentStatus;
    private String passportNumber;
    private String maritalStatus;
    private String preferredLanguage;
    private String notes;
}
