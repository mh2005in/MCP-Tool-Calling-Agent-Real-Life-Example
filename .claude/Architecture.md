# Architecture

> **How the system is built today, and what it must become to satisfy [Requirements.md](Requirements.md).**
> [README.md](../README.md) remains the source of truth for *running* the stack — ports, env vars, and
> setup steps are **not** restated here (CLAUDE.md §12). This document covers structure and decisions.

**Last updated:** 2026-08-24
**Last verified against code:** 2026-08-24 — [`progress/2026-08-24-verification-run.md`](progress/2026-08-24-verification-run.md)
**Identity decision:** Keycloak. Microsoft Entra External ID is **superseded** — [ADR-001](design/ADR-001-keycloak-over-entra.md).
**Decision records and technical designs:** [`design/`](design/) — `ADR-nnn-<slug>.md`

---

## 1. As-built: runtime topology

Eleven compose services on one Docker network, addressing each other by compose service name. Every
host, port, and URL comes from `.env` (CLAUDE.md §15) — nothing is hardcoded.

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
        │ immiauto_db schema │◄─┤ realm immiauto │  │ 14 MCP tools / OAuth  │
        │ + keycloak db      │  │ OIDC + DCR     │  │ audit ──► backend     │
        └────────────────────┘  └────────────────┘  └────────┬──────────────┘
                                                             │
                                        ┌────────────────────▼─────────────────┐
                                        │ librechat (chat UI, MongoDB-backed)  │
                                        │        └──► ollama (local LLM)       │
                                        └──────────────────────────────────────┘

  init containers: realm-init · keycloak-config · ollama-init   (run once, then exit)
```

**Why it is shaped this way**

- **The SPA never calls Keycloak's internal URL.** Browser-facing URLs use `*_PUBLIC_URL`; service-to-service calls use `*_INTERNAL_URL`. This avoids the localhost-vs-service-name token issuer mismatch that otherwise breaks JWT validation.
- **nginx renders `window.__env` via `envsubst` at container start.** Host and API changes need no frontend rebuild, so no URL is ever compiled into `environment*.ts`.
- **The LLM tier is local.** Ollama runs the model on-box, satisfying **BR-5** (client PII does not leave the tenant). This is an architectural commitment, not a cost optimisation.
- **Three init containers do provisioning, not runtime work.** `realm-init` renders the realm template, `keycloak-config` runs DCR provisioning, `ollama-init` pulls the model. They exit; nothing depends on them staying up.

---

## 2. As-built: backend layering

Standard Spring layering, enforced by CLAUDE.md §4–§5. **Counts verified 2026-08-24.**

```
controller/   17 controllers   @PreAuthorize (×26) · @Valid (×22) · DTOs only, never entities
    │
mapper/       17 MapStruct     DTO ↔ entity, both directions (§4) — no hand-rolled builders
    │
service/      37 services      27 core · 8 forms · 2 security
    │                          business rules; shared logic hoisted to CommonService/CommonUtil (§5)
repository/   31 Spring Data   derived queries; no native SQL (keeps SQL-injection risk low)
    │
