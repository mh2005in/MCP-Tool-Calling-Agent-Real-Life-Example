# Delivery Approach

> **How work gets executed on this project — the operating manual.**
> *What* to build: [Requirements.md](Requirements.md). *In what order*: [Plan.md](Plan.md).
> *How it is structured*: [Architecture.md](Architecture.md). *Where it stands*: [status dashboard.md](status%20dashboard.md).

**Last updated:** 2026-08-24

---

## 1. Operating principles

1. **Vertical slices, not horizontal layers.** A slice is migration → entity → repository → mapper → service → controller → DTO → frontend → tests → docs → deployed and verified. A slice that stops at "the backend compiles" is not delivered.
2. **Ask before changing.** CLAUDE.md §3 — describe the change and get permission before making it. This is not ceremony; it is how scope stays owned by the user.
3. **Never assume.** CLAUDE.md — when a requirement is ambiguous, ask with the question tool rather than picking a reading and building on it.
4. **A change is done when it runs in the container.** CLAUDE.md §12 — compiling is not evidence.
5. **Documentation moves with the code.** CLAUDE.md §14 — every affected document is updated in the same change, never as a follow-up.
6. **The audit trail is a feature.** Every artefact traces to a version, an input snapshot, an output hash, and an approver.
7. **Status comes from code, in both directions.** A document that says something is done is not evidence it is done — and a document that says something is outstanding is not evidence it is outstanding. §10a records what this project learned the hard way.

---

## 2. The vertical slice

The unit of delivery. Encoded in the **[`feature-slice`](skills/feature-slice/SKILL.md)** skill.

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

**Steps 9–11 are not optional and not deferred.** A slice that skips them creates the exact drift that
Phase 0.5 exists to clean up — and the current suite is proof of what accumulates: 6 backend/MCP test
files and 0 frontend files, against 37 services and 27 components.

### 2.1 Step 0 — before a slice starts

Added 2026-08-24. **Check whether the thing is already built.**

Verification found nine Section 4.1 items sitting in the register as `OPEN` that were fully
implemented in `CasePackageService`. Starting a slice on any of them would have meant rewriting
working code. One `grep` for the service, the endpoint, or the issue code would have caught it.

> Before implementing: grep for the symbol, the endpoint path, and the enum value. If they exist,
> the slice is a *verification and test* slice, not an implementation slice — and it should say so.

---

## 3. Definition of done

A requirement moves to `VERIFIED` only when **all** of these hold:

| # | Criterion | Source |
| --- | --- | --- |
| 1 | Acceptance criteria in [Requirements.md](Requirements.md) are met | — |
| 2 | Tests exist alongside the code; a bug fix has a test that fails without the fix | CLAUDE.md §9 |
| 3 | The build, lint, and test checks pass | CLAUDE.md §11 |
| 4 | The change runs in the Docker Compose stack, verified by [`deploy-verify`](agents/deploy-verify.md) | CLAUDE.md §12 |
| 5 | [README.md](../README.md) is accurate for a first-time reader | CLAUDE.md §14 |
| 6 | New config is wired through `.env` → compose → consumer, and documented | CLAUDE.md §15 |
| 7 | Any agent, skill, or hook encoding changed parameters is updated in the **same** change | CLAUDE.md §16 |
| 8 | No secrets or real PII in the diff; the staged diff was scanned | CLAUDE.md §8 |
| 9 | [status dashboard.md](status%20dashboard.md) and [change.log.md](change.log.md) are updated | — |
| 10 | Guardrail metric instrumented, not just the success metric | Phase 5 §7 |

### 3.1 Two grades of `VERIFIED`

The portfolio currently holds twenty `VERIFIED` requirements and six test files. Those cannot all mean
the same thing, and pretending they do is how a dashboard stops being useful.

| Grade | Basis | Where it is honest to use |
| --- | --- | --- |
| **Code-verified** | A named file and symbol implement it, read this verification run | A status pass over pre-existing work, where writing the missing tests is itself tracked (`DR-10`, `DR-14`) |
| **Test-verified** | Criterion 2 above is genuinely met | **Anything shipped from now on.** A new slice does not get to claim code-verified |

