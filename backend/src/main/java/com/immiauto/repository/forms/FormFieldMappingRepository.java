package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldMappingRepository extends JpaRepository<FormFieldMapping, Long> {

    List<FormFieldMapping> findByMappingVersionId(Long mappingVersionId);

    List<FormFieldMapping> findByMappingVersionIdAndRequiredForPackageTrue(Long mappingVersionId);
}
