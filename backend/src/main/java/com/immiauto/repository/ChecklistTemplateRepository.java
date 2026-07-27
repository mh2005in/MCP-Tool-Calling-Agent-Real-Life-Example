package com.immiauto.repository;

import com.immiauto.entity.ChecklistTemplate;
import com.immiauto.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistTemplateRepository extends JpaRepository<ChecklistTemplate, Long> {
    List<ChecklistTemplate> findByServiceTypeOrderBySortOrder(ServiceType serviceType);
    List<ChecklistTemplate> findByServiceTypeAndApprovedForUseTrueOrderBySortOrder(ServiceType serviceType);
}
