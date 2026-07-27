package com.immiauto.repository;

import com.immiauto.entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    Optional<Consultant> findByConsultantNumber(String consultantNumber);
    Optional<Consultant> findByEmail(String email);
    boolean existsByEmail(String email);
}
