package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Connects a package profile to existing document/checklist concepts,
 * describing the supporting evidence a package requires.
 */
@Entity
@Table(name = "package_document_requirements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PackageDocumentRequirement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_profile_id", nullable = false)
    private PackageProfile packageProfile;

    @Column
    private String documentCategory;

    @Column
    private String documentType;

    @Column
    private boolean required;

    @Column
    private int sortOrder;

    @Column
    private String namingPattern;

    @Column
    private Long maxSizeBytes;

    @Column(columnDefinition = "TEXT")
    private String translationRule;

    @Column(columnDefinition = "TEXT")
    private String certifiedCopyRule;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
