package com.immiauto.dto;

import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Profile of the currently authenticated user, returned by GET /v1/me.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeDto {
    private Long id;
    private String email;
    private String displayName;
    private AppRole role;
    private AppUserStatus status;
    private Long consultantId;
    /** Whether the linked consultant is an org admin (drives the "My Organization" section on the frontend). */
    private boolean consultantAdmin;
    private String consultantName;
    private String companyName;
}
