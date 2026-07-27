package com.immiauto.config;

import com.immiauto.service.forms.FormStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Dev-only: generates a small synthetic fillable AcroForm PDF for the pilot
 * form IMM 5476 so the Milestone 3 generation pipeline is demonstrable without
 * shipping a real (XFA / copyrighted) IRCC PDF. The field names match the
 * IMM 5476 field definitions and approved mapping seeded in V7.
 *
 * Enable with app.forms.seed-sample-pdf=true (dev profile).
 */
@Component
@Profile("dev")
@ConditionalOnProperty(name = "app.forms.seed-sample-pdf", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class SampleAcroFormSeeder implements CommandLineRunner {

    private static final String FORM_CODE = "IMM_5476";
    private static final String EDITION = "2024";

    /** Field name -> human label, matching the V7 seed field definitions. */
    private static final List<String[]> FIELDS = List.of(
            new String[]{"applicant_name", "Applicant full name"},
            new String[]{"representative_name", "Representative full name"},
            new String[]{"representative_license", "Representative license no."},
            new String[]{"signature_date", "Signature date"}
    );

    private final FormStorageService storage;

    @Override
    public void run(String... args) throws Exception {
        Path target = storage.sourcePdf(FORM_CODE, EDITION);
        if (Files.exists(target)) {
            log.info("Sample AcroForm already present at {}", target);
            return;
        }
        Files.createDirectories(target.getParent());
        buildSampleForm(target);
        log.info("Generated sample fillable AcroForm for {} at {}", FORM_CODE, target);
    }

    private void buildSampleForm(Path target) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);

            PDType1Font helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDResources resources = new PDResources();
            resources.put(COSName.getPDFName("Helv"), helvetica);
            acroForm.setDefaultResources(resources);
            acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
            acroForm.setNeedAppearances(true);

            float y = 720f;
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(helvetica, 14);
                content.newLineAtOffset(50, 770);
                content.showText("Use of a Representative (IMM 5476) - SAMPLE / DRAFT");
                content.endText();
            }

            for (String[] field : FIELDS) {
                addLabel(document, page, helvetica, field[1], y + 16);
                addTextField(acroForm, page, field[0], y);
                y -= 60f;
            }

            document.save(target.toFile());
        }
    }

    private void addLabel(PDDocument document, PDPage page, PDType1Font font, String label, float y) throws Exception {
        try (PDPageContentStream content = new PDPageContentStream(document, page,
                PDPageContentStream.AppendMode.APPEND, true, true)) {
            content.beginText();
            content.setFont(font, 10);
            content.newLineAtOffset(50, y);
            content.showText(label);
            content.endText();
        }
    }

    private void addTextField(PDAcroForm acroForm, PDPage page, String name, float y) throws Exception {
        PDTextField textField = new PDTextField(acroForm);
        textField.setPartialName(name);
        textField.setDefaultAppearance("/Helv 12 Tf 0 0 0 rg");
        acroForm.getFields().add(textField);

        PDAnnotationWidget widget = textField.getWidgets().get(0);
        widget.setRectangle(new PDRectangle(50, y, 300, 20));
        widget.setPage(page);
        widget.setPrinted(true);
        page.getAnnotations().add(widget);
    }
}
