package com.immiauto.service.forms.pdf;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Abstraction over the PDF form engine so the implementation can be swapped
 * (e.g. a commercial engine for XFA forms) without touching callers.
 * (Section 4.1 - Phase C)
 */
public interface PdfFormEngine {

    /** Inspect a source PDF and report its AcroForm fields, XFA status, and hash. */
    PdfInspectionResult inspect(Path sourcePdf) throws IOException;

    /** Fill an AcroForm source PDF with the given pdfFieldName -> value map, writing to outputPdf. */
    PdfFillResult fill(Path sourcePdf, Map<String, String> values, Path outputPdf) throws IOException;

    /** SHA-256 hex digest of a file's bytes. */
    String sha256(Path file) throws IOException;
}
