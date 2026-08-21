# Architecture

> **How the system is built today, and what it must become to satisfy [Requirements.md](Requirements.md).**
> [README.md](../README.md) remains the source of truth for *running* the stack — ports, env vars, and
> setup steps are **not** restated here (CLAUDE.md §12). This document covers structure and decisions.

**Last updated:** 2026-08-21
**Identity decision:** Keycloak. Microsoft Entra External ID is **superseded** — see §7.
**Decision records and technical designs:** [`design/`](design/) — `ADR-nnn-<slug>.md`

---

## 1. As-built: runtime topology

Seven containers on one Docker network, addressing each other by compose service name. Every host,
port, and URL comes from `.env` (CLAUDE.md §15) — nothing is hardcoded.

```
                                  ┌──────────────────────────────┐
   Browser ───────────────────────►  frontend (nginx)            │
                                  │  · serves the Angular 18 SPA  │
                                  │  · renders window.__env       │
                                  │  · proxies /api ──► backend   │
                                  └───────────────┬──────────────┘
                                                  │ internal network
                   ┌──────────────────────────────▼──────────────────────────────┐
                   │  backend (Spring Boot 3.3 / Java 21)   context-path /api     │
                   │  clients · cases · intake · checklists · documents           │
                   │  forms & package automation · reminders · expiry alerts      │
                   │  audit · AI boundary + masking                               │
                   └──┬────────────────┬─────────────────────┬───────────────────┘
                      │                │                     │
        ┌─────────────▼──────┐  ┌──────▼─────────┐  ┌────────▼──────────────┐
        │ postgres           │  │ keycloak       │  │ mcpserver             │
        │ immiauto_db schema │◄─┤ realm immiauto │  │ MCP tools over OAuth   │
        │ + keycloak db      │  │ OIDC + DCR     │  │ audit ──► backend      │
        └────────────────────┘  └────────────────┘  └────────┬──────────────┘
                                                             │
                                        ┌────────────────────▼─────────────────┐
                                        │ librechat (chat UI, MongoDB-backed)  │
                                        │        └──► ollama (local LLM)       │
                                        └──────────────────────────────────────┘
```

**Why it is shaped this way**

- **The SPA never calls Keycloak's internal URL.** Browser-facing URLs use `*_PUBLIC_URL`; service-to-service calls use `*_INTERNAL_URL`. This avoids the localhost-vs-service-name token issuer mismatch that otherwise breaks JWT validation.
- **nginx renders `window.__env` via `envsubst` at container start.** Host and API changes need no frontend rebuild, so no URL is ever compiled into `environment*.ts`.
- **The LLM tier is local.** Ollama runs the model on-box, satisfying **BR-5** (client PII does not leave the tenant). This is an architectural commitment, not a cost optimisation.

---

## 2. As-built: backend layering

Standard Spring layering, enforced by CLAUDE.md §4–§5.

```
controller/   17 controllers   @PreAuthorize (×22) · @Valid (×22) · DTOs only, never entities
    │
mapper/       MapStruct         DTO ↔ entity, both directions (§4) — no hand-rolled builders
    │
service/      30 services       business rules; shared logic hoisted to CommonService/CommonUtil (§5)
    │
repository/   Spring Data       derived queries; no native SQL (keeps SQL-injection risk low)
    │
entity/       22 entities       BaseEntity holds the UUID id (DB-generated)
```

### 2.1 Cross-cutting components

| Concern | Component | Notes |
| --- | --- | --- |
| Identity | `security/CurrentUserProvider` | Consultant identity derives from the **authenticated principal**, never a path variable |
| Case authorization | `security/ConsultantAccessService` | `@consultantAccess.canAccessCase(#caseId)` |
| Admin authorization | `security/AdminAccessService` | `@adminGuard.isAdminConsultant()` |
| Immediate revocation | `security/DisabledUserFilter` | Cuts off a disabled consultant on the next request, without waiting for token expiry |
| Role mapping | `security/JwtAuthoritiesConverter` | Keycloak realm roles → Spring authorities |
| Audit | `CommonService.logAudit` | 18 call sites; compact JSON detail (`GAP-09` wants richer structure) |
| AI safety | `AiBoundaryService`, `DataMaskingService`, `DocumentMetadataSanitizer`, `SensitiveFieldRegistry` | The **BR-1/BR-5** enforcement layer |
| Errors | `exception/GlobalExceptionHandler` | 404 for unmatched routes, 403 for authorization denials — never a leaked 500 |

