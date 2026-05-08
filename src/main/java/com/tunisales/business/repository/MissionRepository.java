package com.tunisales.business.repository;

import com.tunisales.business.domain.Mission;
import com.tunisales.business.domain.enumeration.MissionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Mission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {
    /** Sub-step 2.7 — list missions assigned to the given login (no status filter). */
    List<Mission> findByAssignedToLogin(String assignedToLogin);

    /** Sub-step 2.7 — list missions assigned to the given login with the given status. */
    List<Mission> findByAssignedToLoginAndStatus(String assignedToLogin, MissionStatus status);
}
