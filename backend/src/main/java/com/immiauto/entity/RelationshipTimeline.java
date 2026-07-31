package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "relationship_timeline_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RelationshipTimeline extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String milestoneType; // FIRST_CONTACT, FIRST_MEETING, DATING, ENGAGEMENT, MARRIAGE, COHABITATION, CHILD_BORN, OTHER

    @Column(nullable = false)
    private LocalDate milestoneDate;

    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String evidenceCategory; // PHOTOS, CHATS, TRAVEL_PROOF, JOINT_FINANCIAL, CORRESPONDENCE, OTHER

    @Column
    private int evidenceCount;

    @Column
    private int sortOrder;
}
