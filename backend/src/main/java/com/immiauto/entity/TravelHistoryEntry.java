package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "travel_history_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TravelHistoryEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private LocalDate entryDate;

    @Column
    private LocalDate exitDate;

    @Column
    private String purpose; // TOURISM, WORK, STUDY, FAMILY_VISIT, TRANSIT, RESIDENCE

    @Column
    private int daysAbsent;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column
    private int sortOrder;
}
