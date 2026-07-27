package com.immiauto.repository;

import com.immiauto.entity.ConditionalRule;
import com.immiauto.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionalRuleRepository extends JpaRepository<ConditionalRule, Long> {
    List<ConditionalRule> findByServiceTypeAndActiveTrueOrderByTriggerQuestionKey(ServiceType serviceType);
    List<ConditionalRule> findByServiceTypeAndTriggerQuestionKeyAndActiveTrue(ServiceType serviceType, String triggerQuestionKey);
}
