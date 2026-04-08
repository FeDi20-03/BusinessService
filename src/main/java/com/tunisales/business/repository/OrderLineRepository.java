package com.tunisales.business.repository;

import com.tunisales.business.domain.OrderLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderLine entity.
 */
@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    default Optional<OrderLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OrderLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OrderLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct orderLine from OrderLine orderLine left join fetch orderLine.product left join fetch orderLine.order",
        countQuery = "select count(distinct orderLine) from OrderLine orderLine"
    )
    Page<OrderLine> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct orderLine from OrderLine orderLine left join fetch orderLine.product left join fetch orderLine.order")
    List<OrderLine> findAllWithToOneRelationships();

    @Query(
        "select orderLine from OrderLine orderLine left join fetch orderLine.product left join fetch orderLine.order where orderLine.id =:id"
    )
    Optional<OrderLine> findOneWithToOneRelationships(@Param("id") Long id);
}
