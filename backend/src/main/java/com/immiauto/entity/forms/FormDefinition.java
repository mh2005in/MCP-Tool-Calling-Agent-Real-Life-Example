package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.enums.FormStatus;
import com.immiauto.enums.Jurisdiction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * A governed official form file/version (e.g. an IRCC IMM form edition).
 * The source PDF is identified by its exact SHA-256 so mappings always point
 * to the precise file that was inspected.
 */
@Entity
@Table(name = "form_definitions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormDefinition extends BaseEntity {

    @Column(nullable = false)
    private String formCode; // e.g. IMM_5257

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jurisdiction jurisdiction;

    @Column
    private String programCategory; // visitor, study, work, family, PR, citizenship, PNP

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    @Column
    private String sourceFileName;

    @Column
    private String sourceSha256;

    @Column
    private LocalDate effectiveDate;

    @Column
    private LocalDate retirementDate;

    @Column
    private String editionLabel;

    @Column
    private boolean supportsFill;

    @Column
    private boolean supportsBarcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
