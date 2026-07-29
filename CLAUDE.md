# Project Guidelines

> For any best-practice implementation requested, **ask permission first**, then append the agreed practice to this file.
> For independent task, **ask permission first** then dedicate the task to sub agent.
> For assumption, before making any assumptions **ask permission first using ask question tool** whether the assumption is correct or not.

---

## 1. Environment & Installation

Use the following installation directories before running any build or tooling commands.

| Tool  | Expected Install Directory               |
| ----- | ---------------------------------------- |
| Java  | `C:\Program Files\Java\jdk-24.0.2`       |
| Maven | `C:\Program Files\apache-maven-3.9.16`   |

- If an installation is **not found** at the directory above, ask the user for the correct path.
- Once confirmed, **update this file** with the corrected directory.

---

## 2. Project Structure

```
project-root/
├── backend/      # Spring Boot (Java) implementation
├── frontend/     # UI implementation (Angular / React)
├── MCPserver/    # MCP server (only if required)
└── docs/         # Project-related documentation
```

---

## 3. Interaction & Workflow

- **Always be interactive.** 
- Before making any change, **describe the change to the user and ask permission using ask question tool** before proceeding.
- Always **evaluate the impact** of a change on other modules and plan accordingly.
- After completing work, **provide a summary of all changes along with the affected filenames**.

---

## 4. Architecture & Implementation Standards

- Use a **Spring mapper** to transform DTO ↔ Entity (both directions).
- Use **OAuth2** for authentication unless otherwise specified.
- **Proper exception handling is mandatory.**

---

## 5. Code Reuse

Before implementing any method:

1. Check **`CommonUtil`** and **`CommonService`** — if the method already exists there, **reuse it**.
2. If the method exists in **another service class**, **move it** to `CommonService` or `CommonUtil`, then use it from there.

---

## 6. API Endpoints

- When changing an API endpoint, also look for and apply the **corresponding frontend changes**.
- Always **ignore the MCP controller** when deduplicating APIs.
- If an existing endpoint already does what is being requested, **ask the user before creating a new one**. Only create a new endpoint if the user agrees.

---

## 7. Entities

- Always generate the primary key using a **sequence**, named `tablename_seq`.
- **Never** add length constraints to entity fields.

---

## 8. Secrets & PII — Never Commit, Always Placeholder

> **Secrets and personal data must never be committed. Replace them with placeholders.**

- **Secrets** — API keys, tokens, client secrets, passwords, connection strings, certificates. Replace with an obvious placeholder before committing (e.g. `YOUR_API_KEY_HERE`, `<CLIENT_SECRET>`, `ChangeThisPassword`). Real values belong in configuration/environment variables — never hardcoded, never in git.
- **PII** — client/applicant personal data: names, dates of birth, passport and licence numbers, addresses, emails, phone numbers, and any uploaded or generated client document. Replace with clearly fake sample values (e.g. `Jane Doe`, `AA000000`, `applicant@example.com`) in code, tests, fixtures, seed data, and documentation.
- **Never commit data artifacts** — uploaded or generated documents, database dumps, exports, or logs containing real data. Keep them out of the repo and add them to `.gitignore`.
- **Before every commit, scan the staged diff** for real credentials or personal data and confirm each has been replaced with a placeholder.
- Never paste real credentials or personal data into code, tests, logs, commit messages, or PR descriptions.
- If a real secret is ever committed, treat it as **compromised**: rotate it — don't just amend the commit. If real PII is committed, raise it immediately; scrubbing history may be required.
- **A pre-commit secret scanner (gitleaks) runs automatically** ([.githooks/pre-commit](.githooks/pre-commit) runs `gitleaks git --staged`). It blocks commits whose staged changes contain likely secrets.
  - Requires gitleaks installed. Windows: `winget install gitleaks` (or `scoop`/`choco`); macOS: `brew install gitleaks`. See https://github.com/gitleaks/gitleaks#installing.
  - Enable the hooks in a fresh clone: `git config core.hooksPath .githooks` (per-clone, so each checkout runs this once).
  - It's a safety net, not a substitute for the placeholder rule above — **gitleaks matches secret patterns, not PII**, so it won't catch a real name or email. Bypass a genuine false positive with `git commit --no-verify`.

---

## 9. Testing

- Write tests **alongside** the code they cover.
- A bug fix ships with a test that **fails without the fix**.
- Keep unit tests fast and offline — mock external services (authentication provider, database, third-party APIs). Tests that need a live database or network belong in a separate, slower suite.
- **Never delete or weaken a failing test to make the suite pass** — fix the underlying issue, or ask.

---

## 10. Code Style

- **Match the patterns of the surrounding code** — naming, structure, layering, and idiom.
- **Don't add a second way to do something that already has an established pattern.** If the existing pattern is wrong, propose changing it rather than introducing a parallel one.

---

## 11. Git

