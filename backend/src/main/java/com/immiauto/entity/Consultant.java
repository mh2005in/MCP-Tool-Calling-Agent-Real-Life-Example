package com.immiauto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultants_gen")
    @SequenceGenerator(name = "consultants_gen", sequenceName = "consultants_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 15)
    private String consultantNumber;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String licenseNumber; // RCIC license

    @Column
    private String companyName;

    @Column(nullable = false)
    private boolean admin = false;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ImmigrationCase> cases = new ArrayList<>();

    @PrePersist
    private void onPrePersist() {
        if (this.consultantNumber == null) {
            this.consultantNumber = "CO" + String.format("%013d", id);
        }
    }
}
