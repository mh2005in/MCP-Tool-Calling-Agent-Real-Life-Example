package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "work_history_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkHistoryEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_history_entries_gen")
    @SequenceGenerator(name = "work_history_entries_gen", sequenceName = "work_history_entries_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String employerName;

    @Column
    private String jobTitle;

    @Column
    private String nocTeerCode;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private boolean currentJob;

    @Column
    private double hoursPerWeek;

    @Column
    private String employmentType; // FULL_TIME, PART_TIME, SELF_EMPLOYED, CONTRACT

    @Column(columnDefinition = "TEXT")
    private String duties;

    @Column
    private String country;

    @Column
    private String city;

    @Column
    private boolean referenceLetterReceived;

    @Column
    private boolean referenceDatesMatch;

    @Column
    private boolean referenceDutiesDescribed;

    @Column
    private int sortOrder;
}
