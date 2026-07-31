package com.immiauto.repository;

import com.immiauto.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByExternalSubject(String externalSubject);
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByConsultantId(UUID consultantId);
}
