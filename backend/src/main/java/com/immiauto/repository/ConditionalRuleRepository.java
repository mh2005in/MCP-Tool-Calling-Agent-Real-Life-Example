package com.immiauto.repository;

import com.immiauto.entity.ConditionalRule;
import com.immiauto.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConditionalRuleRepository extends JpaRepository<ConditionalRule, UUID> {
    List<ConditionalRule> findByServiceTypeAndActiveTrueOrderByTriggerQuestionKey(ServiceType serviceType);
    List<ConditionalRule> findByServiceTypeAndTriggerQuestionKeyAndActiveTrue(ServiceType serviceType, String triggerQuestionKey);
}
