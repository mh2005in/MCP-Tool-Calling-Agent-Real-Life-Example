package com.immiauto.service.forms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.dto.forms.CanonicalDataSnapshotDto;
import com.immiauto.dto.forms.CanonicalValueDto;
import com.immiauto.dto.forms.FormMappingPreviewDto;
import com.immiauto.dto.forms.MappedFieldPreviewDto;
import com.immiauto.entity.forms.FormDefinition;
import com.immiauto.entity.forms.FormFieldDefinition;
import com.immiauto.entity.forms.FormFieldMapping;
import com.immiauto.entity.forms.FormMappingVersion;
import com.immiauto.enums.MappingStatus;
import com.immiauto.repository.forms.FormFieldDefinitionRepository;
import com.immiauto.repository.forms.FormFieldMappingRepository;
import com.immiauto.repository.forms.FormMappingVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Loads the approved mapping version for a form and applies its field
 * mapping rules to a canonical snapshot, producing field previews and the
 * resolved PDF value map. Only deterministic transforms are supported.
 * (Section 4.1 - Phase D)
 */
@Service
@RequiredArgsConstructor
public class FormMappingService {

    private final FormMappingVersionRepository mappingVersionRepository;
    private final FormFieldMappingRepository fieldMappingRepository;
    private final FormFieldDefinitionRepository fieldDefinitionRepository;
    private final ObjectMapper objectMapper;

    /** Result of applying one transform: the value plus preview status metadata. */
    private record MappedResult(String value, boolean present, String status, String diagnostic) {}

    private static final String MAPPED = "MAPPED";
    private static final String MISSING_VALUE = "MISSING_VALUE";
    private static final String REVIEW_REQUIRED = "REVIEW_REQUIRED";
    private static final String NO_SOURCE = "NO_SOURCE";
    private static final String UNSUPPORTED_TRANSFORM = "UNSUPPORTED_TRANSFORM";

    /**
     * Build a mapping preview for one form against the given snapshot.
     */
    @Transactional(readOnly = true)
    public FormMappingPreviewDto preview(FormDefinition form, CanonicalDataSnapshotDto snapshot) {
        Map<String, CanonicalValueDto> byKey = indexByKey(snapshot);

        FormMappingPreviewDto.FormMappingPreviewDtoBuilder builder = FormMappingPreviewDto.builder()
                .formDefinitionId(form.getId())
                .formCode(form.getFormCode())
                .displayName(form.getDisplayName())
                .editionLabel(form.getEditionLabel())
                .supportsFill(form.isSupportsFill());

        Optional<FormMappingVersion> approved = mappingVersionRepository
                .findFirstByFormDefinitionIdAndStatusOrderByMappingVersionDesc(form.getId(), MappingStatus.APPROVED);

        if (approved.isEmpty()) {
            return builder
                    .mappingStatus(null)
                    .diagnostic("No approved mapping version exists for this form yet.")
                    .build();
        }

        FormMappingVersion mv = approved.get();
        List<FormFieldMapping> mappings = fieldMappingRepository.findByMappingVersionId(mv.getId());

        List<MappedFieldPreviewDto> fields = new ArrayList<>();
        java.util.Set<String> mappedFieldNames = new java.util.HashSet<>();

        for (FormFieldMapping mapping : mappings) {
            FormFieldDefinition ffd = mapping.getFormFieldDefinition();
            mappedFieldNames.add(ffd.getPdfFieldName());

            CanonicalValueDto canonical = byKey.get(mapping.getCanonicalFieldKey());
            MappedResult result = applyTransform(mapping, canonical);

            boolean reviewRequired = mapping.isConsultantReviewRequired()
                    || (canonical != null && canonical.isReviewRequired());

            String status = result.status();
            if (MAPPED.equals(status) && reviewRequired) {
                status = REVIEW_REQUIRED;
            }

            fields.add(MappedFieldPreviewDto.builder()
                    .pdfFieldName(ffd.getPdfFieldName())
                    .label(ffd.getLabel())
                    .fieldType(ffd.getFieldType() == null ? null : ffd.getFieldType().name())
                    .canonicalFieldKey(mapping.getCanonicalFieldKey())
                    .transformType(mapping.getTransformType() == null ? null : mapping.getTransformType().name())
                    .resolvedValue(result.value())
                    .present(result.present())
                    .requiredForPackage(mapping.isRequiredForPackage())
                    .consultantReviewRequired(reviewRequired)
                    .status(status)
                    .sourceSummary(sourceSummary(canonical))
                    .diagnostic(result.diagnostic())
                    .build());
        }

        // Required PDF fields with no mapping at all.
        List<String> unmappedRequired = new ArrayList<>();
        for (FormFieldDefinition ffd : fieldDefinitionRepository.findByFormDefinitionIdOrderByPageNumber(form.getId())) {
            if (ffd.isRequired() && !mappedFieldNames.contains(ffd.getPdfFieldName())) {
                unmappedRequired.add(ffd.getPdfFieldName());
            }
        }

        return builder
                .mappingVersionId(mv.getId())
                .mappingVersion(mv.getMappingVersion())
                .mappingStatus(mv.getStatus().name())
                .fields(fields)
                .unmappedRequiredFieldNames(unmappedRequired)
                .build();
    }

