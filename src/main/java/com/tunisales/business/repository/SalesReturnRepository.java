package com.tunisales.business.repository;

import com.tunisales.business.domain.SalesReturn;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface SalesReturnRepository extends JpaRepository<SalesReturn, Long>, JpaSpecificationExecutor<SalesReturn> {}
