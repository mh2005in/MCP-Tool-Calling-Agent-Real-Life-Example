package com.immiauto.security;

import com.immiauto.entity.AppUser;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.repository.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Authorization for organization / cross-consultant admin endpoints (org dashboard,
 * consultant management) that have no {@code {consultantId}} path variable.
 *
 * <p>Exposed as the {@code adminGuard} bean for
 * {@code @PreAuthorize("@adminGuard.isAdminConsultant()")}.
 */
@Component("adminGuard")
@RequiredArgsConstructor
public class AdminAccessService {

    private final CurrentUserProvider currentUserProvider;
    private final ConsultantRepository consultantRepository;

    /** @return true if the current user is a platform admin or maps to a consultant with the admin flag. */
    public boolean isAdminConsultant() {
        AppUser user = currentUserProvider.getCurrentUser();
        if (user.getStatus() != AppUserStatus.ACTIVE) {
            return false;
        }
        if (user.getRole() == AppRole.PLATFORM_ADMIN) {
            return true;
        }
        if (user.getConsultantId() == null) {
            return false;
        }
        return consultantRepository.findById(user.getConsultantId())
                .map(Consultant::isAdmin)
                .orElse(false);
    }
}
