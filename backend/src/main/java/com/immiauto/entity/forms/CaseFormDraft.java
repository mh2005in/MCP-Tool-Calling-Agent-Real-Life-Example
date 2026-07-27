package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.DraftOrigin;
import com.immiauto.enums.PackageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A draft form artifact for a case. Either auto-generated from an approved
 * mapping (origin=GENERATED, mappingVersion set) or a manually-filled official
 * form uploaded by the consultant (origin=UPLOADED, no mapping version).
 * Captures the input snapshot, mapped values, validation summary, and the
 * artifact file with its hash for traceability.
 */
@Entity
@Table(name = "case_form_drafts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseFormDraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "case_form_drafts_gen")
    @SequenceGenerator(name = "case_form_drafts_gen", sequenceName = "case_form_drafts_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_definition_id", nullable = false)
    private FormDefinition formDefinition;

    // Nullable: manually-uploaded forms have no mapping version.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_version_id")
    private FormMappingVersion mappingVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftOrigin origin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;

    // Consultant's original file name for uploaded forms.
    @Column
    private String originalFileName;

    @Column(columnDefinition = "TEXT")
    private String inputSnapshotJson;

    @Column(columnDefinition = "TEXT")
    private String mappedValuesJson;

    @Column(columnDefinition = "TEXT")
    private String validationSummaryJson;

    @Column(columnDefinition = "TEXT")
    private String draftFilePath;

    @Column
    private String draftSha256;

    @Column
    private LocalDateTime generatedAt;

    @Column
    private String generatedBy;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private String approvedBy;

    @Column(columnDefinition = "TEXT")
    private String approvalNotes;
}
