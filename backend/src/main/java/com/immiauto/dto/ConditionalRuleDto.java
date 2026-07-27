package com.immiauto.dto;

import com.immiauto.enums.ServiceType;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConditionalRuleDto {
    private Long id;
    private ServiceType serviceType;
    private String triggerQuestionKey;
    private String triggerValue;
    private String operator;
    private String actionType;
    private Long targetChecklistTemplateId;
    private String targetQuestionKey;
    private String description;
    private boolean active;
}
