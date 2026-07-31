package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormFieldDefinitionRepository extends JpaRepository<FormFieldDefinition, UUID> {

    List<FormFieldDefinition> findByFormDefinitionIdOrderByPageNumber(UUID formDefinitionId);

    Optional<FormFieldDefinition> findByFormDefinitionIdAndPdfFieldName(UUID formDefinitionId, String pdfFieldName);
}
