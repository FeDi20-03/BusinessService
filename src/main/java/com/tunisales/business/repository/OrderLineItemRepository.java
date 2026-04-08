package com.tunisales.business.repository;

import com.tunisales.business.domain.OrderLineItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderLineItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {}
