package com.immiauto.security;

import java.util.UUID;

import com.immiauto.entity.AppUser;
import com.immiauto.entity.Consultant;
import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.ChecklistItemRepository;
import com.immiauto.repository.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Database-level authorization: ensures the authenticated user may act under a
 * given consultant account. OAuth token validation only proves identity;
 * this enforces that the user actually owns (or administers) the consultant/case.
 *
 * <p>Exposed as the {@code consultantAccess} bean for use in
 * {@code @PreAuthorize("@consultantAccess.canAccess(#consultantId)")} and
 * {@code @PreAuthorize("@consultantAccess.canAccessCase(#caseId)")}.
 */
@Component("consultantAccess")
@RequiredArgsConstructor
public class ConsultantAccessService {

    private final CurrentUserProvider currentUserProvider;
    private final ConsultantRepository consultantRepository;
    private final CaseRepository caseRepository;
    private final ChecklistItemRepository checklistItemRepository;

    public boolean canAccess(UUID consultantId) {
        if (consultantId == null) {
            return false;
        }
        AppUser user = currentUserProvider.getCurrentUser();
        if (user.getStatus() != AppUserStatus.ACTIVE) {
            return false;
        }
        if (user.getRole() == AppRole.PLATFORM_ADMIN) {
            return true;
        }
        if (consultantId.equals(user.getConsultantId())) {
            return true;
        }
        // An admin consultant may access other consultant accounts (mirrors existing ownership rules).
        if (user.getConsultantId() != null) {
            return consultantRepository.findById(user.getConsultantId())
                    .map(Consultant::isAdmin)
                    .orElse(false);
        }
        return false;
    }

    /**
     * Authorizes access to a resource addressed by case id (documents, checklist, reminders, intake)
     * by resolving the case's owning consultant and delegating to {@link #canAccess(UUID)}.
     * Prevents IDOR where any authenticated consultant could reach another consultant's case.
     */
    public boolean canAccessCase(UUID caseId) {
        if (caseId == null) {
            return false;
        }
        return caseRepository.findById(caseId)
                .map(c -> canAccess(c.getConsultant().getId()))
                .orElse(false);
    }

    /** Authorizes access to a checklist item by resolving its case -&gt; owning consultant. */
    public boolean canAccessChecklistItem(UUID itemId) {
        if (itemId == null) {
            return false;
        }
        return checklistItemRepository.findById(itemId)
                .map(item -> canAccess(item.getImmigrationCase().getConsultant().getId()))
                .orElse(false);
    }
}

