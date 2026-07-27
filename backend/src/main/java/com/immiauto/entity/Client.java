package com.immiauto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients", uniqueConstraints = {
        @UniqueConstraint(name = "uk_client_name_dob_email",
                columnNames = {"fullName", "dateOfBirth", "email"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_gen")
    @SequenceGenerator(name = "clients_gen", sequenceName = "clients_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 15)
    private String clientNumber;

    @PrePersist
    private void generateClientNumber() {
        if (clientNumber == null) {
            clientNumber = "CN" + String.format("%013d", id);
        }
    }

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String whatsapp;

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String countryOfCitizenship;

    @Column
    private String currentLocation; // inside or outside Canada

    @Column
    private String currentStatus; // visitor, student, worker, out of status, etc.

    @Column
    private String passportNumber;

    @Column
    private String maritalStatus;

    @Column
    private String preferredLanguage;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ImmigrationCase> cases = new ArrayList<>();
}
