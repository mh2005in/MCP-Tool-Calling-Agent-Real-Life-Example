package com.immiauto.controller;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.DashboardDto;
import com.immiauto.dto.OrgDashboardDto;
import com.immiauto.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ApiPaths.DASHBOARD_CONTROLLER)
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(ApiPaths.DASHBOARD_GET_BY_ID)
    @PreAuthorize("@consultantAccess.canAccess(#consultantId)")
    public ResponseEntity<DashboardDto> getDashboard(@PathVariable Long consultantId) {
        return ResponseEntity.ok(dashboardService.getDashboard(consultantId));
    }

    @GetMapping(ApiPaths.DASHBOARD_ORG)
    @PreAuthorize("@adminGuard.isAdminConsultant()")
    public ResponseEntity<OrgDashboardDto> getOrganizationDashboard() {
        return ResponseEntity.ok(dashboardService.getOrganizationDashboard());
    }
}
