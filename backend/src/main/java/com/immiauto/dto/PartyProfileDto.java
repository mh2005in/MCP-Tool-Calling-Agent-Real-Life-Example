package com.immiauto.dto;

import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PartyProfileDto {
    private UUID id;
    private UUID caseId;
    private String partyType;
    private String fullName;
    private String email;
    private String phone;
    private String relationship;
    private String organization;
    private boolean portalEnabled;
    private String accessToken;
    private String notes;
}
