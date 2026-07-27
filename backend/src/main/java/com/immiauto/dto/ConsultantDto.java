package com.immiauto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantDto {

    private Long id;
    private String consultantNumber;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String licenseNumber;
    private String companyName;
    private boolean admin;
    private boolean active;
    private int activeCaseCount;
    private LocalDateTime createdAt;
}
