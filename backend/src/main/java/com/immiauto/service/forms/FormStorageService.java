package com.immiauto.service.forms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resolves on-disk locations for governed source PDFs and generated artifacts.
 * Generated files live under each case folder so they are never served by a
 * public static path. (Section 4.1 - Phase G / §7)
 *
 * <pre>
 * {uploadDir}/{caseNumber}/generated/forms/{packageId}/{formCode}_{edition}_{draftId}.pdf
 * {sourceDir}/{formCode}/{edition}/source.pdf
 * </pre>
 */
@Service
public class FormStorageService {

    private final String sourceDir;
    private final String uploadDir;

    public FormStorageService(@Value("${app.forms.source-dir:./forms/source}") String sourceDir,
                              @Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.sourceDir = sourceDir;
        this.uploadDir = uploadDir;
    }

    public Path sourcePdf(String formCode, String editionLabel) {
        return Paths.get(sourceDir, safe(formCode), edition(editionLabel), "source.pdf");
    }

    public Path generatedFormsDir(String caseNumber, Long packageProfileId) {
        return Paths.get(uploadDir, safe(caseNumber), "generated", "forms", String.valueOf(packageProfileId));
    }

    public Path generatedFormFile(String caseNumber, Long packageProfileId, String formCode,
                                  String editionLabel, Long draftId) {
        return generatedFormsDir(caseNumber, packageProfileId)
                .resolve(generatedFileName(formCode, editionLabel, draftId));
    }

    public String generatedFileName(String formCode, String editionLabel, Long draftId) {
        return safe(formCode) + "_" + edition(editionLabel) + "_" + draftId + ".pdf";
    }

    /** Directory for consultant-uploaded filled forms (not tied to a package profile). */
    public Path uploadedFormsDir(String caseNumber) {
        return Paths.get(uploadDir, safe(caseNumber), "generated", "forms", "uploaded");
    }

    public Path uploadedFormFile(String caseNumber, String formCode, Long draftId) {
        return uploadedFormsDir(caseNumber).resolve(safe(formCode) + "_" + draftId + ".pdf");
    }

    /** Directory for an assembled package's artifacts (index/manifest/zip). */
    public Path packageDir(String caseNumber, Long packageId) {
        return Paths.get(uploadDir, safe(caseNumber), "generated", "packages", String.valueOf(packageId));
    }

    public Path packageZip(String caseNumber, Long packageId) {
        return packageDir(caseNumber, packageId).resolve("submission-package.zip");
    }

    private String edition(String editionLabel) {
        return StringUtils.hasText(editionLabel) ? safe(editionLabel) : "current";
    }

    /** Keep path segments to a safe character set (defence against traversal). */
    private String safe(String value) {
        if (value == null) return "_";
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
