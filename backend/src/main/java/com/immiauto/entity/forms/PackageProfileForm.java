package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Connects a package profile to a required/optional form definition.
 */
@Entity
@Table(name = "package_profile_forms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PackageProfileForm extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_profile_id", nullable = false)
    private PackageProfile packageProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_definition_id", nullable = false)
    private FormDefinition formDefinition;

    @Column
    private boolean required;

    @Column
    private int sortOrder;

    @Column(columnDefinition = "TEXT")
    private String conditionalExpression; // optional JSON/text rule

    @Column(columnDefinition = "TEXT")
    private String notes;
}