Every ✅ in the dashboard today is code-verified. The dashboard says so, and §11 of that document
explains why. **Do not let a new slice enter at the lower grade** — the backlog of untested working
code is a debt already booked as `DR-10`, not a precedent.

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

### Three rules that keep the structure honest

1. **A summary and its detail must not disagree.** When they do, the code decides which is right — then fix both. Detail that contradicts its summary is worse than no detail, because someone will act on it. The register once said Milestone 5 was "code-complete" and its eight tasks were "outstanding" in adjacent rows; that contradiction survived four months.
2. **[`input/`](input/) is read-only.** Never edit a source document to reflect a decision. Decisions go in [`design/`](design/) and [change.log.md](change.log.md); the corpus records what the source actually said.
3. **A dangling link is a missing file, not a formatting issue.** If a root document references `requirements/F41-01.md`, that file exists or the reference is wrong.

---

## 4. The automation roster

Fourteen artefacts. Each encodes a rule that would otherwise depend on memory. **A stale artefact is a
bug** (CLAUDE.md §16).

### 4.1 Agents — work that deserves its own context

| Agent | Model | Use it for | Writes code? |
| --- | --- | --- | --- |
| [`requirements-analyst`](agents/requirements-analyst.md) | opus | Parse a requirement doc, map it to the register, detect drift between docs and code | No — analysis only |
| [`backend-feature`](agents/backend-feature.md) | opus | Implement a backend vertical slice: entity → repository → mapper → service → controller → DTO | Yes |
| [`frontend-feature`](agents/frontend-feature.md) | sonnet | Implement the Angular half of a slice: service, component, route, endpoint constant | Yes |
| [`db-migration`](agents/db-migration.md) | sonnet | Author V-numbered SQL migrations; enforce GUID PKs and generated identifier columns | Yes |
| [`test-author`](agents/test-author.md) | sonnet | Write unit and integration tests; the bug-fix-needs-a-failing-test rule | Yes |
| [`security-reviewer`](agents/security-reviewer.md) | opus | Audit a diff against the Phase-4 controls, the product boundary, and PIPEDA obligations | No — reports |
| [`docs-sync`](agents/docs-sync.md) | sonnet | Reconcile README, CLAUDE.md, status dashboard, and change.log with what the code now does | Yes — docs only |
| [`deploy-verify`](agents/deploy-verify.md) | sonnet | Rebuild, redeploy, confirm health, exercise the endpoint | No — reports |

**Model policy** (CLAUDE.md §16): mechanical or well-scoped background work runs on `sonnet`. Opus is
reserved for deep reasoning and ambiguous judgment — requirements interpretation, backend design,
and security review.

**Report-only agents never apply fixes.** `requirements-analyst`, `security-reviewer`, and
`deploy-verify` have no `Edit` or `Write` tool. They diagnose; the main thread decides.

### 4.2 Skills — procedures loaded on demand

| Skill | Invoke when |
| --- | --- |
| [`requirement-intake`](skills/requirement-intake/SKILL.md) | A new requirement document lands in `HighLevelRequirement Pending/` and needs normalising into the register |
| [`feature-slice`](skills/feature-slice/SKILL.md) | Starting any vertical slice — the twelve-step procedure in §2 |
| [`release-gate`](skills/release-gate/SKILL.md) | Before declaring a phase complete or cutting a release — the P0 acceptance checklist |
| [`content-governance`](skills/content-governance/SKILL.md) | Publishing or retiring a form version, mapping version, checklist template, or rule |
| [`status-sync`](skills/status-sync/SKILL.md) | Regenerating the dashboard and changelog; verifying the three traceability invariants |
| [`worktree`](skills/worktree/SKILL.md) | Creating a worktree, or cleaning up after a merge |

### 4.3 Hooks — deterministic gates

In [`.githooks/`](../.githooks/) (CLAUDE.md §16):

| Hook | Enforces |
| --- | --- |
| `pre-commit` | §8 secret scan (gitleaks) + §11 author guard (`mh2005in`) |
| `commit-msg` | §11 attribution — blocks `Co-Authored-By` and agent footers |
| `post-merge` | §13 worktree cleanup after a merged branch is pulled into `main` |