### 2.2 Persistence conventions (CLAUDE.md §7)

- **Primary keys are database-generated GUIDs.** `id uuid NOT NULL DEFAULT gen_random_uuid()`, mapped on `BaseEntity` as read-only (`@Generated(event = INSERT)` + `@ColumnDefault`). **Never** an app-side sequence or `@GeneratedValue`.
- **Human-facing identifiers are derived from the UUID** via a Postgres `GENERATED ALWAYS AS (...) STORED` column, mapped read-only. Not a numeric sequence, not a `@PrePersist` format string.
- **No length constraints on entity fields** — currently violated in three places (`DR-03`).

### 2.3 Migrations

`backend/src/main/resources/db/migration/{postgresql,mysql,mssql,oracle}/`. Flyway is **disabled**;
migrations are applied manually against `immiauto_db`.

| Version | Scope | PostgreSQL | MySQL / MSSQL / Oracle |
| --- | --- | --- | --- |
| V1–V2 | Core schema + seed data | ✅ | ✅ |
| V3–V5 | App users, MCP audit log, drop MCP API key | ✅ | ❌ frozen |
| V6–V9 | Form & package automation, seed, sample fill, draft origin | ✅ | ❌ frozen |

**Consequence:** the project is effectively PostgreSQL-only. Decision `D-4` should make that explicit
or fund the backfill — carrying three dead dialects taxes every schema change.

---

## 3. As-built: the forms & package automation domain

The newest and most valuable subsystem (`GAP-01` / `F41-*`). It is deliberately **content-driven**:
forms are governed data, not hard-coded UI.

```
FormDefinition ──< FormFieldDefinition
      │                    │
      │              FormFieldMapping >── CanonicalDataField
      │                    │
      └──< FormMappingVersion (DRAFT → APPROVED → SUPERSEDED)

PackageProfile ──< PackageProfileForm ──► FormDefinition
      │        └──< PackageDocumentRequirement
      │
CasePackage ──< CaseFormDraft (origin: GENERATED | UPLOADED)
      └──< PackageValidationIssue
```

**Service responsibilities**

| Service | Responsibility |
| --- | --- |
| `FormCatalogueService` | Governed form registry — source URL, edition, SHA-256, status, inspect |
| `CanonicalApplicantDataService` | Projects case/client/intake data into canonical fields — the anti-rekeying layer |
| `FormMappingService` | Canonical field → PDF field, with transforms and versioning |
| `CaseFormGenerationService` | Fills a draft; skips forms where `supportsFill = false` |
| `PackageValidationService` | Deterministic readiness rules; severity-classified issues |
| `CasePackageService` | Assembles index/manifest; approval gate |
| `FormStorageService` | Generated-artifact storage with hashes |
| `pdf/PdfFormEngine` | **SPI** — `PdfBoxFormEngine` is the only implementation today |

### 3.1 The XFA constraint — the single most important architectural fact

`PdfFormEngine` is an interface for a reason. Apache PDFBox is an **AcroForm** engine with no XFA
layout engine, and the high-volume IRCC forms (IMM 0008, 5257, 5645, 5669, 5406, 5532, 1294, 1295)
are **dynamic XFA, encrypted, certified, and Reader-Extended**.

The 2026-06-29 proof of concept established, headlessly and then in Adobe:

