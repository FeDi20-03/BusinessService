package com.tunisales.business.repository;

import com.tunisales.business.domain.SalesReturnLine;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface SalesReturnLineRepository extends JpaRepository<SalesReturnLine, Long> {
    List<SalesReturnLine> findBySalesReturnId(Long salesReturnId);
}
