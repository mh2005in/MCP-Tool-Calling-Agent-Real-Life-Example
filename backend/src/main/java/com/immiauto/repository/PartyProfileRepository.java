package com.immiauto.repository;

import com.immiauto.entity.PartyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyProfileRepository extends JpaRepository<PartyProfile, Long> {
    List<PartyProfile> findByImmigrationCaseId(Long caseId);
    List<PartyProfile> findByImmigrationCaseIdAndPartyType(Long caseId, String partyType);
    Optional<PartyProfile> findByAccessToken(String accessToken);
}
