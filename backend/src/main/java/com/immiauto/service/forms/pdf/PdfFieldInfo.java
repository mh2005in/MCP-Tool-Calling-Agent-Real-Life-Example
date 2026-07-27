package com.immiauto.service.forms.pdf;

import com.immiauto.enums.PdfFieldType;
import lombok.*;

/**
 * A field discovered inside a PDF AcroForm during inspection.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PdfFieldInfo {
    private String pdfFieldName;
    private PdfFieldType fieldType;
    private boolean required;
    private boolean readOnly;
    private Integer maxLength;   // null if not constrained
    private Integer pageNumber;  // 1-based, null if not resolvable
    private String allowedValues; // comma-separated for choice fields, null otherwise
}
