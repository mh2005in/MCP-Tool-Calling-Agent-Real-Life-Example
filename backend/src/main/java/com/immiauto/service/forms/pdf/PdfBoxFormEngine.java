package com.immiauto.service.forms.pdf;

import com.immiauto.enums.PdfFieldType;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Apache PDFBox implementation of {@link PdfFormEngine}. Handles standard
 * AcroForm inspection and filling. Dynamic XFA forms are detected and reported
 * via {@link PdfInspectionResult#isHasXfa()} but are not reliably fillable.
 */
@Component
@Slf4j
public class PdfBoxFormEngine implements PdfFormEngine {

    @Override
    public PdfInspectionResult inspect(Path sourcePdf) throws IOException {
        try (PDDocument doc = Loader.loadPDF(sourcePdf.toFile())) {
            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
            PdfInspectionResult.PdfInspectionResultBuilder builder = PdfInspectionResult.builder()
                    .pageCount(doc.getNumberOfPages())
                    .sha256(sha256(sourcePdf))
                    .hasAcroForm(acroForm != null)
                    .hasXfa(acroForm != null && acroForm.getXFA() != null);

            List<PdfFieldInfo> fields = new ArrayList<>();
            if (acroForm != null) {
                for (PDField field : acroForm.getFieldTree()) {
                    // collect leaf (terminal) fields only; skip group/container fields
                    if (field instanceof PDTerminalField terminal) {
                        fields.add(toFieldInfo(doc, terminal));
                    }
                }
            }
            return builder.fields(fields).build();
        }
    }

    @Override
    public PdfFillResult fill(Path sourcePdf, Map<String, String> values, Path outputPdf) throws IOException {
        Files.createDirectories(outputPdf.getParent());
        int filled = 0;
        List<String> unmatched = new ArrayList<>();

        try (PDDocument doc = Loader.loadPDF(sourcePdf.toFile())) {
            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                throw new IOException("Source PDF has no AcroForm: " + sourcePdf);
            }
            // Ask viewers to (re)build field appearances from values we set.
            acroForm.setNeedAppearances(true);

            for (Map.Entry<String, String> entry : values.entrySet()) {
                PDField field = acroForm.getField(entry.getKey());
                if (field == null) {
                    unmatched.add(entry.getKey());
                    continue;
                }
                String value = entry.getValue() == null ? "" : entry.getValue();
                try {
                    if (setFieldValue(field, value)) {
                        filled++;
                    } else {
                        unmatched.add(entry.getKey());
                    }
                } catch (IllegalArgumentException ex) {
                    // e.g. a checkbox/choice value not in the allowed export values
                    log.warn("Could not set field '{}' to '{}': {}", entry.getKey(), value, ex.getMessage());
                    unmatched.add(entry.getKey());
                }
            }

            doc.save(outputPdf.toFile());
        }

        return PdfFillResult.builder()
                .outputPath(outputPdf.toString())
                .sha256(sha256(outputPdf))
                .filledCount(filled)
                .unmatchedFieldNames(unmatched)
                .build();
    }

    @Override
    public String sha256(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(file));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /** Dispatch setValue per concrete field type; returns false for unsupported types. */
    private boolean setFieldValue(PDField field, String value) throws IOException {
        if (field instanceof PDTextField textField) {
            textField.setValue(value);
            return true;
        }
        if (field instanceof PDCheckBox checkBox) {
            if (isTruthy(value)) {
                checkBox.check();
            } else {
                checkBox.unCheck();
            }
            return true;
        }
        if (field instanceof PDChoice choice) {
            choice.setValue(value);
            return true;
        }
        if (field instanceof PDRadioButton radio) {
            radio.setValue(value);
            return true;
        }
        // Signature, push button, or unsupported - not fillable from a text value.
        return false;
    }

    private boolean isTruthy(String value) {
        if (value == null) return false;
        String v = value.trim().toLowerCase();
        return v.equals("true") || v.equals("yes") || v.equals("y") || v.equals("1") || v.equals("on") || v.equals("x");
    }

    private PdfFieldInfo toFieldInfo(PDDocument doc, PDTerminalField field) {
        PdfFieldInfo.PdfFieldInfoBuilder b = PdfFieldInfo.builder()
                .pdfFieldName(field.getFullyQualifiedName())
                .fieldType(mapType(field))
                .required(field.isRequired())
                .readOnly(field.isReadOnly())
                .pageNumber(resolvePage(doc, field));

        if (field instanceof PDTextField textField) {
            int maxLen = textField.getMaxLen();
            if (maxLen > 0) {
                b.maxLength(maxLen);
            }
        }
        if (field instanceof PDChoice choice) {
            List<String> options = choice.getOptions();
            if (options != null && !options.isEmpty()) {
                b.allowedValues(String.join(",", options));
            }
        }
        return b.build();
    }

    private PdfFieldType mapType(PDField field) {
        if (field instanceof PDTextField) return PdfFieldType.TEXT;
        if (field instanceof PDCheckBox) return PdfFieldType.CHECKBOX;
        if (field instanceof PDRadioButton) return PdfFieldType.RADIO;
        if (field instanceof PDComboBox || field instanceof PDListBox) return PdfFieldType.DROPDOWN;
        if (field instanceof PDSignatureField) return PdfFieldType.SIGNATURE;
        return PdfFieldType.UNSUPPORTED;
    }

    private Integer resolvePage(PDDocument doc, PDTerminalField field) {
        try {
            List<PDAnnotationWidget> widgets = field.getWidgets();
            if (widgets == null || widgets.isEmpty()) return null;
            PDPage page = widgets.get(0).getPage();
            if (page == null) return null;
            int index = doc.getPages().indexOf(page);
            return index >= 0 ? index + 1 : null;
        } catch (Exception e) {
            return null;
        }
    }
}
