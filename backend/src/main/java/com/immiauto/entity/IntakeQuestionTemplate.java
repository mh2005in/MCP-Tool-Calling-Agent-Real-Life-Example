package com.immiauto.entity;

import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intake_question_templates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeQuestionTemplate extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private String sectionName;

    @Column(nullable = false)
    private String questionKey;

    @Column(nullable = false)
    private String questionLabel;

    @Column(columnDefinition = "TEXT")
    private String helpText;

    @Column(nullable = false)
    private String inputType;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(nullable = false)
    @Builder.Default
    private boolean required = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isTriggerQuestion = false;

    @Column
    private int sortOrder;
}
