package com.immiauto.service;

import com.immiauto.entity.AppUser;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.repository.AppUserRepository;
import com.immiauto.repository.ConsultantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProvisioningServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private ConsultantRepository consultantRepository;

    @InjectMocks
    private UserProvisioningService service;

    private static Jwt.Builder jwt() {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300));
    }

    @Test
    void serviceAccountTokenIsNotProvisioned() {
        // Keycloak client-credentials token: no email, preferred_username = service-account-<client>.
        Jwt token = jwt()
                .subject("2f1c8d5e-service-sub")
                .claim("preferred_username", "service-account-immiauto-mcp")
                .build();

        AppUser result = service.resolve(token);

        assertThat(result.getRole()).isEqualTo(AppRole.SERVICE_ACCOUNT);
        assertThat(result.getStatus()).isEqualTo(AppUserStatus.ACTIVE);
        assertThat(result.getConsultantId()).isNull();
        assertThat(result.getExternalSubject()).isEqualTo("2f1c8d5e-service-sub");
        // A machine identity must never be looked up or persisted as a user, nor create a consultant.
        verifyNoInteractions(appUserRepository);
        verify(consultantRepository, never()).save(any());
        verify(consultantRepository, never()).findByEmail(any());
    }

    @Test
    void humanUserLinksToExistingConsultantByEmail() {
        Jwt token = jwt()
                .subject("user-sub-123")
                .claim("preferred_username", "demo")
                .claim("email", "demo@immiauto.ca")
                .claim("name", "Demo Consultant")
                .build();

        UUID consultantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Consultant seedConsultant = Consultant.builder().email("demo@immiauto.ca").build();
        seedConsultant.setId(consultantId);

        when(appUserRepository.findByExternalSubject("user-sub-123")).thenReturn(Optional.empty());
        when(consultantRepository.findByEmail("demo@immiauto.ca")).thenReturn(Optional.of(seedConsultant));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        AppUser result = service.resolve(token);

        assertThat(result.getRole()).isEqualTo(AppRole.CONSULTANT_OWNER);
        assertThat(result.getStatus()).isEqualTo(AppUserStatus.ACTIVE);
        assertThat(result.getConsultantId()).isEqualTo(consultantId);
        // Linked to an existing consultant, so none is created.
        verify(consultantRepository, never()).save(any());
    }
}
