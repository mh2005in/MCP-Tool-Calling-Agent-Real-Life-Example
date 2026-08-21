# PDFBox cannot produce a valid filled IRCC form — do not retry

**Type:** rejected-approach
**Learned:** 2026-06-29 (PoC), recorded 2026-08-21
**Related:** `F41-01`, `F41-02`, `F41-14`, decision `D-1`, [`../input/pending/Section-4.1-XFA-Real-PDF-Onboarding-Issue.md`](../input/pending/Section-4.1-XFA-Real-PDF-Onboarding-Issue.md)

## The fact

Apache PDFBox **can** inject data into a real IRCC form's XFA datasets, but it **cannot re-save that
form into a file Adobe will open.** This was proven end-to-end against IMM 5257 and IMM 5709 and is
not a bug to work around — it is a wall.

## Why it matters

The obvious next move for anyone picking up form automation is "just fill the XFA datasets and save
incrementally." It looks like it works: the injection succeeds, and re-reading the output confirms
every value is present. **Then Adobe refuses to open the file.** That is a week of work ending where
it started.

The second trap is worse. Writing AcroForm values into a dynamic-XFA form *succeeds silently* — our
code reports a filled draft, and the PDF opens **blank** in Adobe. A consultant could file a blank
form on the system's assurance. Phase 5 §4.1 calls this a correctness and trust failure, not a missing
feature. Hence the absolute rule: **never mark a dynamic-XFA form fillable.**

## Evidence

From the 2026-06-29 PoC (`pdf-xfa-poc/`, PDFBox 3.0.3):

- IMM 5257 and IMM 5709 are both **dynamic XFA** (`config` packet has `dynamicRender=required`) with only **2 AcroForm fields** — the validate/barcode fields. The M3 AcroForm `fill()` path is useless against them.
- Datasets injection **worked**: 4 values including a nested `PassportNum` leaf, verified on re-read in both full-save and incremental-save outputs.
- **Both outputs then failed to load in Adobe with a certificate error.** IRCC forms are encrypted **and** certified (DocMDP) **and** Reader-Extended. PDFBox has open defects writing encrypted incremental updates ([PDFBOX-3188](https://issues.apache.org/jira/browse/PDFBOX-3188), [PDFBOX-4286](https://issues.apache.org/jira/browse/PDFBOX-4286)); a full save decrypts and rewrites, breaking certification and dynamic XFA.

Two further traps found in the same PoC:

- The `datasets` packet (~520 KB for 5257) contains a large `<LOVFile>` as a **sibling** of `<xfa:data>`. Scope every write to the `xfa:data` node.
- **Local-name matching is unsafe** — duplicate local names (`FamilyName`, `Country`, `FromDate`) and same-name wrapper leaves (`<PassportNum><PassportNum/></PassportNum>`). Mappings must store an explicit `xfaDataPath`, never a field name.

## What actually works

1. **Classify and block honestly** (`F41-01`) — dynamic-XFA and barcode forms get `status = BLOCKED`, `supportsFill = false`.
2. **Data-sheet fallback** (`F41-02`) — emit a mapped-values sheet the consultant types into the official form in Adobe, which handles the barcode and certification natively. This is the recommended zero-cost path.
3. **Genuine AcroForms still fill** via PDFBox — likely IMM 5476, 5475, 5708, 5709, 5710, each to be confirmed by inspection and a round-trip fixture.

Producing a valid filled XFA needs append-mode + encryption + DocMDP handling: **iText 7 (commercial
licence), Aspose.PDF, or Qoppa**, or Adobe AEM/LiveCycle. That is decision `D-1` — a procurement
question, not an engineering one. SaaS form-fill APIs are already rejected (PII egress, and
AcroForm-only anyway); iText under AGPL is rejected (it would force open-sourcing the product).
