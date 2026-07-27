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
| **Database**  | PostgreSQL 16 (schema `immiauto_db`; primary keys via sequences) |
| **Auth**      | Keycloak (OpenID Connect) — self-hosted, realm imported on first start |
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

All services run as containers on a shared Docker network and address each other by service name (`postgres:5432`, `backend:8080`, `keycloak:8080`). Only the host port mappings above are published. The browser and each token's issuer use the public Keycloak URL (`http://localhost:8085`), while the backend/MCP fetch signing keys over the internal network — avoiding the localhost-vs-service-name issuer mismatch.

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
| Keycloak  | http://localhost:8085 (admin console) |
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
On first start the `keycloak` service imports [`docker/keycloak/realm-immiauto.json`](docker/keycloak/realm-immiauto.json), which defines the `immiauto` realm, three clients (`immiauto-frontend` public SPA, `immiauto-backend` API audience, `immiauto-mcp` service account), audience/role token mappers, and the demo user. The SPA logs in via Keycloak (Authorization Code + PKCE); the backend and MCP server validate the resulting tokens as OAuth2 resource servers. The MCP server authenticates its audit writes to the backend with a Keycloak **service-account** (client-credentials) token.

---

## Configuration

Runtime configuration is supplied through environment variables (see [`.env.example`](.env.example) for the full list). Highlights:

| Variable | Purpose |
|----------|---------|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Database name and credentials (shared by the app and Keycloak) |
| `POSTGRES_HOST_PORT` (default `5435`) | Host port for Postgres |
| `BACKEND_HOST_PORT` / `MCP_HOST_PORT` / `FRONTEND_HOST_PORT` / `KEYCLOAK_HOST_PORT` | Host ports for the services |
| `KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD` | Keycloak initial admin credentials |
| `OIDC_ISSUER` / `OIDC_JWK_SET_URI` | Public realm URL (validates token `iss`) and internal JWKS URL (signing keys) |
| `OIDC_BACKEND_AUDIENCE` / `OIDC_MCP_AUDIENCE` | Expected token audiences for the backend and MCP resource servers |
| `MCP_CLIENT_ID` / `MCP_CLIENT_SECRET` | MCP service-account client for authenticated audit writes |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Optional SMTP credentials for reminder emails |

The backend reads `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` (wired in `docker-compose.yml`), so no application code changes are needed to point it at the containerized database.

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
│   └── keycloak/       # Realm import (clients, mappers, demo user)
├── docs/               # Project documentation
├── docker-compose.yml  # Full local stack
└── .env.example        # Environment template
```

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
- **MCP tool scopes** — define the `mcp.*` scopes as Keycloak client scopes so per-tool scope enforcement works for onboarded AI-assistant clients (audience validation for MCP is off until then).
