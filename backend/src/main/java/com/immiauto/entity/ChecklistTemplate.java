package com.immiauto.entity;

import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "checklist_templates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checklist_templates_gen")
    @SequenceGenerator(name = "checklist_templates_gen", sequenceName = "checklist_templates_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String documentName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean required = true;

    @Column
    private boolean conditional;

    @Column
    private String conditionDescription;

    @Column
    private int sortOrder;

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    @Column
    private LocalDate lastReviewedDate;

    @Column
    private Long reviewedByConsultantId;

    @Column
    private String reviewedByConsultantName;

    @Column(nullable = false)
    @Builder.Default
    private int ruleVersion = 1;

    @Column(nullable = false)
    @Builder.Default
    private boolean approvedForUse = false;

    @Column
    private Long approvedByConsultantId;

    @Column
    private String approvedByConsultantName;

    @Column
    private LocalDate approvedDate;
}
