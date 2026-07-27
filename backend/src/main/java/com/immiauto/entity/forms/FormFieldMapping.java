package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.enums.TransformType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps a canonical data field to a PDF field within a mapping version.
 */
@Entity
@Table(name = "form_field_mappings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormFieldMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_field_mappings_gen")
    @SequenceGenerator(name = "form_field_mappings_gen", sequenceName = "form_field_mappings_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_version_id", nullable = false)
    private FormMappingVersion mappingVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_field_definition_id", nullable = false)
    private FormFieldDefinition formFieldDefinition;

    @Column(nullable = false)
    private String canonicalFieldKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransformType transformType;

    @Column(columnDefinition = "TEXT")
    private String transformConfig; // JSON/text

    @Column(columnDefinition = "TEXT")
    private String defaultValue;

    @Column
    private boolean requiredForPackage;

    @Column
    private boolean consultantReviewRequired;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
