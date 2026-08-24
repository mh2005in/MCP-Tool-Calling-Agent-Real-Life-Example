# ADR-001 — Keycloak is the identity provider; Microsoft Entra External ID is superseded

**Status:** Accepted
**Date:** 2026-08-21
**Decision:** `D-5`
**Requirements:** `DR-11`, `SEC-01`…`SEC-05`, `SEC-16`

## Context

Three source documents describe a Microsoft Entra External ID architecture:
`3.1 microsoft_entra_external_id_backend_frontend_mcp_plan.pdf`, the Phase-4 Hardening Plan, and all
three files in `Runbooks/`. They assume App Service managed identity, Azure Key Vault, Conditional
Access, and Application Insights.

**The repository ships self-hosted Keycloak.** `docker-compose.yml` runs a `keycloak` service against
its own Postgres database, [README.md](../../README.md) documents it, the SPA uses `keycloak-angular`,
the backend validates Keycloak-issued JWTs as an OAuth2 resource server, and MCP clients self-register
through Keycloak's RFC 7591 Dynamic Client Registration endpoint.

This is not a small divergence: it is the difference between a cloud-managed identity service and a
container the project operates itself. Leaving both descriptions in circulation meant every hardening
requirement had two contradictory readings, and the runbooks described operations against a system
that is not deployed.

## Options considered

| Option | Pros | Cons | Verdict |
| --- | --- | --- | --- |
| **Adopt Keycloak as the decision** | Matches what is deployed, documented, and working. No migration. Self-hosted keeps identity data in the tenant, consistent with `BR-5` | Operating an IdP is work the team owns — patching, backup, realm export. No managed Conditional Access equivalent | ✅ **Chosen** |
| Migrate to Entra as the documents specify | Managed service; Conditional Access and Application Insights are strong off-the-shelf controls | Discards a working integration. Ties the product to Azure before a deployment model exists (`D-2`). PII residency needs separate analysis | Rejected — no reason to migrate away from something that works |
| Support both behind an abstraction | Deployment flexibility | Two auth paths to secure and test, on a codebase with six test files. The seam is already clean (OIDC) — an abstraction adds nothing | Rejected — cost with no current buyer |

## Decision

**Keycloak is the production identity provider.** The Entra documents are historical records of a
plan that was not followed.

## Consequences

**What this makes easy**

- `SEC-01`…`SEC-05` restate cleanly. Control *intent* is provider-independent — MFA, step-up auth, refresh handling, secret management, and consent branding are required regardless — so only the mechanism changes.
- Azure-specific items map to provider-neutral equivalents: Key Vault → any secret store (`SEC-04`); Conditional Access → Keycloak required actions and authentication flows (`SEC-01`); Application Insights → any OpenTelemetry-compatible backend (`SEC-14`).
- Identity data stays inside the tenant, consistent with `BR-5`.

**What this makes hard**

- The team owns IdP operations. `SEC-16` (revocation runbook) has no vendor documentation to inherit — it must be written against Keycloak session revocation plus `DisabledUserFilter`.
- Conditional Access has no direct Keycloak equivalent. `SEC-01` and `SEC-02` become authentication-flow configuration rather than policy declarations.

**What this forecloses**

- Nothing permanently. The app-to-IdP boundary is a clean OIDC seam. **Reversing this decision** changes the wording of `SEC-01`…`SEC-05` and revives the runbooks; no application code beyond configuration is affected.

**Immediate effects**

- `DR-11` is `SUPERSEDED`.
- The three `Runbooks/` PDFs do not apply and are retained in [`../input/runbooks/`](../input/runbooks/) as historical record only — per the read-only rule, they are not edited to reflect this decision.
- Anonymous, consent-free DCR is a **development** posture. The removed policies must be re-enabled before production (`SEC-05`). Provisioning lives in `docker/keycloak/configure-dcr.sh`, **not** the realm import — a `clientScopes` array there suppresses Keycloak's built-in `roles` scope.
