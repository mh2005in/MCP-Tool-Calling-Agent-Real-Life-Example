package com.immiauto.repository;

import com.immiauto.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByClientNumber(String clientNumber);
    Optional<Client> findByEmail(String email);
    List<Client> findByFullNameContainingIgnoreCase(String name);
    List<Client> findByConsultantId(UUID consultantId);

    Optional<Client> findByFullNameIgnoreCaseAndDateOfBirthAndEmailIgnoreCase(
            String fullName, LocalDate dateOfBirth, String email);

    @Query("SELECT c FROM Client c WHERE c.consultant.id = :consultantId AND " +
           "(LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Client> searchByConsultant(UUID consultantId, String search);

    @Query("SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Client> search(String search);
}
