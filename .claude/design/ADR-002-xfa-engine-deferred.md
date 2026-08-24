# ADR-002 — No commercial XFA engine is licensed; classify honestly and degrade usefully

**Status:** Proposed — **blocks `F41-14`**, awaiting decision `D-1`
**Date:** 2026-06-29 (finding) · 2026-08-21 (recorded) · 2026-08-24 (reaffirmed)
**Decision:** `D-1`
**Requirements:** `F41-01`, `F41-02`, `F41-14`, `GAP-01`

## Context

The product's core value proposition is filling IRCC forms from canonical case data without rekeying.
The high-volume forms — IMM 0008, 5257, 5645, 5669, 5406, 5532, 1294, 1295 — are **dynamic XFA,
encrypted, certified (DocMDP), and Reader-Extended**.

Apache PDFBox, the engine in the stack, is an **AcroForm** engine with no XFA layout engine. A proof
of concept on 2026-06-29 tested whether it could be pushed far enough anyway:

1. **Datasets injection works.** Values written into the `xfa:data` node are present on re-read.
2. **`saveIncremental()` preserves** the original bytes, encryption, and usage rights.
3. **Both outputs fail to open in Adobe Reader.** PDFBox has open defects writing encrypted incremental updates ([PDFBOX-3188](https://issues.apache.org/jira/browse/PDFBOX-3188), [PDFBOX-4286](https://issues.apache.org/jira/browse/PDFBOX-4286)); a full save decrypts and rewrites the file, breaking both certification and the dynamic XFA layout.

**The wall is the save step, not the fill step.** That distinction matters: it means no amount of
mapping work, field-path precision, or transform logic gets past it. Full record in
[`../memory/pdfbox-cannot-save-ircc-forms.md`](../memory/pdfbox-cannot-save-ircc-forms.md).

A second constraint shapes the options. **`BR-5` forbids client PII leaving the tenant**, which rules
out the entire category of hosted form-fill services regardless of their technical capability.

## Options considered

| Option | Pros | Cons | Verdict |
| --- | --- | --- | --- |
| **Classify honestly + data-sheet fallback** | Zero cost, zero licensing risk, ships now. Consultant transcribes into Adobe, which handles barcode and certification natively. Preserves the anti-rekeying value — the data is still assembled and validated | Manual transcription step remains. Not the demo the product wants | ✅ **Interim position** |
| License a commercial engine (iText 7 commercial / Aspose.PDF / Qoppa) | The only path to a valid filled dynamic-XFA PDF short of Adobe AEM. `PdfFormEngine` SPI means a bean swap, no caller changes | Recurring licence cost. Procurement and legal review. Unproven against *these* certified forms until bought and tested | ⏸ **Undecided — `D-1`** |
| iText 7 under AGPL | Free | AGPL would force open-sourcing a commercial SaaS | ❌ Rejected |
| SaaS form-fill API (Anvil, PDF.co) | Fast integration | **Violates `BR-5`** — PII egress. AcroForm-only anyway, so it does not even solve the problem | ❌ Rejected |
| Adobe AEM Forms | Native XFA support, authoritative | Enterprise licensing and infrastructure far beyond the project's scale | ❌ Rejected on cost |
| Fill anyway and hope | — | Produces a blank-but-"successful" PDF a consultant might file. Phase 5 §4.1 calls this a trust failure, not a missing feature | ❌ **Never** |

## Decision

**Deferred to the user as decision `D-1`.** Until it is answered, the architecture takes the
interim position: classify honestly, degrade usefully, and keep the escalation path cheap.

Three layers, in this order:

| Layer | Mechanism | Requirement | State |
| --- | --- | --- | --- |
| 1. Classify honestly | Inspect every form; mark dynamic-XFA and barcode forms `BLOCKED`. **Never** emit a blank-but-"successful" PDF | `F41-01` | 🟡 Partial |
| 2. Degrade usefully | Data-sheet fallback — a printable mapped-values sheet the consultant transcribes | `F41-02` | ⬜ Open |
| 3. Escalate only if funded | A second `PdfFormEngine` bean selected by form technology | `F41-14` | ⛔ Blocked on `D-1` |

## Consequences

**What this makes easy**

- `PdfFormEngine` is an interface with `PdfBoxFormEngine` as its only implementation. Layer 3 is a bean swap and a classification branch — **no caller changes**. The cost of reversing this decision later is deliberately low.
- AcroForm forms (IMM 5476, 5475, 5708, 5709, 5710) can be filled today. The blocker is specific to dynamic XFA, not universal.

**What this makes hard**

- Layer 1 is the load-bearing control and it is currently **weaker than it looks**. `PdfBoxFormEngine` :38 sets a single boolean, `hasXfa = acroForm.getXFA() != null`. That does not distinguish **static** XFA (fillable through the AcroForm layer) from **dynamic** XFA (not fillable at all), and nothing ever sets `supportsBarcode`. There is no `FormTechnology` enum. **Until classification is four-way and barcode-aware, "fillable" is a guess** — which is exactly the failure this ADR exists to prevent. Tracked as [`../requirements/F41-01.md`](../requirements/F41-01.md).

**What this forecloses**

- Nothing structurally. But **expectations drift while `D-1` sits open** — the longer the product demos AcroForm fill without stating the dynamic-XFA boundary, the more the eventual answer looks like a regression rather than a known constraint.

**If `D-1` resolves against licensing**, `F41-02` is not a fallback — it is the permanent answer, and
should be built and presented as such.

## Constraint carried forward

Mappings must store an explicit **`xfaDataPath`** (`form1.Page1.PersonalDetails.Name.FamilyName`),
never a local field name. The PoC found duplicate local names and same-name wrapper leaves
(`<PassportNum><PassportNum/></PassportNum>`) that make local-name matching unsafe. This holds
regardless of which engine eventually fills the form.
