# Change Log

> **What changed, when, and why — including decisions, not just code.**
> Requirement IDs reference [Requirements.md](Requirements.md). Status effects land in
> [status dashboard.md](status%20dashboard.md). Per-change records live in
> [`progress/`](progress/); decision reasoning in [`design/`](design/).

**Conventions**

- Newest first. Dates are absolute (`YYYY-MM-DD`), never relative.
- Every entry names the **requirement IDs** it moves and the **files** it touched.
- **Decisions get entries too.** A choice not to build something is as consequential as building it.
- Entries are appended in the same change that ships the work — not reconstructed later.

**Change types:** `FEATURE` · `FIX` · `SECURITY` · `REFACTOR` · `DOCS` · `INFRA` · `DECISION` · `AUDIT`

---

## Unreleased

### 2026-08-21 · `DOCS` `FIX` · Document the development harness; correct stale structure blocks

Documented the Claude Code harness in the two entry-point files, and fixed a documentation defect
found while doing it.

**Harness documentation**

- **[README.md](../README.md)** — new "Development harness" section: one-time setup on a fresh clone (`core.hooksPath`, gitleaks), what the harness contains, and a link to [`.claude/README.md`](README.md) for detail. Deliberately short — README is the onboarding doc, and CLAUDE.md §16 remains the deep reference per §14's split. It also states plainly that the harness is **not** needed to build or run the application.
- **[CLAUDE.md](../CLAUDE.md) §16** — new "How the harness runs" and "Harness setup" subsections: how agents, skills, and hooks are each invoked and when to prefer one over another; the parallelism rule; the report-only constraint; and the fact that **no `.claude/settings.json` or `.mcp.json` exists** in this repo. Each agent row now carries its model, making the §16 model policy checkable at a glance.

**Fixes**

