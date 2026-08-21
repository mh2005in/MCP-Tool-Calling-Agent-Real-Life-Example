# Delivery Approach

> **How work gets executed on this project — the operating manual.**
> *What* to build: [Requirements.md](Requirements.md). *In what order*: [Plan.md](Plan.md).
> *How it is structured*: [Architecture.md](Architecture.md).

**Last updated:** 2026-08-21

---

## 1. Operating principles

1. **Vertical slices, not horizontal layers.** A slice is migration → entity → repository → mapper → service → controller → DTO → frontend → tests → docs → deployed and verified. A slice that stops at "the backend compiles" is not delivered.
2. **Ask before changing.** CLAUDE.md §3 — describe the change and get permission before making it. This is not ceremony; it is how scope stays owned by the user.
3. **Never assume.** CLAUDE.md — when a requirement is ambiguous, ask with the question tool rather than picking a reading and building on it.
4. **A change is done when it runs in the container.** CLAUDE.md §12 — compiling is not evidence.
5. **Documentation moves with the code.** CLAUDE.md §14 — the README is updated in the same change that alters how the project is set up, run, or understood. Never as a follow-up.
6. **The audit trail is a feature.** Every artefact traces to a version, an input snapshot, an output hash, and an approver.

---

## 2. The vertical slice

The unit of delivery. Encoded in the **`feature-slice`** skill.

```
1. Understand    read the requirement; check Requirements.md for its ID and acceptance criteria
2. Impact        which modules move? (CLAUDE.md §3) — backend, frontend, MCP, docker, docs
3. Propose       describe the change; ask permission (CLAUDE.md §3)
4. Migrate       V-numbered SQL; GUID PKs; generated human-facing identifiers (§7)
5. Model         entity → repository → MapStruct mapper → DTO
6. Serve         service layer; reuse CommonService/CommonUtil before writing new (§5)
7. Expose        controller with @PreAuthorize and @Valid; DTOs only
8. Consume       Angular feature; API_ENDPOINTS; window.__env — never a hardcoded URL
9. Test          alongside the code; a bug fix ships with a test that fails without the fix (§9)
10. Document     README (§14) · CLAUDE.md if a convention changed · change.log.md · status dashboard.md
11. Verify       deploy-verify agent — rebuild, redeploy, exercise the endpoint (§12)
12. Review       security-reviewer on anything touching auth, PII, uploads, or tenancy
```

**Steps 9–11 are not optional and not deferred.** A slice that skips them creates the exact drift
that Phase 0.5 exists to clean up.

---

## 3. Definition of done

A requirement moves to `VERIFIED` only when **all** of these hold:

| # | Criterion | Source |
| --- | --- | --- |
| 1 | Acceptance criteria in [Requirements.md](Requirements.md) are met | — |
| 2 | Tests exist alongside the code; a bug fix has a test that fails without the fix | CLAUDE.md §9 |
| 3 | The build, lint, and test checks pass | CLAUDE.md §11 |
| 4 | The change runs in the Docker Compose stack, verified by `deploy-verify` | CLAUDE.md §12 |
| 5 | [README.md](../README.md) is accurate for a first-time reader | CLAUDE.md §14 |
| 6 | New config is wired through `.env` → compose → consumer, and documented | CLAUDE.md §15 |
| 7 | Any agent, skill, or hook encoding changed parameters is updated in the **same** change | CLAUDE.md §16 |
| 8 | No secrets or real PII in the diff; the staged diff was scanned | CLAUDE.md §8 |
| 9 | [status dashboard.md](status%20dashboard.md) and [change.log.md](change.log.md) are updated | — |
| 10 | Guardrail metric instrumented, not just the success metric | Phase 5 §7 |

---

## 3a. Where work is recorded

Six documents at `.claude/` root are the **summaries**. Eight folders hold the **detail**. Every
agent and skill writes into this structure — that obligation is what stops it going stale.

