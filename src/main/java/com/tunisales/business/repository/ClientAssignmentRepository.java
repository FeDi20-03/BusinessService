package com.tunisales.business.repository;

import com.tunisales.business.domain.ClientAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface ClientAssignmentRepository extends JpaRepository<ClientAssignment, Long> {
    /** Sub-step 2.8 — locate the current assignment for a given client (latest wins). */
    Optional<ClientAssignment> findFirstByClientIdOrderByAssignedAtDesc(Long clientId);

    /** Sub-step 2.8 — list all assignments for a given commercial (used by /clients/by-commercial/{login}). */
    List<ClientAssignment> findByCommercialLogin(String commercialLogin);
}
