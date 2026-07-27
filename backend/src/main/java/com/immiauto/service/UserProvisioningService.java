package com.immiauto.service;

import com.immiauto.entity.AppUser;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.repository.AppUserRepository;
import com.immiauto.repository.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves a Microsoft Entra token into a local {@link AppUser}, creating the mapping on
 * first sign-in (just-in-time provisioning).
 *
 * <p>Provisioning resolves the consultant link from the user's email:
 * <ol>
 *   <li>A {@link Consultant} with a matching email -> link as CONSULTANT_OWNER (ACTIVE).</li>
 *   <li>Else, if auto-provisioning is enabled -> create a new Consultant + link (ACTIVE).</li>
 *   <li>Else (e.g. no email in the token) -> PENDING CLIENT with no consultant link (no access).</li>
 * </ol>
 *
 * <p>A previously PENDING/unlinked user is <b>self-healed</b> on a later sign-in once an email
 * becomes resolvable (e.g. after adding the email/upn optional claim), so no manual cleanup is needed.
 */
@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final AppUserRepository appUserRepository;
    private final ConsultantRepository consultantRepository;

    /** When true, an unknown authenticated user becomes a brand-new consultant on first login (self-serve MVP). */
    @Value("${app.security.auto-provision-consultant.enabled:true}")
    private boolean autoProvisionConsultant;

    @Transactional
    public AppUser resolve(Jwt jwt) {
        // Machine identities (Keycloak client-credentials service accounts) are not consultants:
        // never provision or persist them, just return a transient authenticated principal with no
        // consultant workspace. This avoids creating junk consultant rows (and the /me 500 that a
        // service account otherwise triggers). See isServiceAccount().
        if (isServiceAccount(jwt)) {
            return serviceAccount(jwt);
        }
        String externalSubject = extractSubject(jwt);
        AppUser existing = appUserRepository.findByExternalSubject(externalSubject).orElse(null);
        if (existing == null) {
            return provision(jwt, externalSubject);
        }
        // A DISABLED user stays disabled (do not re-activate via heal).
        if (existing.getStatus() == AppUserStatus.DISABLED) {
            return existing;
        }
        // Already linked and active -> use as-is. Otherwise try to heal it.
        if (existing.getStatus() == AppUserStatus.ACTIVE && existing.getConsultantId() != null) {
            return existing;
        }
        return heal(jwt, existing);
    }

    private AppUser provision(Jwt jwt, String externalSubject) {
        String email = extractEmail(jwt);
        String name = jwt.getClaimAsString("name");
        Long consultantId = resolveOrCreateConsultantId(email, name);

        return appUserRepository.save(AppUser.builder()
                .externalSubject(externalSubject)
                .email(email)
                .displayName(name)
                .tenantId(jwt.getClaimAsString("tid"))
                .role(consultantId != null ? AppRole.CONSULTANT_OWNER : AppRole.CLIENT)
                .status(consultantId != null ? AppUserStatus.ACTIVE : AppUserStatus.PENDING)
                .consultantId(consultantId)
                .build());
    }

    /** Upgrades a PENDING/unlinked user to an active consultant once an email becomes resolvable. */
    private AppUser heal(Jwt jwt, AppUser existing) {
        String email = extractEmail(jwt);
        String name = jwt.getClaimAsString("name");
        Long consultantId = resolveOrCreateConsultantId(email, name);
        if (consultantId == null) {
            return existing; // still nothing to link to -> remains PENDING
        }
        if (email != null) {
            existing.setEmail(email);
        }
        if (name != null) {
            existing.setDisplayName(name);
        }
        existing.setConsultantId(consultantId);
        existing.setRole(AppRole.CONSULTANT_OWNER);
        existing.setStatus(AppUserStatus.ACTIVE);
        return appUserRepository.save(existing);
    }

    /** Finds a consultant by email, or creates one when auto-provisioning is enabled. Null if no email. */
    private Long resolveOrCreateConsultantId(String email, String name) {
        if (email == null) {
            return null;
        }
        Consultant consultant = consultantRepository.findByEmail(email).orElse(null);
        if (consultant != null) {
            return consultant.getId();
        }
        if (autoProvisionConsultant) {
            Consultant created = consultantRepository.save(Consultant.builder()
                    .fullName(name != null ? name : email)
                    .email(email)
                    .admin(false)
                    .active(true)
                    .build());
            return created.getId();
        }
        return null;
    }

    /**
     * True for a Keycloak service-account (client-credentials) token. Keycloak names these
     * {@code service-account-<clientId>} and they carry no human email claim.
     */
    private boolean isServiceAccount(Jwt jwt) {
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        boolean serviceAccountName = preferredUsername != null
                && preferredUsername.startsWith("service-account-");
        boolean noEmail = jwt.getClaimAsString("email") == null;
        return serviceAccountName && noEmail;
    }

    /** A transient (never persisted) principal for a machine identity: authenticated, no consultant. */
    private AppUser serviceAccount(Jwt jwt) {
        return AppUser.builder()
                .externalSubject(extractSubject(jwt))
                .displayName(jwt.getClaimAsString("preferred_username"))
                .role(AppRole.SERVICE_ACCOUNT)
                .status(AppUserStatus.ACTIVE)
                .consultantId(null)
                .build();
    }

    private String extractSubject(Jwt jwt) {
        String oid = jwt.getClaimAsString("oid");
        return (oid != null && !oid.isBlank()) ? oid : jwt.getSubject();
    }

    /**
     * Resolves the user's email from the token, trying the claims most likely to hold it.
     * Access tokens often omit {@code email}, so fall back to {@code preferred_username} and the
     * UPN ({@code upn} / {@code unique_name}), which is typically the user's email.
     */
    private String extractEmail(Jwt jwt) {
        for (String claim : new String[] {"email", "preferred_username", "upn", "unique_name"}) {
            String value = jwt.getClaimAsString(claim);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