entity/       32 entities      21 core · 11 forms — BaseEntity holds the UUID id (DB-generated)
```

45 DTOs, 17 enums. The Angular SPA is 27 components across 10 feature areas, reaching the API through
a single `api.service` with 121 HTTP calls.

### 2.1 Cross-cutting components

| Concern | Component | Notes |
| --- | --- | --- |
| Identity | `security/CurrentUserProvider` | Consultant identity derives from the **authenticated principal**, never a path variable |
| Case authorization | `security/ConsultantAccessService` | `@consultantAccess.canAccessCase(#caseId)` |
| Admin authorization | `security/AdminAccessService` | `@adminGuard.isAdminConsultant()` |
| Immediate revocation | `security/DisabledUserFilter` | Cuts off a disabled consultant on the next request, without waiting for token expiry |
| Role mapping | `security/JwtAuthoritiesConverter` | Keycloak realm roles → Spring authorities |
| Audit | `CommonService.logAudit` | 18 call sites, all **domain** events; compact JSON detail. No security events (`SEC-09`); unstructured detail blocks compliance export (`GAP-10`) |
| AI safety | `AiBoundaryService`, `DataMaskingService`, `DocumentMetadataSanitizer`, `SensitiveFieldRegistry` | The **BR-1/BR-5** enforcement layer — and **untested** (`DR-10`) |
| Errors | `exception/GlobalExceptionHandler` | 404 for unmatched routes, 403 for authorization denials — never a leaked 500 |

### 2.2 What the security layer does *not* do

Named here because the components above make it easy to assume otherwise.

| Absent | Consequence | Requirement |
| --- | --- | --- |
| Rate limiting — 0 implementations, no bucket4j/resilience4j | Credential stuffing and scraping are unthrottled | `SEC-06`, `SEC-12` |
| HSTS / CSP headers — `SecurityConfig` sets neither | No transport or injection hardening at the edge | `SEC-10` |
| Tenant scoping | Isolation rests entirely on consultant scoping | `SEC-07`, `GAP-13` |
| Malware scanning — 0 implementations | Uploaded documents are stored unscanned | `SEC-08` |
| Pagination — 0 `Pageable` usages | Every list endpoint is an unbounded read | `DR-07` |

CSRF is disabled (`SecurityConfig` :64), which is correct for a stateless bearer-token API and is
noted only so it is not mistaken for an oversight.

### 2.3 Persistence conventions (CLAUDE.md §7)

- **Primary keys are database-generated GUIDs.** `id uuid NOT NULL DEFAULT gen_random_uuid()`, mapped on `BaseEntity` as read-only (`@Generated(event = INSERT)` + `@ColumnDefault`). **Never** an app-side sequence or `@GeneratedValue`.
- **Human-facing identifiers are derived from the UUID** via a Postgres `GENERATED ALWAYS AS (...) STORED` column, mapped read-only. Not a numeric sequence, not a `@PrePersist` format string.
- **No length constraints on entity fields** — currently violated in three places (`DR-03`).

### 2.4 Migrations

`backend/src/main/resources/db/migration/{postgresql,mysql,mssql,oracle}/`. Flyway is **disabled**;
migrations are applied manually against `immiauto_db`.

| Version | Scope | PostgreSQL | MySQL / MSSQL / Oracle |
| --- | --- | --- | --- |
| V1–V2 | Core schema + seed data | ✅ | ✅ |
| V3–V5 | App users, MCP audit log, drop MCP API key | ✅ | ❌ frozen |
| V6–V9 | Form & package automation, seed, sample fill, draft origin | ✅ | ❌ frozen |

**Consequence:** the project is effectively PostgreSQL-only. Decision `D-4` should make that explicit
or fund the backfill — carrying three dead dialects taxes every schema change.

**`V5` left a trace.** It dropped `consultant_mcp_api_key`, but `application.properties` :68-70 still
documents the per-consultant API-key scheme as though it existed (`DR-12`).

---

## 3. As-built: the forms & package automation domain

The newest and most valuable subsystem (`GAP-01` / `F41-*`). It is deliberately **content-driven**:
forms are governed data, not hard-coded UI.

**Milestones M1–M5 are complete; M6 is partial.** Verification on 2026-08-24 corrected nine items
from `OPEN` to delivered — see [status dashboard.md](status%20dashboard.md) §5.

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
      └──< PackageValidationIssue (persisted, resolvable, audited)
```

**Service responsibilities**

| Service | Responsibility | LOC |
| --- | --- | ---: |
| `FormCatalogueService` | Governed form registry — source URL, edition, SHA-256, status, inspect | 286 |
| `CanonicalApplicantDataService` | Projects case/client/intake data into canonical fields — the anti-rekeying layer | 268 |
| `FormMappingService` | Canonical field → PDF field, with transforms and versioning | 258 |
| `CaseFormGenerationService` | Fills a draft; skips forms where `supportsFill = false`; blocks on `FORM_SOURCE_HASH_MISMATCH` | 345 |
| `PackageValidationService` | Deterministic readiness rules; 19 severity-classified issue codes | 388 |
| `CasePackageService` | Assembles index/manifest, persists issues, builds the zip, gates approval, serves download | 495 |
| `FormStorageService` | Generated-artifact storage with hashes | 79 |
| `FormAutomationService` | Orchestration facade for the case-scoped API | 99 |
| `pdf/PdfFormEngine` | **SPI** — `PdfBoxFormEngine` is the only implementation today | — |

**The package lifecycle, as built.** `createOrRefreshPackage` → `PackageValidationService` produces
issues → `persistIssues` writes them as rows → consultant resolves non-ERROR issues via
`resolveIssue` → `approvePackage` blocks unless zero unresolved ERRORs *and* explicit acknowledgement
→ `buildZip` assembles PDFs + index + manifest + readiness under a size cap → secured download.
Every transition audits.

### 3.1 The XFA constraint — the single most important architectural fact

`PdfFormEngine` is an interface for a reason. Apache PDFBox is an **AcroForm** engine with no XFA
layout engine, and the high-volume IRCC forms (IMM 0008, 5257, 5645, 5669, 5406, 5532, 1294, 1295)
are **dynamic XFA, encrypted, certified, and Reader-Extended**.

The 2026-06-29 proof of concept established, headlessly and then in Adobe:

1. Datasets injection into the `xfa:data` node **works** — values are present on re-read.
2. `saveIncremental()` preserves the original bytes, encryption, and usage rights.
3. **But both outputs fail to open in Adobe.** PDFBox has open defects writing encrypted incremental updates ([PDFBOX-3188](https://issues.apache.org/jira/browse/PDFBOX-3188), [PDFBOX-4286](https://issues.apache.org/jira/browse/PDFBOX-4286)); a full save decrypts and rewrites, breaking certification and dynamic XFA.

**Therefore:** pure PDFBox can inject XFA data but **cannot produce an Adobe-valid filled IRCC form.**
Full record: [ADR-002](design/ADR-002-xfa-engine-deferred.md), [`memory/pdfbox-cannot-save-ircc-forms.md`](memory/pdfbox-cannot-save-ircc-forms.md).

Architectural response — three layers, in this order:

| Layer | Mechanism | Requirement | State |
| --- | --- | --- | --- |
| 1. Classify honestly | Inspect every form; mark dynamic-XFA and barcode forms `BLOCKED`. **Never** emit a blank-but-"successful" PDF | `F41-01` | 🟡 Partial — see below |
| 2. Degrade usefully | Data-sheet fallback — a mapped-values sheet the consultant transcribes into Adobe, which handles barcode and certification natively | `F41-02` | ⬜ Open |
| 3. Escalate only if funded | A second `PdfFormEngine` bean (Aspose.PDF / Qoppa / iText 7 commercial), selected by `formTechnology`. **No caller changes** — the SPI already isolates this | `F41-14`, `D-1` | ⛔ Blocked |

**Layer 1 is weaker than it looks.** `PdfBoxFormEngine` :38 sets a single boolean,
`hasXfa = acroForm.getXFA() != null`. That does not distinguish **static** XFA (fillable via the
AcroForm layer) from **dynamic** XFA (not fillable at all), and nothing sets `supportsBarcode`. The
`FormStatus` enum has `BLOCKED`, but there is no `FormTechnology` enum to justify the classification.
**Until the classification is four-way and barcode-aware, "fillable" is a guess** — which is exactly
the failure mode layer 1 exists to prevent.

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

11 classes, 14 tools declared in `MCPServer/src/main/resources/config/tools.json`.

- **Dynamic Client Registration (RFC 7591)** lets MCP clients self-register. Provisioning lives in `docker/keycloak/configure-dcr.sh`, **not** the realm import — a `clientScopes` array there suppresses Keycloak's built-in `roles` scope.
- **Anonymous/consent-free DCR is a development posture.** The removed policies must be re-enabled for production (`SEC-05`).
- **Every tool call is audited** via `McpToolAuditService`, and every response crosses the masking boundary before leaving the backend. **No per-tool throttling or quota** (`SEC-12`).
- **`/v1/mcp/**` is authenticated like the rest of the API** — the MCP server calls it with a Keycloak service-account token. It is not network-isolated; `SEC-13` asks for mTLS or private networking on that path.
- Tool definitions are data, not code — adding a tool is a `tools.json` edit.

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
2. **Structural tenant scoping.** `GAP-13` asks for a tenant boundary on every record, query, cache key, file path, job, event, export, and log context. Retrofitting that across **32 entities, 31 repositories, and 37 services** costs far more than designing it in — and those counts are a third higher than the previous estimate this argument was built on. Phase 5 is explicit: *make tenant ID structurally unavoidable in repositories and storage*.

### 5.2 What the forms domain still needs from the target architecture

The forms subsystem is the most complete part of the product and is now bounded by things outside it:

- **Address history** — `ADDRESS_GAP_DETECTED` cannot be implemented because no address-history entity exists. `WORK_HISTORY_GAP_DETECTED` is implemented (`PackageValidationService` :191) precisely because `WorkHistoryEntry` does. The canonical-data extension in Phase 0 unblocks it.
- **Certified-copy tracking** — `CERTIFIED_COPY_REQUIRED_UNRESOLVED` (:289) is informational because `Document` has no certified-copy flag.
- **Structured audit** — package approvals audit through the same compact-JSON `logAudit` as everything else, so `GAP-10`'s compliance export cannot query them.
- **Content governance** — `FormMappingVersion` has a DRAFT→APPROVED→SUPERSEDED lifecycle, but no separation of duties, impact analysis, or rollback. That is `GAP-09`, not `F41-*`.

---

## 6. Architectural rules

Binding constraints. A change that violates one needs an explicit decision recorded in
[change.log.md](change.log.md) and [`design/`](design/), not a workaround.

1. **Configuration flows through one chain** — base var in `.env`/`.env.example` → derived in `docker-compose.yml` → consumed as `${ENV:default}` (Spring) or `window.__env` (Angular). Never a literal host, port, or URL in a config file (CLAUDE.md §15).
2. **Services address each other by compose service name**, never `localhost` or a published host port.
3. **DTOs at the boundary, entities never.** MapStruct in both directions (CLAUDE.md §4).
4. **Reuse before writing.** Check `CommonUtil` and `CommonService`; if the method lives in another service, *move it* there rather than duplicating (CLAUDE.md §5).
5. **One way to do a thing.** If the established pattern is wrong, propose changing it — do not add a parallel one (CLAUDE.md §10).
6. **Provider-neutral domain models.** Signature envelopes, payment intents, and message records are ours; vendor adapters are replaceable. A vendor change must not lose history.
7. **Immutable evidentiary originals.** Derived artifacts are separate objects.
8. **Every generated artifact is traceable** to a form version, mapping version, input snapshot, output hash, and approver.
9. **Edit the source of truth, never a rendered copy** — `configure-dcr.sh` and `realm-immiauto.json.template`, not a rendered realm (CLAUDE.md §12).
10. **Never mark a form fillable on incomplete evidence.** A blank-but-"successful" PDF is a trust failure, not a bug. `BLOCKED` is the safe default.

---

## 7. Decision records

Full records in [`design/`](design/). Summarised here because they shape the structure above.

| ADR | Decision | Status | Effect on this document |
| --- | --- | --- | --- |
| [ADR-001](design/ADR-001-keycloak-over-entra.md) | **Keycloak, not Microsoft Entra External ID**, is the identity provider | Accepted 2026-08-21 (`D-5`) | §1 topology, §4 DCR, and the Keycloak wording of `SEC-01`…`SEC-05`. Supersedes doc 3.1 and all three `Runbooks/` files (`DR-11`) |
| [ADR-002](design/ADR-002-xfa-engine-deferred.md) | **No commercial XFA engine is licensed**; classify-and-degrade instead | Open — blocks `F41-14` (`D-1`) | §3.1's three-layer response; the `PdfFormEngine` SPI exists to keep layer 3 a bean swap |
| [ADR-003](design/ADR-003-trigger-question-as-flag.md) | **Trigger questions are a flag plus a rule key**, not a `TriggerQuestion` entity | Accepted 2026-08-24 | Closes `DR-01`; promotes `BL-03` to `VERIFIED` |
| [ADR-004](design/ADR-004-postgresql-only.md) | **PostgreSQL is the only supported dialect**; MySQL/MSSQL/Oracle are frozen at V2 | Proposed — needs `D-4` | §2.4; every future migration writes one dialect, not four |

---

## 8. Known architectural debt

Ordered by how much future work each one taxes.

| # | Debt | Cost of leaving it | Requirement |
| --- | --- | --- | --- |
| 1 | **No test suite.** 6 backend/MCP files, **0 frontend**, against 37 services and 27 components | Every refactor is unverifiable. Twenty requirements are `VERIFIED` on code-reading alone | `DR-10`, `DR-14` |
| 2 | **No tenant boundary.** Isolation rests on consultant scoping | Retrofit cost grows with every entity added — 32 today | `GAP-13`, `SEC-07` |
| 3 | **No event/outbox layer.** Cross-feature reactions would be controller-coupled | Each new integration adds point-to-point logic | `GAP-12` |
| 4 | **XFA classification is one boolean.** Static and dynamic XFA are indistinguishable; barcodes undetected | The exact failure mode §3.1 layer 1 exists to prevent | `F41-01` |
| 5 | **Three controllers route to `/api/api/…`** and bypass the `/v1/**` matcher | Broken endpoints plus a security-rule gap | `DR-04` |
| 6 | **No CI.** The only automated gate is a local pre-commit hook a fresh clone lacks | Nothing enforces build, test, or dependency scanning | `SEC-15` |
| 7 | **Multi-DB dialects frozen at V2** | Three dead dialects tax every schema change | `D-4` / ADR-004 |
| 8 | **No pagination anywhere** | DoS and bulk-exposure vector on every list endpoint | `DR-07` |
| 9 | **`show-sql=true` in the main profile** | Bound PII in logs in every environment | `DR-08` |
| 10 | **Audit detail is an unstructured JSON string**, and covers no security events | Compliance exports (`GAP-10`) cannot query it; `BR-4` is only half-enforced | `SEC-09`, `GAP-09` |
| 11 | **Party portal tokens never expire** | Permanent unauthenticated access to case data | `DR-05` |
| 12 | **No security headers** — no HSTS, no CSP | No edge hardening | `SEC-10` |

---

## 9. Keeping this document honest

Per CLAUDE.md §12 and §14, architecture changes are not done until the docs move with them:

- A new service or dependency → add it as a **compose service**, wire `depends_on` and env, update [README.md](../README.md), and update the [`deploy-verify`](agents/deploy-verify.md) agent (it hardcodes service names, ports, and health endpoints).
- A new runtime config value → `.env` + `.env.example` + `docker-compose.yml` + the consuming layer.
- A change to the layering or module boundaries → update §2/§5 here **in the same commit**.
- Any decision that overrides §6 → record it as an ADR in [`design/`](design/) and reference it in [change.log.md](change.log.md).
- **Counts in §2 are verified, not estimated.** They were understated by roughly a third until 2026-08-24, and §5.1's retrofit-cost argument depends on them being right. Re-count with [`status-sync`](skills/status-sync/SKILL.md), not from memory.