    /**
     * Resolve the PDF values (pdfFieldName -> value) for one form against a snapshot,
     * for use by the generation engine. Only non-blank resolved values are returned.
     */
    @Transactional(readOnly = true)
    public Map<String, String> resolveValues(FormDefinition form, CanonicalDataSnapshotDto snapshot) {
        Map<String, CanonicalValueDto> byKey = indexByKey(snapshot);
        Map<String, String> values = new LinkedHashMap<>();

        Optional<FormMappingVersion> approved = mappingVersionRepository
                .findFirstByFormDefinitionIdAndStatusOrderByMappingVersionDesc(form.getId(), MappingStatus.APPROVED);
        if (approved.isEmpty()) {
            return values;
        }
        for (FormFieldMapping mapping : fieldMappingRepository.findByMappingVersionId(approved.get().getId())) {
            MappedResult result = applyTransform(mapping, byKey.get(mapping.getCanonicalFieldKey()));
            if (result.present()) {
                values.put(mapping.getFormFieldDefinition().getPdfFieldName(), result.value());
            }
        }
        return values;
    }

    private MappedResult applyTransform(FormFieldMapping mapping, CanonicalValueDto canonical) {
        String canonicalValue = canonical == null ? null : canonical.getValue();

        switch (mapping.getTransformType()) {
            case DIRECT -> {
                if (canonical == null) {
                    return new MappedResult(null, false, NO_SOURCE,
                            "Canonical field '" + mapping.getCanonicalFieldKey() + "' is not in the snapshot.");
                }
                if (!StringUtils.hasText(canonicalValue)) {
                    return new MappedResult(null, false, MISSING_VALUE, "No value resolved for this field.");
                }
                return new MappedResult(canonicalValue, true, MAPPED, null);
            }
            case DEFAULT_VALUE -> {
                String def = mapping.getDefaultValue();
                if (!StringUtils.hasText(def)) {
                    return new MappedResult(null, false, MISSING_VALUE, "No default value configured.");
                }
                return new MappedResult(def, true, MAPPED, null);
            }
            case DATE_FORMAT -> {
                if (!StringUtils.hasText(canonicalValue)) {
                    return new MappedResult(null, false, MISSING_VALUE, "No date value to format.");
                }
                String pattern = configText(mapping.getTransformConfig(), "pattern");
                if (pattern == null) {
                    return new MappedResult(canonicalValue, true, MAPPED, null);
                }
                try {
                    String formatted = LocalDate.parse(canonicalValue)
                            .format(DateTimeFormatter.ofPattern(pattern));
                    return new MappedResult(formatted, true, MAPPED, null);
                } catch (Exception e) {
                    return new MappedResult(canonicalValue, true, REVIEW_REQUIRED,
                            "Could not format date '" + canonicalValue + "' with pattern '" + pattern + "'.");
                }
            }
            case CHECKBOX_BOOLEAN -> {
                String onValue = textOrDefault(configText(mapping.getTransformConfig(), "onValue"), "Yes");
                String offValue = textOrDefault(configText(mapping.getTransformConfig(), "offValue"), "No");
                boolean truthy = isTruthy(canonicalValue);
                return new MappedResult(truthy ? onValue : offValue, true, MAPPED, null);
            }
            case ENUM_MAP -> {
                if (!StringUtils.hasText(canonicalValue)) {
                    return new MappedResult(null, false, MISSING_VALUE, "No value to map.");
                }
                String mapped = configText(mapping.getTransformConfig(), canonicalValue);
                if (mapped == null) {
                    return new MappedResult(canonicalValue, true, REVIEW_REQUIRED,
                            "No enum mapping for value '" + canonicalValue + "'.");
                }
                return new MappedResult(mapped, true, MAPPED, null);
            }
            default -> {
                // CONCAT, SPLIT_NAME, LIST_ROW, CUSTOM - not yet supported in preview.
                return new MappedResult(canonicalValue, StringUtils.hasText(canonicalValue), UNSUPPORTED_TRANSFORM,
                        "Transform " + mapping.getTransformType() + " is not yet supported in preview.");
            }
        }
    }

    private Map<String, CanonicalValueDto> indexByKey(CanonicalDataSnapshotDto snapshot) {
        Map<String, CanonicalValueDto> byKey = new LinkedHashMap<>();
        if (snapshot != null && snapshot.getValues() != null) {
            for (CanonicalValueDto v : snapshot.getValues()) {
                byKey.put(v.getFieldKey(), v);
            }
        }
        return byKey;
    }

    private String sourceSummary(CanonicalValueDto canonical) {
        if (canonical == null || canonical.getSources() == null) return null;
        return canonical.getSources().stream()
                .filter(s -> s.isSelected())
                .map(s -> "from " + s.getSourceLabel())
                .findFirst()
                .orElse(null);
    }

    /** Read a string property from a JSON transform config; null if absent or unparseable. */
    private String configText(String config, String key) {
        if (!StringUtils.hasText(config) || key == null) return null;
        try {
            JsonNode node = objectMapper.readTree(config);
            JsonNode value = node.get(key);
            return value == null || value.isNull() ? null : value.asText();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTruthy(String value) {
        if (!StringUtils.hasText(value)) return false;
        String v = value.trim().toLowerCase();
        return v.equals("true") || v.equals("yes") || v.equals("y") || v.equals("1");
    }

    private String textOrDefault(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }
}
