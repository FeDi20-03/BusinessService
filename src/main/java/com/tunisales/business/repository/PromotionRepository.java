package com.tunisales.business.repository;

import com.tunisales.business.domain.Promotion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {}
