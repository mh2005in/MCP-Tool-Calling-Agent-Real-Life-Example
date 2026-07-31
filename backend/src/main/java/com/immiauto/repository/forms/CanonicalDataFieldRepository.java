package com.immiauto.repository.forms;

import com.immiauto.entity.forms.CanonicalDataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CanonicalDataFieldRepository extends JpaRepository<CanonicalDataField, UUID> {

    Optional<CanonicalDataField> findByFieldKey(String fieldKey);

    List<CanonicalDataField> findByActiveTrue();

    List<CanonicalDataField> findByCategoryAndActiveTrue(String category);
}
