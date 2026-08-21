# Immigration Intake & Document Automation

AI-assisted intake and document-follow-up automation for **Canadian immigration consultants**. The platform helps a consultant take a client from first intake to a submission-ready package: it captures structured intake, generates the right document checklist per service type, tracks uploaded documents and their gaps, auto-fills official IMM forms, and surfaces reminders and expiry alerts — with an MCP server that lets AI assistants safely query and assist on cases.

> **Status:** active development. Authentication is handled by a self-hosted **Keycloak** (OpenID Connect), deployed as part of the Docker Compose stack.

---

## Purpose

Immigration consultants spend a large share of their time on repetitive, error-prone document work: figuring out which documents each case needs, chasing clients for missing items, filling the same fields across multiple government PDFs, and watching passport/permit expiry dates. This project automates that busywork behind a consultant-facing web app and an AI-tooling layer, so consultants can focus on judgement rather than paperwork.

---

## Features

- **Consultant & client management** — onboard consultants (with admin roles) and manage their clients.
- **Case management** — immigration cases by service type (e.g. Study Permit, Work Permit, Express Entry, Spousal Sponsorship) with status tracking.
- **Structured intake** — configurable intake question templates and captured responses, with conditional rules that adapt the questionnaire.
- **Document checklists** — auto-generated, service-type-specific checklists from approved templates, including conditional ("only if…") items.
- **Document management** — upload, classify, and track documents against the checklist; detect missing or inconsistent documents.
- **Form & package automation** — inspect and auto-fill official IMM AcroForm PDFs (pilot: IMM 5476) and assemble draft submission packages, using governed source forms.
- **Reminders & expiry alerts** — reminders for outstanding items and alerts for expiring passports/permits.
- **Dashboard & workflow** — consultant dashboard and workflow endpoints summarizing case progress.
- **Party portal** — endpoints supporting party/applicant-facing interactions.
- **Audit logging** — actions are recorded for traceability.
- **MCP server for AI assistants** — a Model Context Protocol server exposing safe, per-consultant tools: `list_clients`, `get_client`, `summarize_intake`, `classify_document`, `detect_missing_documents`, `check_inconsistencies`, `draft_reminder_email`, `extract_timeline`, `summarize_translation`, `get_case_overview`, `validate_output`, and case/client/consultant lookups. Calls are authenticated and audited.

---

## Technology stack

| Layer         | Technology |
|---------------|------------|
| **Backend**   | Java 21, Spring Boot 3.3 (Web, Data JPA, Validation, Security, Mail), Spring Security OAuth2 Resource Server, MapStruct (DTO ↔ entity), Lombok, Apache PDFBox (form filling), springdoc-openapi (Swagger UI) |
| **Frontend**  | Angular 18, TypeScript, `keycloak-angular` / `keycloak-js` |
| **MCP server**| Java 21, Spring Boot 3.3 (Model Context Protocol tools, OAuth2 resource server + Keycloak service-account token for audit writes) |
| **Database**  | PostgreSQL 16 (schema `immiauto_db`; primary keys are database-generated GUIDs — `uuid DEFAULT gen_random_uuid()`) |
| **Auth**      | Keycloak (OpenID Connect) — self-hosted, realm imported on first start |
| **LLM + Chat**| Ollama (local, GPU) serving `qwen2.5:3b`; LibreChat chat UI (MongoDB-backed) wired to Ollama and the MCP tools over OAuth |
| **Runtime**   | Docker & Docker Compose; nginx (serves the SPA and reverse-proxies the API) |

---

## Architecture

