package com.immiauto.service.forms.pdf;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of filling a PDF AcroForm: the output path + hash, how many fields
 * were written, and any requested values whose field was not found.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PdfFillResult {
    private String outputPath;
    private String sha256;
    private int filledCount;

    @Builder.Default
    private List<String> unmatchedFieldNames = new ArrayList<>();
}
