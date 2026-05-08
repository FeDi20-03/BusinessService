package com.tunisales.business.repository;

import com.tunisales.business.domain.BonusEntry;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface BonusEntryRepository extends JpaRepository<BonusEntry, Long> {
    /** Sub-step 2.9 — list a vendeur's entries for a given inclusive interval. */
    @Query("select be from BonusEntry be where be.vendeurLogin = :login and be.computedAt >= :start and be.computedAt < :end")
    List<BonusEntry> findForVendeurAndPeriod(
        @Param("login") String login,
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end
    );
}