```
                         ┌─────────────────────────────┐
   Browser ──────────────►  frontend (nginx)  :8081     │
                         │  - serves Angular SPA        │
                         │  - proxies /api ──► backend  │
                         └──────────────┬──────────────┘
                                        │ (internal network)
                    ┌───────────────────▼───────────────────┐
                    │  backend (Spring Boot)  :8080/api      │
                    │  cases, clients, checklists, intake,   │
                    │  documents, form automation, reminders │
                    └──┬──────────────┬─────────────────┬────┘
                       │              │                 │
        ┌──────────────▼───────┐  ┌───▼──────────────┐  ┌▼──────────────────┐
        │ postgres :5435→5432  │  │ keycloak :8085   │  │ mcpserver :8084    │
        │ dbs: immiauto,       │  │ OIDC realm       │  │ AI tools (MCP);    │
        │      keycloak        │◄─┤ immiauto         │  │ audit → backend    │
        │ schema immiauto_db   │  │ (own db)         │  │ (service account)  │
        └──────────────────────┘  └──────────────────┘  └────────────────────┘
```

All services run as containers on a shared Docker network and address each other by service name (`postgres:5432`, `backend:8080`, `keycloak:8080`). Only the host port mappings above are published. The browser and each token's issuer use the public Keycloak URL (`KEYCLOAK_PUBLIC_URL`, default `http://localhost:8085`), while the backend/MCP fetch signing keys over the internal network (`KEYCLOAK_INTERNAL_URL`) — avoiding the localhost-vs-service-name issuer mismatch. Every host/port/URL comes from `.env` (see [Configuration](#configuration)), so nothing is hardcoded.

---

## Getting started (Docker Compose)

Everything runs as one stack — you do **not** need Java, Maven, Node, or a local Postgres installed; only Docker.

### Prerequisites
- Docker Desktop (Docker Engine 24+ and Compose v2). Start Docker Desktop before running the commands below.

### 1. Configure environment
```bash
cp .env.example .env
```
Edit `.env` and set the values you need (DB password, Keycloak admin password, MCP client secret). `.env` is gitignored; never commit real secrets.

### 2. Build and start the stack
```bash
docker compose up -d --build
```

### 3. Open the app
| Service   | URL |
|-----------|-----|
| Frontend  | http://localhost:8081 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/api/swagger-ui.html |
| MCP server | http://localhost:8084 |
| LibreChat  | http://localhost:3080 (chat UI over Ollama + MCP tools) |
| Ollama    | http://localhost:11434 (local LLM API) |
| Keycloak  | http://keycloak.localtest.me:8085 (admin console) |
| Postgres  | `localhost:5435` (dbs `immiauto` + `keycloak`, app schema `immiauto_db`) |

**Demo login:** `demo` / `Passw0rd!` (email `demo@immiauto.ca`, linked to the seed consultant). The Keycloak admin console signs in with `admin` / your `KEYCLOAK_ADMIN_PASSWORD` (`admin` locally).

### 4. Manage the stack
```bash
docker compose ps            # status of all services
docker compose logs -f backend   # follow a service's logs
docker compose down          # stop (keeps the database volume)
docker compose down -v       # stop and wipe the database volume (re-runs DB init on next up)
```

### Database initialization
On a **fresh** Postgres volume, the scripts in [`docker/postgres/init/`](docker/postgres/init/) run automatically: [`00-init.sh`](docker/postgres/init/00-init.sh) creates the `immiauto_db` schema and applies the versioned `V1…Vn` scripts from `backend/src/main/resources/db/migration/postgresql/` (schema + seed data) in order, and [`01-keycloak-db.sh`](docker/postgres/init/01-keycloak-db.sh) creates a separate `keycloak` database for Keycloak's own storage. This only runs on an empty volume — to rebuild from scratch (app **and** Keycloak realm data), run `docker compose down -v` and bring the stack up again.

### Authentication (Keycloak)
On first start a small `realm-init` service renders [`docker/keycloak/realm-immiauto.json.template`](docker/keycloak/realm-immiauto.json.template) with `envsubst` (substituting `FRONTEND_PUBLIC_URL` into the redirect URIs / web origins) into a shared volume, and the `keycloak` service imports it. The realm defines the `immiauto` realm, three clients (`immiauto-frontend` public SPA, `immiauto-backend` API audience, `immiauto-mcp` service account), audience/role token mappers, and the demo user. The SPA logs in via Keycloak (Authorization Code + PKCE); the backend and MCP server validate the resulting tokens as OAuth2 resource servers. The MCP server authenticates its audit writes to the backend with a Keycloak **service-account** (client-credentials) token.