| Folder | Holds | Written by |
| --- | --- | --- |
| [`requirements/`](requirements/) | `<ID>.md` — detail for requirements too big for a register row | `requirements-analyst`, `requirement-intake` |
| [`design/`](design/) | `ADR-nnn-<slug>.md` decisions, `<ID>-design.md` technical designs | `requirements-analyst`, `backend-feature`, `security-reviewer` |
| [`plan/`](plan/) | Phase breakdowns, slice plans, re-sequencing records | `feature-slice`, `docs-sync` |
| [`progress/`](progress/) | Dated snapshots, verification runs, slice completions | `status-sync`, `docs-sync`, `feature-slice` |
| [`qa/`](qa/) | Test plans, review findings, gate records, coverage | `test-author`, `security-reviewer`, `release-gate` |
| [`operations/`](operations/) | Runbooks, deployment records, incidents | `deploy-verify`, `docs-sync`, `security-reviewer` |
| [`input/`](input/) | The source requirement corpus — **read-only** | `requirement-intake` (additions only) |
| [`memory/`](memory/) | Durable project facts — gotchas, constraints, dead ends | any agent that learns one |

**Each folder's README states its conventions and templates.** Read it before adding a file.

### The two memory scopes

| | `.claude/memory/` | User memory (`~/.claude/projects/…/memory/`) |
| --- | --- | --- |
| Scope | The project | The individual |
| Committed | Yes — shared, version-controlled | No — local, private |
| Written by | Agents and skills | The assistant only |
| Holds | Gotchas, constraints, rejected approaches, environment quirks | Personal working preferences and style |

**The test:** would a different person on this project need to know it? → `.claude/memory/`. Is it
about how one person likes to work? → user memory. A project fact recorded only in user memory is
invisible to everyone else.

### Two rules that keep the structure honest

1. **A summary and its detail must not disagree.** When they do, the code decides which is right — then fix both. Detail that contradicts its summary is worse than no detail, because someone will act on it.
2. **[`input/`](input/) is read-only.** Never edit a source document to reflect a decision. Decisions go in [`progress/`](progress/) and the affected root doc; the corpus records what the source actually said.

---

## 4. The automation roster

Twelve artefacts. Each encodes a rule that would otherwise depend on memory. **A stale artefact is a
bug** (CLAUDE.md §16).

### 4.1 Agents — work that deserves its own context

| Agent | Model | Use it for | Writes code? |
| --- | --- | --- | --- |
| `requirements-analyst` | opus | Parse a requirement doc, map it to the register, detect drift between docs and code | No — analysis only |
| `backend-feature` | opus | Implement a backend vertical slice: entity → repository → mapper → service → controller → DTO | Yes |
| `frontend-feature` | sonnet | Implement the Angular half of a slice: service, component, route, endpoint constant | Yes |
| `db-migration` | sonnet | Author V-numbered SQL migrations; enforce GUID PKs and generated identifier columns | Yes |
| `test-author` | sonnet | Write unit and integration tests; the bug-fix-needs-a-failing-test rule | Yes |
| `security-reviewer` | opus | Audit a diff against the Phase-4 controls, the product boundary, and PIPEDA obligations | No — reports |
| `docs-sync` | sonnet | Reconcile README, CLAUDE.md, status dashboard, and change.log with what the code now does | Yes — docs only |
| `deploy-verify` | sonnet | Rebuild, redeploy, confirm health, exercise the endpoint *(pre-existing)* | No — reports |

**Model policy** (CLAUDE.md §16): mechanical or well-scoped background work runs on `sonnet`. Opus is
reserved for deep reasoning and ambiguous judgment — requirements interpretation, backend design,
and security review.

### 4.2 Skills — procedures loaded on demand

