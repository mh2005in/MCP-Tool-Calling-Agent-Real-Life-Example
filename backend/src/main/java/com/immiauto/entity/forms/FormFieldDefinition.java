package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.enums.PdfFieldType;
import jakarta.persistence.*;
import lombok.*;

/**
 * A field discovered inside a PDF form via inspection.
 */
@Entity
@Table(name = "form_field_definitions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormFieldDefinition extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_definition_id", nullable = false)
    private FormDefinition formDefinition;

    @Column(nullable = false)
    private String pdfFieldName;

    @Column
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PdfFieldType fieldType;

    @Column
    private boolean required;

    @Column
    private Integer maxLength;

    @Column(columnDefinition = "TEXT")
    private String allowedValues;

    @Column
    private Integer pageNumber;

    @Column
    private boolean readOnly;

    @Column
    private boolean calculated;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
