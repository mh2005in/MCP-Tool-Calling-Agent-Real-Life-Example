# ADR-003 — Trigger questions are a flag plus a rule key, not a `TriggerQuestion` entity

**Status:** Accepted
**Date:** 2026-08-24
**Requirements:** `DR-01` (closed), `BL-03` (promoted to `VERIFIED`)
**Source:** Stage 2.1 §2.6

## Context

Stage 2.1 §2.6 specified a `TriggerQuestion` entity: intake questions whose answers decide which
conditional checklist rules fire. The 2026-08-21 verification pass grepped for `*Trigger*` as a
filename, found no such file, and recorded `DR-01` — *"specified but never built"* — which also held
`BL-03` (rules-based checklist generation) at `PARTIAL`.

**The capability is fully implemented.** The 2026-08-24 pass grepped for the *concept* rather than the
filename and found it distributed across the intake and rule models:

| Element | Location |
| --- | --- |
| The flag marking a question as a trigger | `IntakeQuestionTemplate.isTriggerQuestion` :40 |
| The rule's reference to that question | `ConditionalRule.triggerQuestionKey` :19 |
| Finder — trigger questions for a service type | `IntakeQuestionTemplateRepository.findByServiceTypeAndIsTriggerQuestionTrueOrderBySortOrder` :15 |
| Finder — rules for a given trigger | `ConditionalRuleRepository.findByServiceTypeAndTriggerQuestionKeyAndActiveTrue` :14 |
| Evaluation | `ChecklistGeneratorService` :46 (load rules), :111 (`answers.get(rule.getTriggerQuestionKey())`) |
| Seeding | `ConditionalRuleSeeder` :153, `IntakeQuestionSeeder` :330 |
| Exposure | `IntakeQuestionTemplateDto.isTriggerQuestion`, `ConditionalRuleDto.triggerQuestionKey` |

Nothing about the specified behaviour is missing. What differs is the shape: a boolean on the
existing template plus a string key on the rule, rather than a third table joining the two.

## Options considered

| Option | Pros | Cons | Verdict |
| --- | --- | --- | --- |
| **Accept the deviation** | The capability works and is seeded, exposed, and evaluated. Zero migration. One fewer table and one fewer join on the checklist-generation hot path | The spec and the schema differ, which will confuse the next reader unless recorded — which is what this ADR is for | ✅ **Chosen** |
| Build the `TriggerQuestion` entity to match the spec | Literal conformance | A migration, an entity, a repository, a mapper, and a rewrite of `ChecklistGeneratorService` — to arrive at behaviour that already exists. Violates CLAUDE.md §10 by adding a second way to express the same thing | Rejected — cost with no behavioural gain |
| Leave `DR-01` open pending a decision | Defers judgment | Keeps a false open item in the register. Someone eventually builds it, or it sits there misrepresenting the state for another four months | Rejected |

## Decision

**The flag-plus-key model is the accepted implementation of trigger questions.** `DR-01` is closed as
`SUPERSEDED`; `BL-03` moves to `VERIFIED`.

## Consequences

**What this makes easy**

- Adding a trigger question is a flag on an existing template row — no join table to populate, no orphan risk between a question and its trigger record.
- `ChecklistGeneratorService` resolves rules by key against the answer map directly, without loading a third entity.

**What this makes hard**

- **The key is a string, unconstrained by a foreign key.** A `ConditionalRule.triggerQuestionKey` that does not match any `IntakeQuestionTemplate.questionKey` fails silently — the rule simply never fires. A dedicated entity would have made that a referential-integrity error.
  **Mitigation:** the test foundation (`DR-10`) must include a seed-integrity check asserting every `triggerQuestionKey` in `ConditionalRule` resolves to a seeded question, per service type. Recorded in [`../qa/DR-10-test-foundation.md`](../qa/DR-10-test-foundation.md).

**What this forecloses**

- Nothing. Should trigger questions later need attributes of their own — a version, an owner, an effective date under `GAP-09` — promoting the flag to an entity is a normal migration.

## Note on how this was found

`DR-01` was raised by searching for a **filename** (`*Trigger*`) and finding none. The capability was
found by searching for the **concept** (`triggerQuestion`, case-insensitive, across entities, DTOs,
repositories, and services).

A missing file is not a missing capability. Verification searches for behaviour — recorded in
[Delivery approach.md](../Delivery%20approach.md) §10a.
