# QA — test strategy, review findings, gate records

**Related summary:** [`../Delivery approach.md`](../Delivery%20approach.md) §8 (testing strategy) and
the `release-gate` skill. **This folder holds the evidence** — what was tested, what a review found,
and whether a gate passed.

---

## Why this folder is load-bearing here

`DR-10` — **6 test files** covering ~30 services and 17 controllers — is the single largest risk in
the portfolio. Every refactor is unverifiable until it is fixed. QA is not a phase at the end of this
project; it is the thing that makes the other phases safe.

---

## What goes here

| Filename | For |
| --- | --- |
| `test-plan-<REQ-ID>.md` | What must be covered for a requirement, before the tests are written |
| `review-YYYY-MM-DD-<scope>.md` | A `security-reviewer` or code-review finding set |
| `gate-<phase>-YYYY-MM-DD.md` | A `release-gate` run, with evidence |
| `coverage-YYYY-MM-DD.md` | A coverage snapshot, and what is still untested |

---

## Test plan template

```markdown
# Test plan — <REQ-ID>

## Unit (fast, offline — mock Keycloak, DB, third-party APIs)
- [ ] <behaviour>

## Authorization  ← highest value on this codebase
- [ ] Cross-case access returns 404
- [ ] Cross-consultant access denied
- [ ] Admin override works where intended, and only there
- [ ] Disabled consultant cut off on the next request

## Integration (slower suite)
- [ ] <end-to-end path>

## Regression fixtures
- [ ] <fixture, in backend/src/test/resources/form-fixtures/>

## Explicitly not covered
<and why — an honest gap beats a false claim of coverage>
```

---

## Gate record template

```markdown
# Release gate — <phase> — YYYY-MM-DD

**Verdict:** PASS | PASS WITH ACCEPTED RISK | FAIL

| Gate | Result | Evidence |
| 1 Requirements  | | |
| 2 Security      | | |
| 3 Privacy       | | |
| 4 Boundary      | | |
| 5 Tests         | | <count + actual runner output> |
| 6 Documentation | | |
| 7 Deployment    | | <compose ps output> |
| 8 Operations    | | |

## Blocking failures
## Accepted risks
| Risk | Why accepted | Accepted by | Revisit |
```

---

## The rules that make QA meaningful (CLAUDE.md §9)

1. **A bug fix ships with a test that fails without the fix.** Write it first, watch it fail. If it passes before the fix, it is testing the wrong thing.
2. **Never delete or weaken a failing test to make the suite pass.** Fix the cause, or ask. This is absolute.
3. **Unit tests are fast and offline.** Live-dependency tests go in the separate, slower suite.
4. **Evidence, not assertion.** Paste the real output. A gate passed on a claim is a gate not run.
5. **Gate 4 — the product boundary — blocks on its own.** No eligibility decisions, no "requirements met" language, no artefact reaching a client unapproved, **no dynamic-XFA form marked fillable.** A blank-but-"successful" PDF that a consultant files on our assurance is the worst outcome this product can produce.

---

## Priority coverage targets

From the Section 4.1 §9 plan plus what the audit exposed:

| Area | Why it is first |
| --- | --- |
| Compliance calculators | `TravelHistoryService`, `PoliceCertificateService`, `LmiaCalculatorService` were **P0 correctness defects once**. Use the official worked examples as fixtures |
| Authorization on nested resources | IDOR was a P1 finding; these tests are the standing guard against its return |
| Forms domain | Snapshot assembly, transforms, PDF inspect/fill, validation, approval blocking, SHA-256 |
| Package lifecycle | Create → generate → readiness → approve → download → audit; changed hash blocks generation |

## Test data (CLAUDE.md §8)

Fixtures ship in the repository. **Never real PII** — `Jane Doe`, `AA000000`,
`applicant@example.com`, `1990-01-01`. **Never a real client document as a fixture** — synthesize one.

## Who writes here

`test-author` (test plans, coverage), `security-reviewer` (findings), the `release-gate` skill (gate
records), `deploy-verify` (deployment evidence within a gate).
