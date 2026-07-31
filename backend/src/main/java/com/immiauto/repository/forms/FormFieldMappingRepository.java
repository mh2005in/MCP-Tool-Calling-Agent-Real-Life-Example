package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FormFieldMappingRepository extends JpaRepository<FormFieldMapping, UUID> {

    List<FormFieldMapping> findByMappingVersionId(UUID mappingVersionId);

    List<FormFieldMapping> findByMappingVersionIdAndRequiredForPackageTrue(UUID mappingVersionId);
}
