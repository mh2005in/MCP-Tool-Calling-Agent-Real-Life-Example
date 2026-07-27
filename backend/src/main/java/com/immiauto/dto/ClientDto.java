package com.immiauto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientDto {
    private Long id;
    private String clientNumber;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email @NotBlank(message = "Email is required")
    private String email;

    private Long consultantId;

    private String phone;
    private String whatsapp;
    private LocalDate dateOfBirth;
    private String countryOfCitizenship;
    private String currentLocation;
    private String currentStatus;
    private String passportNumber;
    private String maritalStatus;
    private String preferredLanguage;
    private String notes;
}
