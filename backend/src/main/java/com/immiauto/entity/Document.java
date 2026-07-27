package com.immiauto.entity;

import com.immiauto.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_gen")
    @SequenceGenerator(name = "documents_gen", sequenceName = "documents_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String filePath;

    @Column
    private String mimeType;

    @Column
    private Long fileSizeBytes;

    @Column
    private String documentCategory; // e.g., Identity, Financial, Education

    @Column
    private String documentType; // e.g., Passport, Bank Statement, Transcript

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column
    private LocalDate expiryDate;

    @Column(columnDefinition = "TEXT")
    private String reviewNote;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column
    private boolean translationRequired;

    @Column
    private boolean notarizationRequired;

    @Column
    private String standardizedName; // Renamed for organized file package
}
