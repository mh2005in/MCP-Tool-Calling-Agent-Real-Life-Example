package com.immiauto.entity.forms;

import com.immiauto.entity.BaseEntity;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.enums.PackageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A generated submission package for one case, assembled from approved form
 * drafts and supporting documents per a package profile.
 */
@Entity
@Table(name = "case_packages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CasePackage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "case_packages_gen")
    @SequenceGenerator(name = "case_packages_gen", sequenceName = "case_packages_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_profile_id", nullable = false)
    private PackageProfile packageProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;

    @Column(columnDefinition = "TEXT")
    private String packageIndexJson;

    @Column(columnDefinition = "TEXT")
    private String readinessReportJson;

    @Column(columnDefinition = "TEXT")
    private String packageManifestPath;

    @Column(columnDefinition = "TEXT")
    private String packageZipPath;

    @Column
    private String packageSha256;

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