- Both [README.md](../README.md) and [CLAUDE.md](../CLAUDE.md) §2 showed a **`docs/` folder that does not exist** in the repository, and omitted `.claude/` and `.githooks/` entirely. Corrected in both, with an explicit note in CLAUDE.md §2 that documentation lives in README (how to run it), CLAUDE.md (how we work on it), and `.claude/` (what we're building).
- Consolidated the duplicated hook-setup instructions in CLAUDE.md §16 into one place. The §8 cross-reference to §16 is retained — it is a pointer, not a duplicate.

**Files:** [README.md](../README.md), [CLAUDE.md](../CLAUDE.md)

---

### 2026-08-21 · `DOCS` · Establish the planning and automation baseline

Created the six governance documents and the agent/skill roster in `.claude/`, derived from the
requirement corpus in `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`,
[README.md](../README.md), and [CLAUDE.md](../CLAUDE.md).

**Requirements:** establishes `BL-*`, `DR-*`, `F41-*`, `SEC-*`, `GAP-*` — 70 tracked items.

**Files**

| File | Purpose |
| --- | --- |
| `.claude/Requirements.md` | Normalized requirement register; product boundary; buy-vs-build; measurement framework; open decisions |
| `.claude/Architecture.md` | As-built topology and layering; forms domain; XFA constraint; target architecture; architectural debt |
| `.claude/Plan.md` | Five-phase plan with a Phase 0.5 inserted for drift; critical path; risks; first 90 days |
| `.claude/Delivery approach.md` | Vertical-slice procedure; definition of done; automation roster; orchestration patterns |
| `.claude/status dashboard.md` | Per-requirement status from the code audit; codebase inventory |
| `.claude/change.log.md` | This file |
| `.claude/agents/*.md` | 7 new agents (`requirements-analyst`, `backend-feature`, `frontend-feature`, `db-migration`, `test-author`, `security-reviewer`, `docs-sync`) |
| `.claude/skills/*/SKILL.md` | 5 new skills (`requirement-intake`, `feature-slice`, `release-gate`, `content-governance`, `status-sync`) |

---

### 2026-08-21 · `AUDIT` · Re-verification of the Completed requirement corpus

Every capability claimed by `HighLevelRequirement Completed/` was checked against the code rather
than taken on the document's word.

**Confirmed delivered** — `BL-01`, `BL-02`, `BL-04`, `BL-06`, `BL-07`, `BL-08`, `BL-09`, `BL-10`, `BL-11`, `BL-12`.

Notably, the prior audits' most serious findings are genuinely closed:

- **All four P0 defects from doc 2.3** — citizenship physical-presence now uses `MIN_PR_DAYS=730` and `PRE_PR_CAP_DAYS=365`; police-certificate logic evaluates *continuous* stays with a 10-year window after age 18; intake resolves questions server-side from templates and rejects unknown keys; the LMIA calculator returns `preliminaryReviewStatus`, not "requirements met".
- **All five Critical findings from doc 3.0** — the API is an authenticated OAuth2 resource server, identity derives from the principal rather than a path variable, `mcpApiKey` is gone from both code and schema (`V5`), and `logAudit` has 18 call sites where the audit found zero.

**Found still open — 11 drift items** (`DR-01`…`DR-11`), four of them High severity:

| ID | Finding |
| --- | --- |
| `DR-04` | Three controllers — not one, as the backlog recorded — map `/api` under context-path `/api`, resolving to `/api/api/…`: `AutomationController`, `PartyPortalController`, `WorkflowController` |
| `DR-05` | `PartyProfile.accessToken` has no expiry, revocation, or rotation |
| `DR-08` | `show-sql=true` in the **main** profile logs bound PII in every environment |
| `DR-10` | 6 test files for ~30 services and 17 controllers |

Medium and low: `DR-02` (PNP checklist seed near-empty), `DR-03` (`@Column(length = 15)` violating
CLAUDE.md §7), `DR-06` (CORS defined twice), `DR-07` (zero pagination), `DR-09` (insecure DB-password
fallback), `DR-01` (`TriggerQuestion` never built).

**Method:** entity/service/controller enumeration; targeted `grep` per documented claim; migration and
seed inspection; test-file count. No source files were changed.

**Effect:** created Phase 0.5 in [Plan.md](Plan.md) to clear drift before new feature work.

---

### 2026-08-21 · `DECISION` · `D-5` — Keycloak is the identity provider; Entra is superseded

**Status:** Accepted.

**Context.** `HighLevelRequirement Completed/3.1 microsoft_entra_external_id_backend_frontend_mcp_plan.pdf`,
the Phase-4 Hardening Plan, and all three `Runbooks/` files describe a Microsoft Entra External ID
architecture. The repository ships self-hosted **Keycloak**: `docker-compose.yml` runs it,
[README.md](../README.md) documents it, the SPA uses `keycloak-angular`, and the backend validates
Keycloak-issued JWTs.

**Decision.** Keycloak is the production identity provider. The Entra documents are historical.

**Consequences**

- `SEC-01`…`SEC-05` restated in Keycloak terms in [Requirements.md](Requirements.md) §6. **Control intent is unchanged** — MFA, step-up auth, refresh handling, secret management, and consent branding are required regardless of provider.
- The three `Runbooks/` PDFs do not apply. `SEC-16` calls for a Keycloak-based revocation runbook to replace them.
- Azure-specific items map to provider-neutral equivalents: Key Vault → any secret store; Conditional Access → Keycloak required actions and flows; Application Insights → any OpenTelemetry backend.
- **Reversible.** The app-to-IdP boundary is a clean OIDC seam; reversing this changes `SEC-01`…`SEC-05` wording and revives the runbooks, and nothing else.

**Recorded as:** `DR-11` (`SUPERSEDED`), [Architecture.md](Architecture.md) §7.

---

### 2026-08-21 · `DECISION` · `D-1` deferred — automated XFA autofill stays blocked

**Status:** Open — **blocks `F41-14`**.

**Context.** The 2026-06-29 proof of concept injected values into IMM 5257's `xfa:data` node and
verified them headlessly in both full-save and incremental-save outputs. **Both then failed to open in
Adobe.** IRCC forms are encrypted, certified (DocMDP), and Reader-Extended; PDFBox has open defects
writing encrypted incremental updates ([PDFBOX-3188](https://issues.apache.org/jira/browse/PDFBOX-3188),
[PDFBOX-4286](https://issues.apache.org/jira/browse/PDFBOX-4286)), and a full save decrypts and
rewrites, breaking certification and dynamic XFA.

**Finding.** Pure PDFBox **cannot** produce an Adobe-valid filled IRCC form. This is a licensing
question, not an engineering one.

**Already ruled out**

| Option | Ruling | Reason |
| --- | --- | --- |
| SaaS form-fill APIs (Anvil, PDF.co) | Rejected | PII egress violates **BR-5**; AcroForm-only anyway |
| iText 7 under AGPL | Rejected | Would force open-sourcing a commercial SaaS |

**Interim position.** Ship `F41-01` (classify and `BLOCK` honestly) and `F41-02` (data-sheet fallback).
`F41-14` remains unscheduled until `D-1` resolves.

---

## Released

### 2026-07-31 · `REFACTOR` `FIX` — GUID primary keys and error-status correctness

- **`583ca56`** Migrated every table's primary key from sequences to database-generated GUIDs. `id` is now `uuid NOT NULL DEFAULT gen_random_uuid()`; the id lives on `BaseEntity` mapped read-only as DB-generated. Human-facing identifiers (`client_number`, `consultant_number`, `case_number`) derive from the UUID via `GENERATED ALWAYS AS (...) STORED` columns, replacing the `String.format("%013d", id)` `@PrePersist`. *Established CLAUDE.md §7.*
- **`3a68303`** Return 404 for unmatched routes and 403 for authorization denials instead of 500. *Closes doc 3.0 Medium-11 (internal-detail leakage).*
- Merged as [#7](https://github.com/mh2005in/Immigration-Consultation/pull/7).

### 2026-07-30 · `DOCS` `INFRA` — Development tooling and guidelines

- **`6a48633`** Added CLAUDE.md §16 (automation — agents, skills, hooks) and deduped hook setup. Merged as [#6](https://github.com/mh2005in/Immigration-Consultation/pull/6).
- **`a8c42a8`** Trimmed project guidelines; added the `deploy-verify` agent, the `worktree` skill, and the `pre-commit` / `commit-msg` / `post-merge` hooks. Merged as [#5](https://github.com/mh2005in/Immigration-Consultation/pull/5).

### 2026-07-29 · `FEATURE` — Local LLM tier and MCP dynamic client registration

- **`0079d1a`** Added the Ollama + LibreChat chat tier with MCP tool calling. Keeps inference on-box, satisfying **BR-5** (no PII egress). Merged as [#4](https://github.com/mh2005in/Immigration-Consultation/pull/4).
- **`d7e14ce`** MCP Dynamic Client Registration ([RFC 7591](https://www.rfc-editor.org/rfc/rfc7591)). Provisioning lives in `docker/keycloak/configure-dcr.sh`, not the realm import. *Anonymous DCR is a **development** posture — re-enable the removed policies for production.* Merged as [#3](https://github.com/mh2005in/Immigration-Consultation/pull/3).

### 2026-07-28 · `INFRA` `FIX` — Environment-driven configuration

- **`637009e`** Drove every host, port, and URL from environment variables. Composite values (OIDC issuer, JWKS URI, token endpoint, CORS origin, MCP audit URL) derive in `docker-compose.yml` from base vars; the SPA reads `window.__env` rendered by nginx via `envsubst`, so host and API changes need no rebuild. *Established CLAUDE.md §15.* Merged as [#2](https://github.com/mh2005in/Immigration-Consultation/pull/2).
- **`d14f679`** Fixed Docker deployment: CORS origin, expiry-alerts routing, recreation hardening. Merged as [#1](https://github.com/mh2005in/Immigration-Consultation/pull/1).

### 2026-07-27 · `FEATURE` — Initial platform

- **`cb34113`** Initial commit. Spring Boot backend, Angular 18 SPA, MCP server, Keycloak, PostgreSQL, Docker Compose. Delivers the `BL-*` baseline: 11 application types, intake templates, checklist generation, document management, Canadian workflow modules, AI boundary and masking, forms & package automation M1–M4 plus manual upload.

---

## Pre-repository history

Work predating `cb34113`, reconstructed from the requirement corpus. Dates are the source documents'.

| Date | Stage | Outcome |
| --- | --- | --- |
| 2026-06-06 | Stage 1 — Business domain | Established the product boundary: intake and document automation **with consultant approval**, never immigration advice. The `BR-1`…`BR-5` rules descend from here |
| 2026-06-11 | Stage 2.0 — Application-type deep dive | Mapped 11 application types by volume, complexity, automation value, and risk; set the compliance principle and MVP priority order |
| 2026-06-12 → 06-14 | Stage 2.1 — TODO register | ~100 items across compliance, type expansion, intake, checklists, automation, portals, workflows, frontend, and lead-to-client |
| 2026-06-14 | Stage 2.2 — MCP/AI + access control | Specified `SensitiveFieldRegistry`, `DataMaskingService`, `DocumentMetadataSanitizer`, and the `McpApiController` masking boundary — all subsequently built (`BL-07`, `BL-08`) |
| 2026-06-15 | Stage 2.3 — Validation compliance review | 4 P0 + 4 P1 findings. **7 of 8 now closed** (`BL-10`, `BL-11`); upload security remains partial |
| 2026-06-19 | Stage 3.0 — Security & standards audit | 5 Critical, 4 High, 6 Medium. **All Critical closed** (`BL-12`); 8 remain open as `DR-*` and `SEC-*` |
| 2026-06-19 | Stage 3.1 — Entra External ID plan | **Superseded 2026-08-21** by `D-5` — the stack runs Keycloak |
| 2026-06-21 | Phase-4 Hardening Plan | 16 production-readiness items → `SEC-01`…`SEC-16` |
| 2026-06-22 | Phase 5 Gap Analysis | 17 product domains → `GAP-01`…`GAP-17`; 18-month roadmap; measurement framework |
| 2026-06-26 | Section 4.1 implementation plan | Domain model and milestone plan for IRCC form and package automation |
| 2026-06-29 | Section 4.1 XFA PoC | **Save-step wall found.** PDFBox cannot re-save encrypted + certified IRCC forms into an Adobe-valid file. Drives `F41-14` `BLOCKED` and the `F41-02` fallback |
| 2026-07-01 | Section 4.1 backlog | M1–M4 complete, manual upload complete, M5 code-complete, M6 core complete → `F41-01`…`F41-14` |

---

## Maintaining this log

- **Append in the same change that ships the work.** A log reconstructed at release time loses the reasoning, which is the part worth keeping.
- **Record decisions, including the ones that defer or reject.** `D-1` above is more useful than most feature entries.
- **Name the requirement IDs.** The `status-sync` skill checks that every `VERIFIED` requirement has a corresponding entry here.
- **Never put a secret or real PII in an entry** (CLAUDE.md §8) — that includes example payloads.
- **Never reference Claude or CLAUDE.md in a commit message** (CLAUDE.md §11). This file is documentation and may reference CLAUDE.md freely; commit messages may not.
