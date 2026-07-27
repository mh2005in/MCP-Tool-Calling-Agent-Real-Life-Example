package com.immiauto.repository.forms;

import com.immiauto.entity.forms.FormDefinition;
import com.immiauto.enums.FormStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormDefinitionRepository extends JpaRepository<FormDefinition, Long> {

    List<FormDefinition> findByStatus(FormStatus status);

    List<FormDefinition> findByProgramCategoryAndStatus(String programCategory, FormStatus status);

    Optional<FormDefinition> findByFormCodeAndEditionLabelAndSourceSha256(String formCode, String editionLabel, String sourceSha256);
}
