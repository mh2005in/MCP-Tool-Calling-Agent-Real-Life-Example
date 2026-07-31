package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "party_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartyProfile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String partyType; // HOST, SPONSOR, EMPLOYER

    @Column(nullable = false)
    private String fullName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String relationship; // parent, grandparent, spouse, employer name

    @Column
    private String organization;

    @Column
    private String accessToken; // for portal access

    @Column
    private boolean portalEnabled;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