**Setup on a fresh clone:** `git config core.hooksPath .githooks`, plus gitleaks
(`winget install gitleaks`) for the secret scan. **Until that command is run, a clone has no gates at
all** — and there is no CI to catch what the hooks miss (`SEC-15`).

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

**Batch related findings into one slice.** `DR-08`, `DR-09`, `DR-12`, and `DR-13` all live in two
properties files; four separate slices would mean four rebuild-and-verify cycles for one edit's worth
of change.

### Pattern B — a new feature slice

```
requirement-intake (skill)   normalise into Requirements.md; assign an ID
        ↓
feature-slice (skill)        drive the twelve steps — starting with step 0 (§2.1)
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
status-sync (skill)     verify traceability invariants; regenerate the dashboard FROM CODE
        ↓
release-gate (skill)    P0 acceptance checklist
        ↓
security-reviewer       full-diff review since the last gate
        ↓
deploy-verify           clean-stack bring-up from scratch
```

### Pattern D — a verification pass

Added 2026-08-24, because the project has now run two and they changed the plan both times.

```
Enumerate      entities, services, controllers, components, tests — counts, not impressions
        ↓
Challenge      for EACH status in the register, find the code or prove its absence
        ↓ (both directions — OPEN items may be built, VERIFIED items may not be)
Record         progress/<date>-verification-run.md — evidence with file:line
        ↓
Reconcile      Requirements, dashboard, Plan, Architecture — in the same change
        ↓
Log            change.log.md, naming every requirement whose status moved
```

**Parallelism rule:** independent agents launch in one batch. Dependent ones wait. Never spawn an
agent to re-derive context the main thread already has.

---

## 6. Git and branching

Per CLAUDE.md §11 and §13.

- **Branches:** `mh/<kebab-name>`. Never an agent name in a branch name.
- **Worktrees:** feature work lives in `.claude/worktrees/<name>/`. Use the [`worktree`](skills/worktree/SKILL.md) skill.
- **Commits:** focused; build, lint, and tests pass first. Author is `mh2005in`.
- **Never** add Claude as author or co-author — no `Co-Authored-By`, no agent attribution in commit messages or PR bodies, no references to Claude or CLAUDE.md in commit messages. The `commit-msg` hook enforces this for commits; **PR bodies are not covered — keep the rule by hand.**
- **Don't commit or push unless asked** (§11).
- After a PR merges to `main` and the merge is pulled locally, the `post-merge` hook removes the worktree and prunes the branch. It does **not** fire on `git pull --rebase`, and it does not fire when the PR merges on GitHub — hooks are local.

---

## 7. Secrets and PII

CLAUDE.md §8. The most consequential rule on a project handling passport numbers and dates of birth.

- **Secrets** — API keys, tokens, client secrets, passwords, connection strings, certificates. Placeholder them before committing. Real values live in environment variables.
- **PII** — names, dates of birth, passport and licence numbers, addresses, emails, phone numbers, and any uploaded or generated client document. Use obviously fake samples (`Jane Doe`, `AA000000`, `applicant@example.com`) in code, tests, fixtures, seed data, and documentation.
- **Never commit data artifacts** — uploaded documents, database dumps, exports, or logs containing real data.
- **Scan the staged diff before every commit.**
- **gitleaks matches secret patterns, not PII.** It will not catch a real name or email. The placeholder rule is the actual control; the hook is a safety net.
- A committed secret is **compromised** — rotate it, do not merely amend the commit. Committed real PII must be raised immediately; history scrubbing may be required.

**Two live examples of why this is not theoretical.** `application.properties` ships a real-looking
default password (`DR-09`) and turns on SQL logging in the main profile (`DR-08`), which writes bound
parameters — passport numbers among them — into the logs of every environment.

---

## 8. Testing strategy

CLAUDE.md §9, plus the Section 4.1 §9 plan. **This is the project's largest single debt.**
Detailed plan: [`qa/DR-10-test-foundation.md`](qa/DR-10-test-foundation.md).

