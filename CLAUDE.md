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

- The stack is defined in [docker-compose.yml](docker-compose.yml): `postgres` + `backend` + `mcpserver` + `frontend` (Angular served by nginx). Bring it up with:

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