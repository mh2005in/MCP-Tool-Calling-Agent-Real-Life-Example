# Memory — durable project context

**Shared, committed, team-visible.** Facts that survive across sessions and that a newcomer — human or
agent — would otherwise have to rediscover the hard way.

---

## Two memory scopes, and the line between them

| | **This folder** (`.claude/memory/`) | **User memory** (`~/.claude/projects/…/memory/`) |
| --- | --- | --- |
| **Scope** | The project | The individual working on it |
| **Committed** | Yes — version-controlled, shared | No — local, private |
| **Written by** | Agents and skills | The assistant only |
| **Holds** | Gotchas, rejected approaches, hard-won constraints, environment quirks | Personal working preferences, working style, tool habits |
| **Example** | "PDFBox cannot re-save encrypted+certified IRCC forms — proven, don't retry" | "Prefers comprehensive artifact rosters over minimal ones" |

**The test:** would a *different person* on this project need to know it? → here. Is it about *how
this particular person likes to work*? → user memory.

**Never put a project fact only in user memory** — it becomes invisible to everyone else. **Never put
a personal preference here** — it does not belong in a shared repo.

---

## What goes here

| Filename | For |
| --- | --- |
| `<slug>.md` | One fact or closely-related cluster — e.g. `pdfbox-xfa-wall.md`, `flyway-disabled.md` |

## Template

```markdown
# <title>

**Type:** gotcha | constraint | rejected-approach | environment | context
**Learned:** YYYY-MM-DD
**Related:** <requirement IDs, ADRs, files>

## The fact
<stated plainly, in one or two sentences>

## Why it matters
<what goes wrong if someone doesn't know this>

## Evidence
<how we know — a PoC, an error, a file:line>
```

---

## What is worth writing

**Write it when:**

- Something was tried and **provably does not work** — so nobody spends a week rediscovering it. `F41-14` is the archetype: a PoC proved PDFBox cannot re-save IRCC's encrypted, certified forms into an Adobe-valid file. That is a week of someone's life, saved.
- An environment quirk bites. *Flyway is disabled — migrations are manual.* *`pdftotext` needs `-layout` or the requirement tables turn to mush.*
- A constraint is real but non-obvious from the code.
- An approach was **rejected for a reason that still holds** — SaaS form-fill APIs (PII egress), iText under AGPL (licensing).

**Don't write it when:**

- The repository already records it — code structure, git history, CLAUDE.md, the README.
- It only matters to one conversation.
- It is a status. Statuses live in [`../status dashboard.md`](../status%20dashboard.md) and go stale here.
- It is a decision with reasoning. Those are ADRs in [`../design/`](../design/).

---

## Rules

- **One fact per file.** A file holding five things gets read for one and skimmed past the rest.
- **Absolute dates**, never relative. "Recently" is meaningless six months on.
- **Verify before relying.** A memory reflects what was true when written. If it names a file, function, or flag, check it still exists.
- **Delete what turns out to be wrong.** A stale memory is worse than none — it is confidently misleading.
- **No real PII, secrets, or infrastructure identifiers** (CLAUDE.md §8).
- **Link liberally** — `[[other-memory]]`. A link to something not yet written marks it as worth writing.

## Who writes here

Any agent that learns something durable. Most often `requirements-analyst` (constraints found while
reconciling), `backend-feature` and `db-migration` (environment quirks), `security-reviewer`
(rejected approaches), `deploy-verify` (deployment gotchas).
