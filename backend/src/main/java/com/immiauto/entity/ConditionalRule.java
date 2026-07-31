package com.immiauto.entity;

import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "conditional_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConditionalRule extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private String triggerQuestionKey;

    @Column(nullable = false)
    private String triggerValue;

    @Column(nullable = false)
    private String operator;

    @Column(nullable = false)
    private String actionType;

    @Column
    private UUID targetChecklistTemplateId;

    @Column
    private String targetQuestionKey;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
