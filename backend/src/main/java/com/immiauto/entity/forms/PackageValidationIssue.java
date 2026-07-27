package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.enums.ValidationSeverity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A deterministic validation result attached to a package (and optionally a
 * specific form draft for form-level issues).
 */
@Entity
@Table(name = "package_validation_issues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PackageValidationIssue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "package_validation_issues_gen")
    @SequenceGenerator(name = "package_validation_issues_gen", sequenceName = "package_validation_issues_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_package_id", nullable = false)
    private CasePackage casePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_form_draft_id")
    private CaseFormDraft caseFormDraft; // nullable, for package-level issues

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationSeverity severity;

    @Column(nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column
    private String fieldKey;

    @Column
    private String pdfFieldName;

    @Column
    private String sourceType; // client, intake, document, checklist, travel history, work history, package

    @Column
    private Long sourceId;

    @Column
    private boolean resolved;

    @Column
    private String resolvedBy;

    @Column
    private LocalDateTime resolvedAt;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;
}
