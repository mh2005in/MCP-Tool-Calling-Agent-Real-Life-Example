package com.immiauto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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

    // Human-facing number derived from the UUID id by a Postgres STORED generated column.
    @Generated(event = EventType.INSERT)
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
}