- **Don't commit or push unless asked.**
- Keep commits focused; run the project's build, lint, and test checks before committing.
- **Author commits and PRs as `mh2005in`.** If git's configured author is anything else, stop and fix the config before committing — never author under another identity.
- **Never add Claude as an author or co-author.** No `Co-Authored-By` trailer, no agent attribution in commit messages or PR bodies, and no references to Claude/CLAUDE.md in commit messages.
- **Temp/working branches: `mh/<kebab-name>`** — prefix with `mh/`, then a short kebab-case description of the work. Never put an agent name in the branch name.

---

## 12. Docker & Deployment

> **The whole project runs as a Docker Compose stack. After every change, build and deploy it via Docker Compose and confirm it comes up.**

- The stack is defined in [docker-compose.yml](docker-compose.yml): `postgres` + `keycloak` (+ `realm-init`, `keycloak-config`) + `backend` + `mcpserver` + `frontend` (Angular served by nginx) + the local-LLM/chat tier `ollama` (+ `ollama-init`) + `mongodb` + `librechat`. Bring it up with:

  ```bash
  docker compose up -d --build
  ```

- **After any change, rebuild and redeploy the affected service(s)** and verify the stack is healthy (`docker compose ps`, then exercise the affected endpoint) — don't consider a change done until it runs in the container.
- **Services talk to each other by compose service name over the internal network** (`postgres:5432`, `backend:8080`), never `localhost` or the published host ports. The frontend reaches the API same-origin via the nginx `/api` proxy.
- **Postgres is published on host port `5435`** (container `5432`). Data lives in the `pgdata` volume.
- **The DB schema + seed run automatically on a fresh volume** via [docker/postgres/init/00-init.sh](docker/postgres/init/00-init.sh), which creates the `immiauto_db` schema and applies the versioned `V1..Vn` PostgreSQL scripts. To rebuild the DB from scratch: `docker compose down -v && docker compose up -d --build`.
- **New runtime config → add it to the service's `environment:` in docker-compose and document it in [.env.example](.env.example).** Real values go in `.env` (gitignored) — never commit secrets.
- **New dependency (a service, model, etc.) → add it as a compose service** and wire the dependent service's `depends_on`/env to reach it.
- **Auth:** self-hosted **Keycloak** (OIDC) runs as the `keycloak` service, backed by its own `keycloak` database in the shared Postgres. The realm (`immiauto`), clients, mappers, and a demo user are imported from [docker/keycloak/realm-immiauto.json](docker/keycloak/realm-immiauto.json) on first start. Backend and MCP validate tokens by issuer (public URL) + audience, fetching JWKS over the internal network (`http://keycloak:8080`). The Angular SPA uses `keycloak-angular`/`keycloak-js`. Demo login: `demo` / `Passw0rd!`. Keycloak admin console: `http://localhost:8085` (`admin`/`admin` locally).
- **MCP Dynamic Client Registration (RFC 7591):** AI-assistant clients self-register with Keycloak; the MCP server gates `/mcp/**` on the **user's** realm role (`CONSULTANT_OWNER`/`ADMIN`) via `realm_access.roles`, not the (untrusted) client. The `mcp.*` client scopes and the open-registration policy are provisioned by the one-shot **`keycloak-config`** service ([docker/keycloak/configure-dcr.sh](docker/keycloak/configure-dcr.sh)) that runs `kcadm.sh` after Keycloak — **not** in the realm import, because a `clientScopes` array in a realm import suppresses Keycloak's built-in scopes (incl. `roles`). The script is idempotent; edit it (not a rendered copy) to change DCR provisioning. Anonymous/consent-free DCR is a **dev** posture — re-enable the removed policies for production.
- **Local LLM + chat UI:** `ollama` serves `qwen2.5:3b` (`OLLAMA_MODEL`) on the **NVIDIA GPU** (`deploy.resources.reservations.devices`; drop that block for CPU-only); `ollama-init` pulls the model once. `librechat` (config [docker/librechat/librechat.yaml](docker/librechat/librechat.yaml), MongoDB-backed) chats with Ollama and calls the MCP server via **OAuth** (DCR). This is why `KEYCLOAK_PUBLIC_URL` uses **`keycloak.localtest.me:8085`** not `localhost`: the MCP OAuth issuer must resolve to Keycloak from the browser **and** the LibreChat container. `*.localtest.me` is public DNS→`127.0.0.1`; compose maps the same name to the host gateway inside the container via `extra_hosts`. LibreChat secrets (`LIBRECHAT_CREDS_KEY/IV`, `JWT_*`) live in `.env`.

---

## 13. Worktrees