1. Datasets injection into the `xfa:data` node **works** — values are present on re-read.
2. `saveIncremental()` preserves the original bytes, encryption, and usage rights.
3. **But both outputs fail to open in Adobe.** PDFBox has open defects writing encrypted incremental updates ([PDFBOX-3188](https://issues.apache.org/jira/browse/PDFBOX-3188), [PDFBOX-4286](https://issues.apache.org/jira/browse/PDFBOX-4286)); a full save decrypts and rewrites, breaking certification and dynamic XFA.

**Therefore:** pure PDFBox can inject XFA data but **cannot produce an Adobe-valid filled IRCC form**.

Architectural response — three layers, in this order:

| Layer | Mechanism | Requirement |
| --- | --- | --- |
| 1. Classify honestly | Inspect every form; mark dynamic-XFA and barcode forms `BLOCKED`. **Never** emit a blank-but-"successful" PDF | `F41-01` |
| 2. Degrade usefully | Data-sheet fallback — a mapped-values sheet the consultant transcribes into Adobe, which handles barcode and certification natively | `F41-02` |
| 3. Escalate only if funded | A second `PdfFormEngine` bean (Aspose.PDF / Qoppa / iText 7 commercial), selected by `formTechnology`. **No caller changes** — the SPI already isolates this | `F41-14`, decision `D-1` |

Mappings must store an explicit **`xfaDataPath`** (`form1.Page1.PersonalDetails.Name.FamilyName`),
never a local field name: the PoC found duplicate local names and same-name wrapper leaves
(`<PassportNum><PassportNum/></PassportNum>`) that make local-name matching unsafe.

---

## 4. As-built: the MCP / AI tier

```
LibreChat ──OAuth──► mcpserver ──OBO──► backend /v1/mcp/**
                         │                     │
                         └── audit ────────────┘   (Keycloak service-account token)
     └──► ollama (local model, no PII egress)
```

- **Dynamic Client Registration (RFC 7591)** lets MCP clients self-register. Provisioning lives in `docker/keycloak/configure-dcr.sh`, **not** the realm import — a `clientScopes` array there suppresses Keycloak's built-in `roles` scope.
- **Anonymous/consent-free DCR is a development posture.** The removed policies must be re-enabled for production.
- **Every tool call is audited**, and every response crosses the masking boundary before leaving the backend.
- Tool definitions are data: `MCPServer/src/main/resources/config/tools.json`.

---

## 5. Target architecture

From Phase 5 §5. The gap between §1–§4 and this section **is** the `GAP-*` backlog.

| Layer | Target | Why | Requirement |
| --- | --- | --- | --- |
| **Experience** | Separate staff workspace, authenticated client portal, narrow token-upload flow, public lead/booking surfaces, admin/content operations | Different identities and risk profiles need different authorization and UX | `GAP-06`, `GAP-11` |
| **Domain services** | Modules for case, party, intake, forms, documents, tasks, communications, calendar, billing, signature, knowledge, reporting, identity/tenant | Clear ownership reduces cross-feature coupling and permits staged extraction | `GAP-02`…`GAP-08` |
| **Canonical data** | Reusable person, family, address, employment, education, travel, immigration, identity-document, organization records **with provenance** | Eliminates rekeying; enables cross-form validation | `GAP-01` — partially built |
| **Events** | Transactional outbox + versioned domain events; idempotent consumers; retries and dead-letter operations | Reliable reminders, webhooks, integrations, analytics, workflow triggers | `GAP-12` |
| **Files** | Immutable originals **plus** derived previews/OCR/redactions/packages; checksums, encryption, signed access, malware quarantine, lifecycle policies | Preserves evidentiary integrity — **never overwrite the original** | `GAP-07`, `SEC-08` |
| **Authorization** | Tenant, role, relationship, case, party, and object-level policies evaluated **server-side**; audit every sensitive access | URL scoping alone is insufficient for regulated multi-tenant data | `GAP-13`, `SEC-07` |
| **Content** | Effective-dated forms/rules/templates with sources, review, publication, impact analysis, rollback, regression cases | Immigration content changes independently of application code | `GAP-09` |
| **Observability** | Correlated request, user, tenant, case, job, integration, and AI-operation telemetry with sensitive-data filtering | Required to operate long-running workflows and produce audit evidence | `SEC-14` |

### 5.1 The two structural moves that unblock the most

1. **Transactional outbox + domain events.** `GAP-04`, `GAP-05`, `GAP-08`, `GAP-10`, and `GAP-12` all need reliable "when X happened, do Y". Without an outbox, each becomes point-to-point logic embedded in controllers — exactly what Phase 5 §4.12 warns against. **Build this before the second integration, not after the fifth.**
2. **Structural tenant scoping.** `GAP-13` asks for a tenant boundary on every record, query, cache key, file path, job, event, export, and log context. Retrofitting that across 22 entities and 30 services costs far more than designing it in. Phase 5 is explicit: *make tenant ID structurally unavoidable in repositories and storage*.

---

## 6. Architectural rules

Binding constraints. A change that violates one needs an explicit decision recorded in
[change.log.md](change.log.md), not a workaround.

1. **Configuration flows through one chain** — base var in `.env`/`.env.example` → derived in `docker-compose.yml` → consumed as `${ENV:default}` (Spring) or `window.__env` (Angular). Never a literal host, port, or URL in a config file (CLAUDE.md §15).
2. **Services address each other by compose service name**, never `localhost` or a published host port.
3. **DTOs at the boundary, entities never.** MapStruct in both directions (CLAUDE.md §4).
4. **Reuse before writing.** Check `CommonUtil` and `CommonService`; if the method lives in another service, *move it* there rather than duplicating (CLAUDE.md §5).
5. **One way to do a thing.** If the established pattern is wrong, propose changing it — do not add a parallel one (CLAUDE.md §10).
6. **Provider-neutral domain models.** Signature envelopes, payment intents, and message records are ours; vendor adapters are replaceable. A vendor change must not lose history.
7. **Immutable evidentiary originals.** Derived artifacts are separate objects.
8. **Every generated artifact is traceable** to a form version, mapping version, input snapshot, output hash, and approver.
9. **Edit the source of truth, never a rendered copy** — `configure-dcr.sh` and `realm-immiauto.json.template`, not a rendered realm (CLAUDE.md §12).

---

## 7. Decision record: Keycloak over Microsoft Entra External ID

**Status:** Accepted 2026-08-21 (decision `D-5`). Supersedes `HighLevelRequirement Completed/3.1 microsoft_entra_external_id_backend_frontend_mcp_plan.pdf` and all three files in `Runbooks/`.

**Context.** Docs 3.1 and the Phase-4 Hardening Plan describe a Microsoft Entra External ID architecture with App Service managed identity, Azure Key Vault, Conditional Access, and Application Insights. The repository ships a self-hosted **Keycloak** container: `docker-compose.yml` runs it, `README.md` documents it, the SPA uses `keycloak-angular`, and the backend validates Keycloak-issued JWTs.

**Decision.** Keycloak is the identity provider. The Entra documents are historical.

**Consequences.**

- `SEC-01`…`SEC-05` are restated in Keycloak terms in [Requirements.md](Requirements.md) §6. **The control intent is unchanged** — MFA, step-up auth, refresh handling, secret management, and consent branding are all required regardless of provider.
- The three `Runbooks/` PDFs describe operations against Entra. They do not apply. `SEC-16` calls for a Keycloak-based revocation runbook to replace them.
- Azure-specific items map onto provider-neutral equivalents: Key Vault → any secret store (`SEC-04`); Conditional Access → Keycloak required actions and authentication flows (`SEC-01`); Application Insights → any OpenTelemetry-compatible backend (`SEC-14`).
- **If this decision is reversed**, `SEC-01`…`SEC-05` revert to their original wording and the runbooks become live again. Nothing else in this document changes — the boundary between the app and the identity provider is a clean OIDC seam.

---

## 8. Known architectural debt

Ordered by how much future work each one taxes.

| # | Debt | Cost of leaving it | Requirement |
| --- | --- | --- | --- |
| 1 | **No tenant boundary.** Isolation rests on consultant scoping | Retrofit cost grows with every entity added | `GAP-13`, `SEC-07` |
| 2 | **No event/outbox layer.** Cross-feature reactions would be controller-coupled | Each new integration adds point-to-point logic | `GAP-12` |
| 3 | **6 test files** for ~30 services and 17 controllers | Every refactor is unverifiable; blocks safe change | `DR-10` |
| 4 | **Three controllers route to `/api/api/…`** and bypass the `/v1/**` matchers | Broken endpoints plus a security-rule gap | `DR-04` |
| 5 | **Multi-DB dialects frozen at V2** | Three dead dialects tax every schema change | `D-4` |
| 6 | **No pagination anywhere** | DoS and bulk-exposure vector on every list endpoint | `DR-07` |
| 7 | **`show-sql=true` in the main profile** | Bound PII in logs in every environment | `DR-08` |
| 8 | **Audit detail is an unstructured JSON string** | Compliance exports (`GAP-10`) cannot query it | `GAP-09` |
| 9 | **Party portal tokens never expire** | Permanent unauthenticated access to case data | `DR-05` |

---

## 9. Keeping this document honest

Per CLAUDE.md §12 and §14, architecture changes are not done until the docs move with them:

- A new service or dependency → add it as a **compose service**, wire `depends_on` and env, update [README.md](../README.md), and update the `deploy-verify` agent (it hardcodes service names, ports, and health endpoints).
- A new runtime config value → `.env` + `.env.example` + `docker-compose.yml` + the consuming layer.
- A change to the layering or module boundaries → update §2/§5 here **in the same commit**.
- Any decision that overrides §6 → record it in [change.log.md](change.log.md) with its rationale.
