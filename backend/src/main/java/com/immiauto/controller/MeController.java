package com.immiauto.controller;

import com.immiauto.constants.ApiPaths;
import com.immiauto.dto.MeDto;
import com.immiauto.entity.AppUser;
import com.immiauto.mapper.AppUserMapper;
import com.immiauto.repository.ConsultantRepository;
import com.immiauto.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Returns the profile of the currently authenticated Entra user. The frontend
 * calls this after login to discover its role, consultant id, and admin status.
 */
@RestController
@RequestMapping(ApiPaths.ME_CONTROLLER)
@RequiredArgsConstructor
public class MeController {

    private final CurrentUserProvider currentUserProvider;
    private final ConsultantRepository consultantRepository;
    private final AppUserMapper appUserMapper;

    @GetMapping
    public ResponseEntity<MeDto> me() {
        AppUser user = currentUserProvider.getCurrentUser();
        MeDto dto = appUserMapper.toMeDto(user);
        if (user.getConsultantId() != null) {
            consultantRepository.findById(user.getConsultantId()).ifPresent(consultant -> {
                dto.setConsultantAdmin(consultant.isAdmin());
                dto.setConsultantName(consultant.getFullName());
                dto.setCompanyName(consultant.getCompanyName());
            });
        }
        return ResponseEntity.ok(dto);
    }
}
