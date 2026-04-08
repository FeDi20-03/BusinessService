package com.tunisales.business.repository;

import com.tunisales.business.domain.Delivery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Delivery entity.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {
    default Optional<Delivery> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Delivery> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Delivery> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct delivery from Delivery delivery left join fetch delivery.order",
        countQuery = "select count(distinct delivery) from Delivery delivery"
    )
    Page<Delivery> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct delivery from Delivery delivery left join fetch delivery.order")
    List<Delivery> findAllWithToOneRelationships();

    @Query("select delivery from Delivery delivery left join fetch delivery.order where delivery.id =:id")
    Optional<Delivery> findOneWithToOneRelationships(@Param("id") Long id);
}
