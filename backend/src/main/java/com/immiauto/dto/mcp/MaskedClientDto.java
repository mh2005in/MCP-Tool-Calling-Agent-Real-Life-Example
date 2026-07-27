package com.immiauto.dto.mcp;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MaskedClientDto {

    private Long id;
    private String clientNumber;
    private String fullName;
    private String email;
    private Long consultantId;
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
