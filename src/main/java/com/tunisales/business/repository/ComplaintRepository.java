package com.tunisales.business.repository;

import com.tunisales.business.domain.Complaint;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {}
