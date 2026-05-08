package com.tunisales.business.repository;

import com.tunisales.business.domain.BonusRule;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface BonusRuleRepository extends JpaRepository<BonusRule, Long> {
    /** Find active rules matching either the product or zone (or both). */
    List<BonusRule> findByIsActiveTrue();
}
