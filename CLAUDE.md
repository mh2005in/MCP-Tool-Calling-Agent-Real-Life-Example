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
├── backend/      # Spring Boot (Java) API
├── frontend/     # Angular 18 SPA (served by nginx in Docker)
├── MCPServer/    # Spring Boot MCP server (AI tools)
├── docker/       # Per-service configs + DB/Keycloak/LibreChat bootstrap
└── docs/         # Project documentation
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
- **A pre-commit secret scanner (gitleaks) runs automatically** ([.githooks/pre-commit](.githooks/pre-commit) runs `gitleaks git --staged`) and blocks commits whose staged changes contain likely secrets. Hook setup — installing gitleaks and enabling `core.hooksPath` — is in §16.
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
  - **A pre-commit author guard enforces this** ([.githooks/pre-commit](.githooks/pre-commit)): it blocks any commit whose effective author (`git var GIT_AUTHOR_IDENT`) isn't `mh2005in`. Fix with `git config user.name 'mh2005in'`; bypass a deliberate exception with `git commit --no-verify`. It checks the **author identity only**; the message-attribution rule below is enforced separately by the `commit-msg` hook.
- **Never add Claude as an author or co-author.** No `Co-Authored-By` trailer, no agent attribution in commit messages or PR bodies, and no references to Claude/CLAUDE.md in commit messages.
  - **A commit-msg hook enforces this for commit messages** ([.githooks/commit-msg](.githooks/commit-msg)): it blocks any `Co-Authored-By:` trailer or `Generated with Claude` footer. It's **attribution-only** — neutral mentions like "Update CLAUDE.md" are allowed. **PR bodies live on GitHub and are not covered — keep the rule for PRs by hand.** Bypass a genuine human co-author with `git commit --no-verify`.
- **Temp/working branches: `mh/<kebab-name>`** — prefix with `mh/`, then a short kebab-case description of the work. Never put an agent name in the branch name.

---

## 12. Docker & Deployment

> **The whole project runs as a Docker Compose stack. After every change, build and deploy it via Docker Compose and confirm it comes up.**

