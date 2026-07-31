package com.immiauto.repository;

import com.immiauto.entity.PartyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartyProfileRepository extends JpaRepository<PartyProfile, UUID> {
    List<PartyProfile> findByImmigrationCaseId(UUID caseId);
    List<PartyProfile> findByImmigrationCaseIdAndPartyType(UUID caseId, String partyType);
    Optional<PartyProfile> findByAccessToken(String accessToken);
}
