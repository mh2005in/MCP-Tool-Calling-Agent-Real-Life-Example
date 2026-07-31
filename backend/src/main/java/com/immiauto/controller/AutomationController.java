package com.immiauto.controller;

import java.util.UUID;

import com.immiauto.service.ConsistencyCheckService;
import com.immiauto.service.DocumentClassificationService;
import com.immiauto.service.LmiaCalculatorService;
import com.immiauto.service.PoliceCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AutomationController {

    private final DocumentClassificationService classificationService;
    private final ConsistencyCheckService consistencyCheckService;
    private final PoliceCertificateService policeCertificateService;
    private final LmiaCalculatorService lmiaCalculatorService;

    @GetMapping("/documents/classify")
    public Map<String, String> classifyDocument(@RequestParam String fileName) {
        return classificationService.suggestClassification(fileName);
    }

    @GetMapping("/cases/{caseId}/consistency-check")
    public List<Map<String, String>> checkConsistency(@PathVariable UUID caseId) {
        return consistencyCheckService.checkConsistency(caseId);
    }

    @GetMapping("/cases/{caseId}/police-certificates")
    public List<Map<String, Object>> getRequiredPoliceCertificates(@PathVariable UUID caseId) {
        return policeCertificateService.determineRequiredCertificates(caseId);
    }

    @GetMapping("/cases/{caseId}/lmia-compliance")
    public Map<String, Object> getLmiaCompliance(@PathVariable UUID caseId) {
        return lmiaCalculatorService.calculateRecruitmentCompliance(caseId);
    }
}
