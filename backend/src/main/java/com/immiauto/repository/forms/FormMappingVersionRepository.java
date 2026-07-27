package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormMappingVersion;
import com.immiauto.enums.MappingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormMappingVersionRepository extends JpaRepository<FormMappingVersion, Long> {

    List<FormMappingVersion> findByFormDefinitionIdOrderByMappingVersionDesc(Long formDefinitionId);

    Optional<FormMappingVersion> findByFormDefinitionIdAndMappingVersion(Long formDefinitionId, Integer mappingVersion);

    Optional<FormMappingVersion> findFirstByFormDefinitionIdAndStatusOrderByMappingVersionDesc(Long formDefinitionId, MappingStatus status);
}
