package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.enums.MappingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A versioned mapping set for one form definition. Mappings must be APPROVED
 * before they can be used to generate forms.
 */
@Entity
@Table(name = "form_mapping_versions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormMappingVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_definition_id", nullable = false)
    private FormDefinition formDefinition;

    @Column(nullable = false)
    private Integer mappingVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MappingStatus status;

    @Column
    private UUID approvedByConsultantId;

    @Column
    private String approvedByConsultantName;

    @Column
    private LocalDateTime approvedAt;

    @Column(columnDefinition = "TEXT")
    private String changeSummary;

    @Column(columnDefinition = "TEXT")
    private String regressionFixturePath;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