- The stack ([docker-compose.yml](docker-compose.yml)) is `postgres` + `keycloak` + `backend` + `mcpserver` + `frontend` (nginx) + the `ollama`/`librechat` local-LLM chat tier. Bring it up with `docker compose up -d --build`. **The full service list, published ports, DB init/reset, auth/Keycloak, MCP DCR, and the Ollama/LibreChat tier are documented in [README.md](README.md) — that is the source of truth; don't restate it here.**
- **After any change, rebuild and redeploy the affected service(s)** and verify the stack is healthy (`docker compose ps`, then exercise the affected endpoint) — don't consider a change done until it runs in the container. Delegate this to the **`deploy-verify`** subagent ([.claude/agents/deploy-verify.md](.claude/agents/deploy-verify.md)), which does exactly this in its own context.
- **`deploy-verify` hardcodes the app's parameters** (service names, host ports, health/liveness endpoints) copied from [docker-compose.yml](docker-compose.yml) and [.env.example](.env.example). **Any change to those parameters must update the agent in the same change** so it can't drift — this is part of the config chain in §15.
- **Services talk to each other by compose service name over the internal network** (`postgres:5432`, `backend:8080`), never `localhost` or the published host ports. The frontend reaches the API same-origin via the nginx `/api` proxy.
- **New runtime config → add it to the service's `environment:` in docker-compose and document it in [.env.example](.env.example).** Real values go in `.env` (gitignored) — never commit secrets.
- **New dependency (a service, model, etc.) → add it as a compose service** and wire the dependent service's `depends_on`/env to reach it.
- **Editing the auth/DCR/LLM setup? Change the source of truth, never a rendered copy:** Keycloak DCR provisioning lives in [docker/keycloak/configure-dcr.sh](docker/keycloak/configure-dcr.sh) (not the realm import — a `clientScopes` array there suppresses Keycloak's built-in `roles` scope); the realm in [realm-immiauto.json.template](docker/keycloak/realm-immiauto.json.template); LibreChat wiring in [docker/librechat/librechat.yaml](docker/librechat/librechat.yaml). Anonymous/consent-free DCR is a **dev** posture — re-enable the removed policies for production.

---

## 13. Worktrees

- Feature work happens in git worktrees under `.claude/worktrees/<name>/`. Use the **`worktree`** skill ([.claude/skills/worktree/SKILL.md](.claude/skills/worktree/SKILL.md)) to create one and to clean up after a merge — it packages the commands and safety rules below.
- **On a successful PR merge to `main`, delete that PR's worktree directory.** Once the merge is confirmed (e.g. `gh pr view <n> --json state,mergedAt` shows it merged), run `git worktree remove .claude/worktrees/<name>` from the main checkout to remove it cleanly (add `--force` only if the tree has intended leftover files). Then prune the merged branch with `git branch -d <branch>`.
- Only remove a worktree after the merge is verified — never delete one with unmerged or uncommitted work. Don't delete the `main` checkout or the shared root `CLAUDE.md`.
- **A post-merge cleanup hook automates the above** ([.githooks/post-merge](.githooks/post-merge)). After a `git merge`/`git pull` on `main` it removes each worktree under `.claude/worktrees/` whose branch has landed, and prunes that branch.
  - It runs when you **pull merged work into `main`**, not when the PR merges — hooks are local, so nothing fires on GitHub's side. It does **not** run on `git pull --rebase` (no merge happens). (Hook setup is in §16.)
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

- **`.env` holds base values; composite values are derived in [docker-compose.yml](docker-compose.yml), never hand-copied** (OIDC issuer, JWKS URI, token endpoint, CORS origin, MCP audit URL are all built from base vars). Public browser-facing URLs use `*_PUBLIC_URL`; internal service-to-service calls use `*_INTERNAL_URL`. **The full variable list and derivation chain are in [README.md](README.md) "Configuration" — that is the source of truth.**
- **Consume config the established way, never a bare literal:** Spring reads `${ENV:default}` in `application.properties` (Docker overrides via compose); the Angular SPA reads `window.__env` (nginx renders it via `envsubst`, so host/API changes need **no rebuild** — never hardcode a URL in `environment*.ts`); the Keycloak realm uses `${FRONTEND_PUBLIC_URL}` in [realm-immiauto.json.template](docker/keycloak/realm-immiauto.json.template) (edit the `.template`, never a rendered copy).
- **When you add or change an endpoint/URL/port, wire it through the chain** — base var in `.env`/`.env.example` → derived in compose → consumed via `${ENV:default}` or `window.__env` — and update `README.md` per §14 **and the `deploy-verify` agent per §12** (it hardcodes service names, ports, and health endpoints).

---

## 16. Automation — agents, skills & hooks

Some guidelines here are backed by tooling, not just prose. Keep the tooling and the rules it encodes in sync — a stale agent/skill/hook is a bug.

- **Current artifacts:**
  - `deploy-verify` **agent** ([.claude/agents/deploy-verify.md](.claude/agents/deploy-verify.md)) — §12 rebuild-and-verify.
  - `worktree` **skill** ([.claude/skills/worktree/SKILL.md](.claude/skills/worktree/SKILL.md)) — §13 worktree create/cleanup.
  - **hooks** ([.githooks/](.githooks/)) — `pre-commit` (§8 secrets + §11 author guard), `commit-msg` (§11 attribution), `post-merge` (§13 worktree cleanup).
- **Hook setup (fresh clone, one-time):** enable the hooks with `git config core.hooksPath .githooks` (per-clone config). The `pre-commit` secret scan additionally needs **gitleaks** — Windows `winget install gitleaks` (or `scoop`/`choco`), macOS `brew install gitleaks` (see https://github.com/gitleaks/gitleaks#installing); without it that scan is skipped (the author guard still runs).
- **Keep them updated.** When a change alters what an artifact encodes — parameters (service names/ports/endpoints), a workflow, or a rule — update that artifact in the **same change** so it can't drift (see the deploy-verify sync rule in §12/§15). When you add a new artifact, list it here.
- **When the user proposes a CLAUDE.md change, first classify it** — decide whether the rule is better delivered (or additionally enforced) as an agent, skill, or hook, say so, then follow the user's instruction (§3):
  - **Hook** — a deterministic "**every time** X / **before/after** Y" rule that can be machine-checked (commit gates, post-merge steps). Fires on events, so it can't be forgotten.
  - **Skill** — an occasional, task-specific **procedure** loaded on demand. Keeps always-on context small.
  - **Agent** — a self-contained task worth its **own context** (build/verify, broad search), especially if independent or parallelizable.
  - **Stay in CLAUDE.md** — an always-on principle that shapes most actions and can't be conditionally loaded.
  Recommend the best home, note whether it also needs a CLAUDE.md pointer, and implement per §3 (ask first).