package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.enums.Jurisdiction;
import com.immiauto.enums.CaseSubtype;
import com.immiauto.enums.FormStatus;
import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * A supported application package for a given case type / program.
 */
@Entity
@Table(name = "package_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PackageProfile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String profileCode;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column
    private CaseSubtype caseSubtype;

    @Enumerated(EnumType.STRING)
    @Column
    private Jurisdiction jurisdiction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormStatus status; // DRAFT, ACTIVE, RETIRED (BLOCKED unused for profiles)

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private LocalDate effectiveDate;

    @Column
    private LocalDate retirementDate;
}
