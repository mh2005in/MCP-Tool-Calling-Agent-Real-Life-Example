package com.immiauto.dto;

import com.immiauto.enums.ServiceType;
import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConditionalRuleDto {
    private UUID id;
    private ServiceType serviceType;
    private String triggerQuestionKey;
    private String triggerValue;
    private String operator;
    private String actionType;
    private UUID targetChecklistTemplateId;
    private String targetQuestionKey;
    private String description;
    private boolean active;
}
