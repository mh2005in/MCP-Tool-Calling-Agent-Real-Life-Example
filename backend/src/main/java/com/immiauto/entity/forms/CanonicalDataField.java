package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Reusable normalized application data field, independent of any single form.
 * e.g. {@code primaryApplicant.passport.number}.
 */
@Entity
@Table(name = "canonical_data_fields")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CanonicalDataField extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String fieldKey;

    @Column(nullable = false)
    private String displayName;

    @Column
    private String category; // person, address, education, employment, travel, family, immigration history, case, document

    @Column
    private String dataType; // string, date, boolean, number, country, enum, list

    @Column(columnDefinition = "TEXT")
    private String sourcePriority; // JSON/text list describing where the value comes from

    @Column
    private boolean sensitive;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private boolean active;
}