| Layer | Scope | Speed | Today |
| --- | --- | --- | ---: |
| **Backend unit** | Snapshot assembly, transforms, PDF inspect and fill, validation rules, approval blocking, SHA-256, access enforcement | Fast, offline — mock Keycloak, the database, and third-party APIs | **2 files** |
| **Backend integration** | Create package → generate draft → readiness → approve → download → audit entries; changed-source-hash blocks generation | Slower suite, separate | **0** |
| **MCP** | Tool mapping, template resolution, security config, executor | Fast | **4 files** |
| **Frontend** | Profile selection, validation grouping, approval button disabled with errors, approval payload, status badges | Fast | **0 files** |
| **Security** | Cross-tenant access, IDOR on every nested resource, authorization on every endpoint | Part of the gate | **0** |
| **Regression fixtures** | `backend/src/test/resources/form-fixtures/` — round-trip proof per onboarded form | Per form | **0** |

**Priority order for the foundation** (`DR-10`, `DR-14`):

1. **Characterisation tests for the nine `F41` items just promoted to `VERIFIED`.** They are the largest body of working, untested, business-critical code in the repository, and nothing currently stops a refactor from breaking the approval gate silently.
2. **`BL-07` masking and `BL-10` calculators.** Silent-failure logic guarding `BR-1` and `BR-5`.
3. **A failing-without-the-fix test per drift item**, written as each is fixed.
4. **Frontend specs** for `api.service`, the auth guards, and the four forms-package components.

**Non-negotiables**

- A bug fix ships with a test that **fails without the fix**.
- **Never delete or weaken a failing test to make the suite pass** — fix the cause, or ask.
- Unit tests are fast and offline. Anything needing a live database or network belongs in the slower suite.

---

## 9. Content governance

Immigration content changes independently of application code. Encoded in the
[`content-governance`](skills/content-governance/SKILL.md) skill.

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

- **Separation of duties** — the author is not the approver. *Not yet enforced in code; that is `GAP-09`.*
- **Every rule carries an authoritative citation**, effective date, owner, reviewer, and change reason.
- **Rollback is a first-class operation**, not a re-edit.
- **`sourceSha256` changes ⇒ re-inspect.** A form that changed upstream is not the form we mapped. Enforced at generation (`CaseFormGenerationService` :285); not yet in the readiness layer.
- **Never mark a dynamic-XFA form fillable.** Emitting a blank-but-"successful" PDF is a trust failure (Phase 5 §4.1). Today the engine cannot tell static XFA from dynamic — see `F41-01`.

---

## 10. Working with requirement documents

The upstream folder is `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`.

- **PDFs are extracted, never guessed at.** `pdftotext -layout <file> <out>` — the layout flag preserves the tables these documents rely on.
- **A document marked "Completed" is a claim, not evidence.**
- **A backlog listing a task as remaining is also a claim, not evidence.**
- **When a document contradicts the code, the code wins** as a statement of fact; the contradiction becomes a `DR-*` entry and a decision.
- **A new document is normalised through [`requirement-intake`](skills/requirement-intake/SKILL.md)** before any implementation starts — an unnormalised requirement has no ID, no acceptance criteria, and no place in the plan.

### 10a. What two verification passes taught

Both passes changed the plan. They failed in opposite directions, and the second is the less obvious one.

| Pass | Trusted | Found | Lesson |
| --- | --- | --- | --- |
| **2026-08-21** | `HighLevelRequirement Completed/` | 11 open items inside signed-off work; a backlog naming one broken controller when there were three | Documents **overstate** completeness |
| **2026-08-24** | `Section-4.1-Backlog.md` | 9 delivered items recorded as `OPEN`; inventory understated by a third; a self-contradicting register row nobody had reconciled | Documents **understate** it too |

**The rule that follows from both:** a status copied from a document is wrong in whichever direction
that document was wrong. Neither optimism nor pessimism is the safe default — reading the code is.

Two habits that would have caught the 08-24 errors earlier:

- **Reconcile contradictions when you write them.** "M5 code-complete" and "`F41-03`…`F41-10` outstanding" appeared in the same document, four months apart in origin, and were never compared.
- **Count, don't estimate.** "22 entities" and "30 services" were carried forward as remembered figures. `find | wc -l` takes a second and returned 32 and 37.

---

## 11. Communication

- **After completing work, summarise the changes and name the affected files** (CLAUDE.md §3).
- **Report outcomes faithfully.** If tests fail, say so with the output. If a step was skipped, say that. If a status improved because the previous reading was wrong rather than because work was done, **say that too** — the distinction matters to anyone planning from it.
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
