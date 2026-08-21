---
name: content-governance
description: >-
  Publish, update, or retire governed immigration content — a form version, field
  mapping version, checklist template, or conditional rule — through the
  draft-review-approve-publish-retire lifecycle with impact analysis and rollback.
  Use when onboarding an IRCC form, changing a mapping, editing checklist templates
  or rules, or responding to an upstream IRCC/ESDC change.
---

# Content governance

Immigration content changes **independently of application code**. IRCC revises a form; a rule's
effective date passes; a checklist gains a required document. None of that is a code release, and
treating it as one is why firms end up filing on stale content.

This is `GAP-09` (Phase 5 §4.9) and `BR-3`.

> Phase 5 §4.9: *Software alone cannot guarantee current immigration content; ownership, review
> cadence, sources, and response targets are part of the product.*

---

## What counts as governed content

| Content | Entity | Versioned by |
| --- | --- | --- |
| Official form file | `FormDefinition` | `editionLabel` + `sourceSha256` |
| PDF field inventory | `FormFieldDefinition` | its form's version |
| Canonical → PDF mapping | `FormMappingVersion` | `DRAFT` → `APPROVED` → `SUPERSEDED` |
| Package composition | `PackageProfile`, `PackageProfileForm` | profile version |
| Checklist template | `ChecklistTemplate` | `ruleVersion` + `lastReviewedDate` |
| Conditional rule | `ConditionalRule` | rule version |

---

## The lifecycle

```
Source registry → Draft → Review → Approve → Publish (effective-dated) → Retire
                            │
                  Impact analysis: which templates, open cases,
                  generated forms, deadlines, advice content?
                            │
                  Regression pack over representative case profiles
                            │
                  Release note: informational vs action-required
```

**The author is never the approver.** Separation of duties is the control that makes this defensible.

---

## 1 · Source registry

Every piece of content records where it came from:

- [ ] `sourceUrl` — the official IRCC/ESDC page, not a mirror or a summary
- [ ] `sourceFileName` and `sourceSha256` for a form file
- [ ] `effectiveDate`, and `retirementDate` when known
- [ ] `editionLabel`
- [ ] Owner and reviewer
- [ ] Change reason

**No governed content without an authoritative citation** (`BR-3`). A checklist item that cannot point
at an official page is an opinion, and this product does not ship opinions.

## 2 · Draft

Content is created in `DRAFT` and is **not** visible to consultants or clients.

For a new form, run the inspect flow (`F41-01`) and record:

- [ ] `sourceSha256`
- [ ] Technology classification — `STANDARD_ACROFORM` / `STATIC_XFA` / `DYNAMIC_XFA` / `NONE`
- [ ] `supportsFill`, `supportsBarcode`, `status`
- [ ] Discovered field inventory (and the `xfa:data` skeleton for XFA forms)

> ### ⛔ The rule that overrides everything else here
>
> **A dynamic-XFA or barcode form is never marked fillable.** IRCC's high-volume forms (IMM 0008,
> 5257, 5645, 5669, 5406, 5532, 1294, 1295) are dynamic XFA, encrypted, and certified. Writing
> AcroForm values into one produces a PDF that opens **blank** in Adobe while our system reports
> success. Phase 5 §4.1 classifies that as a correctness and trust failure — a consultant could file
> a blank form on our assurance.
>
> When in doubt: `status = BLOCKED`, `supportsFill = false`, and give the consultant a clear reason
> plus the data-sheet fallback (`F41-02`).

## 3 · Review — a second person

- [ ] The reviewer is **not** the author
- [ ] Source URL opened and confirmed current
- [ ] Field mappings spot-checked against the real PDF
- [ ] For XFA forms, mappings target an explicit **`xfaDataPath`**, never a local field name — duplicate local names and same-name wrapper leaves (`<PassportNum><PassportNum/></PassportNum>`) make local-name matching unsafe
- [ ] Effective date correct
- [ ] Change reason recorded

## 4 · Impact analysis — before approval, not after

Answer all five:

1. **Which checklist templates** reference this content?
2. **Which open cases** use it? Are any mid-preparation?
3. **Which generated forms and packages** were produced from the previous version?
4. **Which deadlines** derive from a rule that just changed?
5. **Which advice or guidance content** cites it?

Then classify the change:

| Class | Meaning | Consultant action |
| --- | --- | --- |
| **Informational** | No active case is affected | None |
| **Action required** | Open cases must be reviewed or regenerated | Named, with a deadline |

## 5 · Regression pack

Run representative case profiles through the changed content **before** publishing:

- [ ] Checklist generation produces the expected items for each service type
- [ ] Mapping produces the expected values for a known canonical input
- [ ] For a fillable form, a **round-trip fixture** proves values land correctly
- [ ] Validation rules fire as expected
- [ ] No previously passing profile regresses

Fixtures live in `backend/src/test/resources/form-fixtures/`. **Never a real client document** — synthesize one (CLAUDE.md §8).

## 6 · Approve and publish

- [ ] Approver recorded, distinct from the author
- [ ] Effective date set
- [ ] Previous version marked `SUPERSEDED`, **not deleted** — generated artefacts reference it
- [ ] Release note published, classified informational or action-required
- [ ] Entry added to `.claude/change.log.md`

## 7 · Retire

- [ ] `retirementDate` set
- [ ] New generation blocked against it
- [ ] Existing generated artefacts keep their link to it — **history is never rewritten**
- [ ] Affected open cases identified and their owners notified

---

## Rollback

Rollback is a **first-class operation**, not a re-edit.

- [ ] Reinstate the previous version as current
- [ ] Record the rollback and its reason in `change.log.md`
- [ ] Identify artefacts generated from the withdrawn version
- [ ] Notify affected consultants
- [ ] **Never delete the withdrawn version** — it is referenced by artefacts already produced

---

## Upstream change detection

- **`sourceSha256` mismatch ⇒ re-inspect.** A form that changed upstream is not the form we mapped. `FORM_SOURCE_HASH_MISMATCH` blocks generation.
- Re-verify `sourceUrl` on the review cadence — a 404 or redirect means the content moved or was withdrawn.
- Watch effective and retirement dates; content that lapses must not stay live.

---

## Recording a content release

| What | Where |
| --- | --- |
| The publication, retirement, or rollback | `.claude/change.log.md` — source, version, approver, effective date |
| Impact analysis and the regression result | `.claude/progress/<YYYY-MM-DD>-content-release.md` |
| The operational procedure, once it stabilises | `.claude/operations/runbook-content-release.md` |
| A form's technology classification and why | `.claude/memory/<slug>.md` if it was non-obvious |
| A decision on whether a form can ever be filled | `.claude/design/ADR-nnn-<slug>.md` |

**Read [`.claude/memory/pdfbox-cannot-save-ircc-forms.md`](../../memory/pdfbox-cannot-save-ircc-forms.md)
before onboarding any IRCC form.** It records exactly which forms cannot be filled and why the
obvious approach fails after appearing to work — a week of work, already spent.

## Rules

- **Every rule carries an authoritative citation, effective date, owner, reviewer, and change reason** (`BR-3`).
- **Separation of duties** — the author is never the approver.
- **Never delete a superseded version.** Mark it; artefacts reference it.
- **Never mark a dynamic-XFA form fillable.**
- **Never seed real PII into content or fixtures** (CLAUDE.md §8).
- **Impact analysis happens before approval**, not after publication.
- **Ask before publishing** — publication changes what consultants see and what clients are asked for (CLAUDE.md §3).
