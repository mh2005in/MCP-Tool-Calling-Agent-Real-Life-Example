package com.immiauto.service.forms.pdf;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of inspecting a source PDF: its discovered fields, page count,
 * whether it uses dynamic XFA (which PDFBox cannot reliably fill), and the
 * file's SHA-256.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PdfInspectionResult {
    private boolean hasAcroForm;
    private boolean hasXfa;
    private int pageCount;
    private String sha256;

    @Builder.Default
    private List<PdfFieldInfo> fields = new ArrayList<>();
}
