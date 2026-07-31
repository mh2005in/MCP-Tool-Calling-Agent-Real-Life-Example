package com.immiauto.dto;

import com.immiauto.enums.ServiceType;
import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeQuestionTemplateDto {
    private UUID id;
    private ServiceType serviceType;
    private String sectionName;
    private String questionKey;
    private String questionLabel;
    private String helpText;
    private String inputType;
    private String options;
    private boolean required;
    private boolean isTriggerQuestion;
    private int sortOrder;
}
