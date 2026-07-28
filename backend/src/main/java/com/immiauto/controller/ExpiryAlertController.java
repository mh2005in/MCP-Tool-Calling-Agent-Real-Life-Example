package com.immiauto.controller;

import com.immiauto.dto.ExpiryAlertDto;
import com.immiauto.service.ExpiryAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ExpiryAlertController {

    private final ExpiryAlertService expiryAlertService;

    @GetMapping("/consultants/{consultantId}/expiry-alerts")
    public List<ExpiryAlertDto> getAlertsByConsultant(@PathVariable Long consultantId) {
        return expiryAlertService.getAlertsByConsultant(consultantId);
    }

    @GetMapping("/cases/{caseId}/expiry-alerts")
    public List<ExpiryAlertDto> getAlertsForCase(@PathVariable Long caseId) {
        return expiryAlertService.getAlertsForCase(caseId);
    }

    @PutMapping("/expiry-alerts/{alertId}/acknowledge")
    public ExpiryAlertDto acknowledgeAlert(@PathVariable Long alertId,
                                            @RequestParam String acknowledgedBy) {
        return expiryAlertService.acknowledgeAlert(alertId, acknowledgedBy);
    }

    @PostMapping("/expiry-alerts/scan")
    public ResponseEntity<String> triggerScan() {
        expiryAlertService.scanForExpiringDocuments();
        return ResponseEntity.ok("Expiry scan completed");
    }
}