| Skill | Invoke when |
| --- | --- |
| `requirement-intake` | A new requirement document lands in `HighLevelRequirement Pending/` and needs normalising into the register |
| `feature-slice` | Starting any vertical slice — the twelve-step procedure in §2 |
| `release-gate` | Before declaring a phase complete or cutting a release — the P0 acceptance checklist |
| `content-governance` | Publishing or retiring a form version, mapping version, checklist template, or rule |
| `status-sync` | Regenerating the dashboard and changelog; verifying the three traceability invariants |
| `worktree` | Creating a worktree, or cleaning up after a merge *(pre-existing)* |

### 4.3 Hooks — deterministic gates

Pre-existing, in `.githooks/` (CLAUDE.md §16):

| Hook | Enforces |
| --- | --- |
| `pre-commit` | §8 secret scan (gitleaks) + §11 author guard (`mh2005in`) |
| `commit-msg` | §11 attribution — blocks `Co-Authored-By` and agent footers |
| `post-merge` | §13 worktree cleanup after a merged branch is pulled into `main` |

**Setup on a fresh clone:** `git config core.hooksPath .githooks`, plus gitleaks
(`winget install gitleaks`) for the secret scan.

---

## 5. Orchestration patterns

How the roster combines on real work.

### Pattern A — a drift fix (Phase 0.5)

```
requirements-analyst   confirm the finding still reproduces; identify blast radius
        ↓
backend-feature        apply the fix                    ← ask permission first (§3)
        ↓
test-author            write the test that fails without the fix   (§9)
        ↓
deploy-verify          rebuild, redeploy, confirm healthy          (§12)
        ↓
docs-sync              status dashboard + change.log
```

### Pattern B — a new feature slice

```
requirement-intake (skill)   normalise into Requirements.md; assign an ID
        ↓
feature-slice (skill)        drive the twelve steps
        ↓
db-migration ─┐
backend-feature ├─ in parallel where independent
frontend-feature ┘
        ↓
test-author            unit + integration
        ↓
security-reviewer      if the slice touches auth, PII, uploads, or tenancy
        ↓
deploy-verify          rebuild and exercise
        ↓
docs-sync              README (§14) + dashboard + change.log
```

### Pattern C — a phase gate

```
status-sync (skill)     verify traceability invariants; regenerate the dashboard
        ↓
release-gate (skill)    P0 acceptance checklist
        ↓
security-reviewer       full-diff review since the last gate
        ↓
deploy-verify           clean-stack bring-up from scratch
```

**Parallelism rule:** independent agents launch in one batch. Dependent ones wait. Never spawn an
agent to re-derive context the main thread already has.

---

## 6. Git and branching

Per CLAUDE.md §11 and §13.

- **Branches:** `mh/<kebab-name>`. Never an agent name in a branch name.
- **Worktrees:** feature work lives in `.claude/worktrees/<name>/`. Use the `worktree` skill.
- **Commits:** focused; build, lint, and tests pass first. Author is `mh2005in`.
- **Never** add Claude as author or co-author — no `Co-Authored-By`, no agent attribution in commit messages or PR bodies, no references to Claude or CLAUDE.md in commit messages. The `commit-msg` hook enforces this for commits; **PR bodies are not covered — keep the rule by hand.**
- **Don't commit or push unless asked** (§11).
- After a PR merges to `main` and the merge is pulled locally, the `post-merge` hook removes the worktree and prunes the branch.

---

## 7. Secrets and PII

CLAUDE.md §8. The most consequential rule on a project handling passport numbers and dates of birth.

- **Secrets** — API keys, tokens, client secrets, passwords, connection strings, certificates. Placeholder them before committing. Real values live in environment variables.
- **PII** — names, dates of birth, passport and licence numbers, addresses, emails, phone numbers, and any uploaded or generated client document. Use obviously fake samples (`Jane Doe`, `AA000000`, `applicant@example.com`) in code, tests, fixtures, seed data, and documentation.
- **Never commit data artifacts** — uploaded documents, database dumps, exports, or logs containing real data.
- **Scan the staged diff before every commit.**
- **gitleaks matches secret patterns, not PII.** It will not catch a real name or email. The placeholder rule is the actual control; the hook is a safety net.
- A committed secret is **compromised** — rotate it, do not merely amend the commit. Committed real PII must be raised immediately; history scrubbing may be required.

