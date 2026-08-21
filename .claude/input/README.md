# Input — the source requirement corpus

**Purpose:** a committed, greppable copy of every upstream document the requirements derive from, so
agents can search the corpus without re-extracting and without the Downloads folder existing.

**Upstream original:** `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`
**Extracted:** 2026-08-21 · `pdftotext -layout` (the `-layout` flag is required — these documents are table-heavy and lose their meaning without it)

---

## ⚠ Redaction applied

The three Entra runbooks contained a **real Entra tenant ID and four real application client IDs**.
Per CLAUDE.md §8 they were replaced with placeholders in these copies:

| Placeholder | Was |
| --- | --- |
| `<ENTRA_TENANT_ID>` | the organization's Entra tenant ID |
| `<SPA_CLIENT_ID>` | `immigration-saas-spa` |
| `<BACKEND_API_CLIENT_ID>` | `immigration-saas-api` |
| `<MCP_API_CLIENT_ID>` | `immigration-saas-mcp` |
| `<MCP_PREREG_CLIENT_ID>` | `immigration-mcp-client` |

The originals in Downloads are untouched. **These runbooks are superseded anyway** — decision `D-5`,
the stack runs Keycloak.

**Scan results (2026-08-21):** no emails, no phone numbers, no passport or SIN patterns, no secret
values. The only `password`/`secret` keyword hits are placeholders (`change-me`), env-var references
(`${ENTRA_MCP_CLIENT_SECRET:}`), and generic setup instructions.

---

## Contents

### `completed/` — baseline (verified 2026-08-21, see [status dashboard](../status%20dashboard.md) §3)

| File | Date | Covers | Requirements |
| --- | --- | --- | --- |
| `1.txt` | 2026-06-06 | Stage 1 — business domain, consultant workflow, **the product boundary** | `BR-1`…`BR-5` |
| `2.0.txt` | 2026-06-11 | Application-type deep dive; 11 types by volume, complexity, risk | `BL-02` |
| `2.1-todo-8-removed.txt` | 2026-06-14 | TODO register, ~100 items across 10 sections | `BL-01`…`BL-06`, `BL-09` |
| `2.1-todo-8-present-original.txt` | 2026-06-12 | The same register before section 8 was removed | — |
| `2.2-todo-cleanup-and-AI-planning.txt` | 2026-06-14 | MCP/AI integration + access control; masking layer design | `BL-07`, `BL-08` |
| `2.3-Missing-Validation.txt` | 2026-06-15 | Validation compliance review — 4×P0, 4×P1 | `BL-10`, `BL-11` |
| `3.0-Security_Compliance_Coding_Audit.txt` | 2026-06-19 | 5 Critical, 4 High, 6 Medium + PIPEDA + standards | `BL-12`, `DR-03`…`DR-09` |
| `3.1-microsoft_entra_external_id_...txt` | 2026-06-19 | Entra External ID plan — **SUPERSEDED** by `D-5` | `DR-11` |

### `pending/` — open requirements

| File | Date | Covers | Requirements |
| --- | --- | --- | --- |
| `Phase-4-Hardening-Plan.txt` | 2026-06-21 | 16 production-readiness items, A1–D16 | `SEC-01`…`SEC-16` |
| `Phase-5-...-Missing-Features-and-Recommendations.txt` | 2026-06-22 | 17 product domains, architecture, 18-month roadmap, metrics | `GAP-01`…`GAP-17` |
| `Section-4.1-IRCC-Form-Package-Automation-Implementation-Plan.md` | 2026-06-26 | Domain model and milestones for form automation | `F41-*` |
| `Section-4.1-XFA-Real-PDF-Onboarding-Issue.md` | 2026-06-29 | **The XFA blocker** — PoC results, options, recommendation | `F41-01`, `F41-02`, `F41-14` |
| `Section-4.1-Backlog.md` | 2026-07-01 | Living backlog; M1–M6 status | `F41-*` |
| `Section-4.1-Backlog.txt` | 2026-07-01 | PDF rendering of the above | — |

> **The `.md` files are the source of truth over their generated `.pdf`/`.txt` twins.** The backlog
> markdown says so explicitly.

### `runbooks/` — superseded operational procedures

`Entra-Auth-Configuration-Runbook.txt`, `MCP-Client-Pre-Registration.txt`,
`MCP-Registration-Removal.txt` — all describe Microsoft Entra. **They do not apply to the running
system.** Kept because `SEC-16` needs a Keycloak equivalent and these show the shape it should take.

### `db-setup/` — local database install guides

PostgreSQL, MySQL, SQL Server Express, Oracle AI Database Free. Reference only — the stack ships
PostgreSQL in Docker Compose (see [README.md](../../README.md)). Relevant to decision `D-4`.

---

## Conventions

- **Read-only.** Never edit a file here to reflect a decision — decisions go in [`../progress/`](../progress/) and the affected root doc. This folder is a record of what the source said.
- **Re-extract, don't hand-patch.** If an upstream document changes, re-run the extraction, re-run the PII scan, re-apply redactions, and note it in [`../progress/`](../progress/).
- **A "Completed" marking is a claim, not evidence.** The 2026-08-21 audit found 11 open items inside signed-off work. Verify against code — see the `status-sync` and `requirement-intake` skills.

## Re-extraction

```bash
pdftotext -layout "<source>.pdf" "<target>.txt"
```

Then re-run the PII scan before committing:

```bash
grep -rhoiE "[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}" .
grep -rhoE "\b[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}\b" .
grep -rniE "(password|secret|api[_-]?key|token)\s*[:=]\s*\S{6,}" .
```
