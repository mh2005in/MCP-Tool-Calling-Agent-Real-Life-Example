package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormFieldDefinitionRepository extends JpaRepository<FormFieldDefinition, Long> {

    List<FormFieldDefinition> findByFormDefinitionIdOrderByPageNumber(Long formDefinitionId);

    Optional<FormFieldDefinition> findByFormDefinitionIdAndPdfFieldName(Long formDefinitionId, String pdfFieldName);
}