---

## 8. Testing strategy

CLAUDE.md §9, plus the Section 4.1 §9 plan.

| Layer | Scope | Speed |
| --- | --- | --- |
| **Backend unit** | Snapshot assembly, transforms, PDF inspect and fill, validation rules, approval blocking, SHA-256, access enforcement | Fast, offline — mock Keycloak, the database, and third-party APIs |
| **Backend integration** | Create package → generate draft → readiness → approve → download → audit entries; changed-source-hash blocks generation | Slower suite, separate |
| **Frontend** | Profile selection, validation grouping, approval button disabled with errors, approval payload, status badges | Fast |
| **Security** | Cross-tenant access, IDOR on every nested resource, authorization on every endpoint | Part of the gate |
| **Regression fixtures** | `backend/src/test/resources/form-fixtures/` — round-trip proof per onboarded form | Per form |

**Non-negotiables**

- A bug fix ships with a test that **fails without the fix**.
- **Never delete or weaken a failing test to make the suite pass** — fix the cause, or ask.
- Unit tests are fast and offline. Anything needing a live database or network belongs in the slower suite.

---

## 9. Content governance

Immigration content changes independently of application code. Encoded in the `content-governance` skill.

```
Source registry → Draft → Review (second person) → Approve → Publish (effective-dated) → Retire
                                    │
                          Impact analysis: which templates, open cases,
                          generated forms, deadlines, advice content?
                                    │
                          Regression pack over representative case profiles
                                    │
                          Release note: informational vs action-required
```

Rules that do not bend:

- **Separation of duties** — the author is not the approver.
- **Every rule carries an authoritative citation**, effective date, owner, reviewer, and change reason.
- **Rollback is a first-class operation**, not a re-edit.
- **`sourceSha256` changes ⇒ re-inspect.** A form that changed upstream is not the form we mapped.
- **Never mark a dynamic-XFA form fillable.** Emitting a blank-but-"successful" PDF is a trust failure (Phase 5 §4.1).

---

## 10. Working with requirement documents

The upstream folder is `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`.

- **PDFs are extracted, never guessed at.** `pdftotext -layout <file> <out>` — the layout flag preserves the tables these documents rely on.
- **A document marked "Completed" is a claim, not evidence.** The 2026-08-21 audit found 11 open items in signed-off work. Verify against code.
- **When a document contradicts the code, the code wins** as a statement of fact; the contradiction becomes a `DR-*` entry and a decision.
- **A new document is normalised through `requirement-intake`** before any implementation starts — an unnormalised requirement has no ID, no acceptance criteria, and no place in the plan.

---

## 11. Communication

- **After completing work, summarise the changes and name the affected files** (CLAUDE.md §3).
- **Report outcomes faithfully.** If tests fail, say so with the output. If a step was skipped, say that.
- **Flag concerns once, then proceed.** If the user reaffirms, that is the decision — build it in full under stated assumptions.
- **Scaling the work down is the user's call.** If part of the scope is blocked, finish everything else and say explicitly what was left out and why.

---

## 12. Keeping this document honest

This file describes the operating model. When the model changes — a new agent, a changed hook, a
different definition of done — update it in the **same change**, and list the artefact in CLAUDE.md
§16. Per §16, when a new rule is proposed, first classify where it belongs:

| Home | When |
| --- | --- |
| **Hook** | A deterministic "every time X / before-after Y" rule that can be machine-checked. Fires on events, so it cannot be forgotten |
| **Skill** | An occasional, task-specific procedure loaded on demand. Keeps always-on context small |
| **Agent** | A self-contained task worth its own context — build/verify, broad search — especially if parallelizable |
| **CLAUDE.md** | An always-on principle that shapes most actions and cannot be conditionally loaded |
