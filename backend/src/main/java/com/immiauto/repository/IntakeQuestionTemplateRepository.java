package com.immiauto.repository;

import com.immiauto.entity.IntakeQuestionTemplate;
import com.immiauto.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntakeQuestionTemplateRepository extends JpaRepository<IntakeQuestionTemplate, UUID> {
    List<IntakeQuestionTemplate> findByServiceTypeOrderBySortOrder(ServiceType serviceType);
    List<IntakeQuestionTemplate> findByServiceTypeAndSectionNameOrderBySortOrder(ServiceType serviceType, String sectionName);
    List<IntakeQuestionTemplate> findByServiceTypeAndIsTriggerQuestionTrueOrderBySortOrder(ServiceType serviceType);
}
