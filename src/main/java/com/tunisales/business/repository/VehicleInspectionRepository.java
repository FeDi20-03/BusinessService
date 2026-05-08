package com.tunisales.business.repository;

import com.tunisales.business.domain.VehicleInspection;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface VehicleInspectionRepository extends JpaRepository<VehicleInspection, Long> {}
