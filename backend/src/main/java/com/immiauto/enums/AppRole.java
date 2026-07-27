package com.immiauto.enums;

/**
 * Application-level roles for users authenticated via the OIDC provider (Keycloak).
 * These are internal business roles - distinct from OAuth scopes which gate API/tool categories.
 */
public enum AppRole {
    PLATFORM_ADMIN("Platform Admin"),
    CONSULTANT_OWNER("Consultant Owner"),
    CONSULTANT_STAFF("Consultant Staff"),
    CLIENT("Client"),
    /** Non-human machine identity (e.g. the MCP server's client-credentials service account). */
    SERVICE_ACCOUNT("Service Account");

    private final String displayName;

    AppRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
