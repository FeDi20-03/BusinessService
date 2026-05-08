package com.tunisales.business.repository;

import com.tunisales.business.domain.InvoiceLine;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link InvoiceLine} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
    List<InvoiceLine> findByInvoiceId(Long invoiceId);
}