### MCP Dynamic Client Registration (DCR)
The MCP server lets AI-assistant clients (Claude Desktop, MCP Inspector, …) **self-register** with Keycloak via [RFC 7591](https://datatracker.ietf.org/doc/html/rfc7591) — no pre-provisioned client id. It serves OAuth 2.0 Protected Resource Metadata at `/.well-known/oauth-protected-resource`, pointing clients at Keycloak, whose discovery document advertises the `registration_endpoint`. A client registers, then runs Authorization Code + PKCE to obtain a user token.

Because a self-registered client is untrusted, **access is gated on the user, not the client**: the MCP server maps the token's `realm_access.roles` and requires an app role (`CONSULTANT_OWNER` or `ADMIN`) on `/mcp/**`. A token minted through a dynamically-registered client whose subject is not a real app user is rejected with **403** (missing token → **401**). Per-tool `mcp.*` scopes are still enforced on top of that.

Keycloak isn't DCR-ready out of the box, so a one-shot [`keycloak-config`](docker-compose.yml) service runs after Keycloak (using its bundled `kcadm.sh`, see [`docker/keycloak/configure-dcr.sh`](docker/keycloak/configure-dcr.sh)) and idempotently: creates the `mcp.*` client scopes as realm **optional** defaults (so registered clients can request them) and removes the anonymous **Trusted Hosts**, **Consent Required**, and **Full Scope Disabled** registration policies so registration is open and tokens carry the user's realm roles. These scopes/policies are provisioned here rather than in the realm import on purpose — supplying a `clientScopes` array in a realm import makes Keycloak skip creating its built-in scopes (including `roles`, which the role gate depends on).

> **Dev posture.** Anonymous, consent-free DCR with full user scope is convenient for local development. For a hardened deployment, re-enable the removed policies (or pin trusted clients) and require explicit consent — see the notes in [`configure-dcr.sh`](docker/keycloak/configure-dcr.sh).

### Local LLM + chat UI (Ollama + LibreChat)
The stack ships a local chat assistant that can call the immigration MCP tools, entirely on your machine:

- **`ollama`** runs the LLM (`qwen2.5:3b` by default, `OLLAMA_MODEL`) on the **NVIDIA GPU** and exposes an OpenAI-compatible API on `:11434`. A one-shot **`ollama-init`** service pulls the model into a shared volume on first start.
- **`librechat`** ([config](docker/librechat/librechat.yaml)) is the chat UI at http://localhost:3080, backed by **`mongodb`**. It talks to Ollama for chat and connects to the MCP server (`http://mcpserver:8084/mcp`) for tools.

**Using the tools:** sign up in LibreChat (local account), pick the **Qwen2.5 3B (Ollama)** model, and enable the **immiauto** MCP server. LibreChat runs the MCP **OAuth** flow — it self-registers with Keycloak (DCR), sends you to sign in (`demo` / `Passw0rd!`), and because the MCP server gates on your role, only a real app user (`CONSULTANT_OWNER`/`ADMIN`) can invoke the tools.

> **Why `keycloak.localtest.me`?** The MCP OAuth flow needs one issuer URL that resolves to Keycloak from **both** the browser and the LibreChat container. `*.localtest.me` is public DNS → `127.0.0.1` for the browser, and compose points the same name at the host gateway inside the container (`extra_hosts`). No LAN IP, no hosts-file edit (offline fallback: add `127.0.0.1 keycloak.localtest.me` to your hosts file). Set `KEYCLOAK_PUBLIC_URL` back to `http://localhost:8085` if you don't need LibreChat → MCP tool calling.

> **GPU required as configured.** The `ollama` service reserves an NVIDIA GPU (needs Docker with the NVIDIA Container Toolkit / WSL2 GPU). To run CPU-only, remove the `deploy.resources.reservations.devices` block from the `ollama` service in [`docker-compose.yml`](docker-compose.yml).

---

## Configuration

**All hosts, ports, domains, and URLs are configured through environment variables — `.env` is the single source of truth.** You change a domain or port in `.env` **once**; `docker-compose.yml` derives the OIDC issuer, JWKS URI, token endpoint, CORS origin, audit URL, and the SPA's runtime config from it. Nothing is hardcoded in property or config files. Copy [`.env.example`](.env.example) to `.env` and edit as needed.

**Base variables (edit these):**

| Variable | Purpose |
|----------|---------|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Database name and credentials (shared by the app and Keycloak) |
| `POSTGRES_HOST_PORT` / `BACKEND_HOST_PORT` / `MCP_HOST_PORT` / `FRONTEND_HOST_PORT` / `KEYCLOAK_HOST_PORT` | Published host ports |
| `FRONTEND_PUBLIC_URL` / `KEYCLOAK_PUBLIC_URL` | **Public** URLs the browser uses (must match the host ports). Drive the token issuer, CORS origin, Keycloak redirect/web-origins, and the SPA's Keycloak URL |
| `KEYCLOAK_INTERNAL_URL` / `BACKEND_INTERNAL_URL` | **Internal** Docker-network URLs for service-to-service calls (JWKS/token fetch, MCP audit) |
| `KC_REALM` / `FRONTEND_CLIENT_ID` / `API_BASE_URL` | Realm name, SPA client id, and the SPA's API base path |
| `KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD` | Keycloak initial admin credentials |
| `OIDC_BACKEND_AUDIENCE` / `OIDC_MCP_AUDIENCE` / `*_VALIDATION` | Expected token audiences and whether to enforce them |
| `MCP_CLIENT_ID` / `MCP_CLIENT_SECRET` | MCP service-account client for authenticated audit writes |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Optional SMTP credentials for reminder emails |

**Derived automatically** (do not set by hand): `OIDC_ISSUER`, `OIDC_JWK_SET_URI`, `OIDC_TOKEN_ENDPOINT`, `CORS_ALLOWED_ORIGINS`, `MCP_AUDIT_URL`, `KC_HOSTNAME` — composed in `docker-compose.yml` from the base variables above.

How each layer stays env-driven (no rebuilds to change a host/port):
- **Spring (backend, MCP):** read `${ENV:default}` in `application.properties`; compose overrides for Docker.
- **Angular SPA:** runtime config via `window.__env` — nginx renders `assets/env.js` from env with `envsubst` on container start ([`frontend/env.template.js`](frontend/env.template.js)), so `environment.ts` never hardcodes a URL.
- **Keycloak realm:** [`realm-immiauto.json.template`](docker/keycloak/realm-immiauto.json.template) uses `${FRONTEND_PUBLIC_URL}`; the `realm-init` service runs `envsubst` on it before Keycloak imports it.

To run behind a different host/domain (e.g. a server or a different port), set `FRONTEND_PUBLIC_URL`, `KEYCLOAK_PUBLIC_URL`, and the `*_HOST_PORT` values in `.env`, then `docker compose down && up -d` — no code changes or rebuilds required.

---

## Project structure

```
Immigration-Consultation/
├── backend/            # Spring Boot API (cases, clients, checklists, forms, intake)
│   └── src/main/resources/db/migration/postgresql/   # V1..Vn schema + seed scripts
├── frontend/           # Angular 18 SPA (served by nginx in Docker)
├── MCPServer/          # Spring Boot MCP server (AI tools)
├── docker/
│   ├── postgres/init/  # DB bootstrap scripts (app schema + migrations, keycloak db)
│   ├── keycloak/       # Realm import TEMPLATE + configure-dcr.sh (MCP DCR bootstrap)
│   └── librechat/      # librechat.yaml (Ollama endpoint + MCP server wiring)
├── .claude/            # Development harness + project tracking (see below)
├── .githooks/          # pre-commit, commit-msg, post-merge
├── docker-compose.yml  # Full local stack
├── CLAUDE.md           # Contributor working guidelines
└── .env.example        # Environment template
```

---

## Development harness

The repo ships a **[Claude Code](https://claude.com/claude-code) harness** — agents, skills, git hooks, and a project-tracking structure under [`.claude/`](.claude/). It automates the conventions in [CLAUDE.md](CLAUDE.md) so they can't be forgotten.

**You do not need it to build or run the application.** The Docker Compose stack above is self-contained. The harness matters if you are *contributing* to the codebase.

### One-time setup on a fresh clone

```bash
git config core.hooksPath .githooks
```

That enables three hooks: `pre-commit` (secret scan + commit-author guard), `commit-msg` (blocks AI co-author attribution), and `post-merge` (cleans up merged worktrees).

The secret scan additionally needs **gitleaks** — without it that check is skipped silently, though the author guard still runs:

```bash
winget install gitleaks
```

*(macOS: `brew install gitleaks`; see [gitleaks install docs](https://github.com/gitleaks/gitleaks#installing).)*

### What's in the harness

| | |
| --- | --- |
| **8 agents** ([`.claude/agents/`](.claude/agents/)) | Scoped roles with their own context — backend and frontend feature work, migrations, tests, security review, requirements analysis, docs sync, deploy verification |
| **6 skills** ([`.claude/skills/`](.claude/skills/)) | On-demand procedures — vertical slice, requirement intake, release gate, content governance, status sync, worktrees |
| **3 git hooks** ([`.githooks/`](.githooks/)) | Deterministic commit and merge gates |
| **Project tracking** ([`.claude/`](.claude/)) | Requirements register, architecture, delivery plan, status dashboard, change log, and the source requirement corpus |

**[`.claude/README.md`](.claude/README.md) is the index** — it explains the layout, the roster, and where each kind of work gets recorded.

### Where the project stands

[`.claude/status dashboard.md`](.claude/status%20dashboard.md) tracks 70 requirements against the code. Two things a new contributor should know up front:

- **Test coverage is thin** — 6 test files for ~30 services and 17 controllers. Adding tests alongside your change is not optional here (CLAUDE.md §9).
- **Flyway is disabled.** Migrations under `db/migration/postgresql/` are applied **manually**; a written migration is not a deployed one.

---

## Git & contribution conventions

- **Don't commit or push unless asked.** Keep commits focused; run the project's build, lint, and tests before committing.
- **Author commits and PRs as `mh2005in`.** If git's configured author is anything else, stop and fix the config before committing — don't author under another identity.
- **Do not add any AI assistant as an author or co-author.** No `Co-Authored-By` trailer and no agent attribution in commit messages or PR bodies.
- **Temp/working branches: `mh/<kebab-name>`** — prefix with `mh/`, then a short kebab-case description of the work. Never put an agent name in the branch name.
- **Never commit secrets or PII.** Replace API keys, tokens, passwords, and personal data with obvious placeholders before committing; keep real values in `.env` (gitignored) and keep uploaded/generated documents out of the repo.

See [CLAUDE.md](CLAUDE.md) for the full working guidelines.

---

## Roadmap

- **MCP per-user identity** — MCP tool/audit calls to the backend currently use a Keycloak **service-account** token (they act as the MCP service account, not the calling user). Restore true per-user context via Keycloak **token exchange** when the MCP server is exercised with real user tokens.
- **Service-account provisioning** — the backend auto-provisions any authenticated subject as a consultant; a non-human service account should be recognized and skipped (it currently errors on `/me`). Harden `UserProvisioningService` to ignore service accounts.
- **MCP audience validation** — the `mcp.*` scopes are now real Keycloak client scopes and MCP clients self-register via [DCR](#mcp-dynamic-client-registration-dcr), but MCP audience validation (`OIDC_MCP_AUDIENCE_VALIDATION`) stays off until registered clients are provisioned to carry `aud=immiauto-mcp`. Turn it on once DCR clients receive the MCP audience mapper.