- Feature work happens in git worktrees under `.claude/worktrees/<name>/`.
- **On a successful PR merge to `main`, delete that PR's worktree directory.** Once the merge is confirmed (e.g. `gh pr view <n> --json state,mergedAt` shows it merged), run `git worktree remove .claude/worktrees/<name>` from the main checkout to remove it cleanly (add `--force` only if the tree has intended leftover files). Then prune the merged branch with `git branch -d <branch>`.
- Only remove a worktree after the merge is verified — never delete one with unmerged or uncommitted work. Don't delete the `main` checkout or the shared root `CLAUDE.md`.
- **A post-merge cleanup hook automates the above** ([.githooks/post-merge](.githooks/post-merge)). After a `git merge`/`git pull` on `main` it removes each worktree under `.claude/worktrees/` whose branch has landed, and prunes that branch.
  - It runs when you **pull merged work into `main`**, not when the PR merges — hooks are local, so nothing fires on GitHub's side. It does **not** run on `git pull --rebase` (no merge happens).
  - Enable the hooks in a fresh clone: `git config core.hooksPath .githooks` (per-clone config, so each checkout must run this once).
  - It calls `git worktree remove` and `git branch -d` without `--force`, so git refuses anything dirty or unmerged and the hook reports it instead of deleting it. Directories git doesn't track as worktrees are reported, never deleted.
  - It detects merged branches by ancestry. If the repo ever switches to squash merges, branch commits never become ancestors of `main`, so ancestry-based cleanup stops working.

---

## 14. Documentation (README)

> **[README.md](README.md) is the entry point for someone who has never seen this project. Keep it accurate — update it in the same change that alters how the project is set up, run, or understood.**

- **Whenever a change affects any of the following, update `README.md` as part of that change** (not as a follow-up):
  - **Deployment / run steps** — commands, prerequisites, ports, environment variables, `docker compose` usage, DB rebuild/reset steps, first-run setup.
  - **Technology / stack** — a new service, dependency, framework, language/runtime version, or external tool (e.g. a new compose service, a new model, an auth provider change).
  - **Architecture** — new components, how services talk to each other, data flow, auth flow, or the responsibilities of a module.
  - **Anything a third person needs to run or understand it** — new gotchas, required config, credentials/demo logins, troubleshooting for setup failures.
- **Write for a newcomer:** assume no prior context. Someone should be able to clone the repo and get the stack running from `README.md` alone.
- **Keep it in sync with reality** — if a step no longer works (e.g. a renamed service, a changed port, a new required env var), fix the README in the same commit. Don't let it drift.
- **Prefer the README for "how to run it" and CLAUDE.md for "how we work on it."** Operational/onboarding instructions belong in `README.md`; contributor conventions belong here.
- When you finish a change, ask yourself: *would a first-time reader following `README.md` still succeed?* If not, update it before considering the work done.

---

## 15. Configuration & environment variables

> **Every host, port, domain, and URL is configured through environment variables — `.env` is the single source of truth. Never hardcode a host/port/URL in a property or config file.**

- **`.env` holds only base values** (host ports, public URLs, internal URLs, realm name, client id, API base path). Copy from [.env.example](.env.example); keep the two in sync. Real secrets live in `.env` (gitignored) — never commit them.
- **Derive composite values in [docker-compose.yml](docker-compose.yml), never hand-copy them.** The OIDC issuer, JWKS URI, token endpoint, CORS origin, and MCP audit URL are all built from the base vars (e.g. `OIDC_ISSUER: ${KEYCLOAK_PUBLIC_URL}/realms/${KC_REALM}`). Change a domain/port in `.env` once and everything follows.
- **Public vs internal URLs are distinct.** Browser-facing endpoints use `*_PUBLIC_URL` (e.g. `KEYCLOAK_PUBLIC_URL`, `FRONTEND_PUBLIC_URL`); service-to-service calls over the Docker network use `*_INTERNAL_URL` (compose service names, e.g. `http://keycloak:8080`). The token **issuer** is the public URL; **JWKS/token** fetching uses the internal URL.
- **Spring services** read env with a dev fallback: `${ENV_VAR:sensible-default}` in `application.properties`. The default is for local `mvn`/`ng serve`; Docker overrides it via compose. Add new config this way — never a bare literal.
- **The Angular SPA uses runtime config, not build-time constants.** Values come from `window.__env` (served by `assets/env.js`), which nginx renders from env via `envsubst` at container start ([frontend/env.template.js](frontend/env.template.js), [frontend/docker-entrypoint.d/40-env-config.sh](frontend/docker-entrypoint.d/40-env-config.sh)). `environment.ts` reads `window.__env` with fallbacks. So changing the SPA's Keycloak/API host needs **no rebuild** — just env + restart. Never hardcode a URL in `environment*.ts`.
- **The Keycloak realm is a template.** [docker/keycloak/realm-immiauto.json.template](docker/keycloak/realm-immiauto.json.template) uses `${FRONTEND_PUBLIC_URL}` for redirect URIs / web origins; the `realm-init` service runs `envsubst` on it (the Keycloak image has no `envsubst`) into a shared volume before Keycloak imports it. Edit the `.template`, never a rendered copy.
- **When you add a new endpoint/URL/port to any service, wire it through this chain** (base var in `.env`/`.env.example` → derived in compose → consumed via `${ENV:default}` or `window.__env`), and update `README.md` per §14.