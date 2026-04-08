package com.tunisales.business.repository;

import com.tunisales.business.domain.PriceList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PriceList entity.
 */
@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long>, JpaSpecificationExecutor<PriceList> {
    default Optional<PriceList> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PriceList> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PriceList> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct priceList from PriceList priceList left join fetch priceList.product left join fetch priceList.client",
        countQuery = "select count(distinct priceList) from PriceList priceList"
    )
    Page<PriceList> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct priceList from PriceList priceList left join fetch priceList.product left join fetch priceList.client")
    List<PriceList> findAllWithToOneRelationships();

    @Query(
        "select priceList from PriceList priceList left join fetch priceList.product left join fetch priceList.client where priceList.id =:id"
    )
    Optional<PriceList> findOneWithToOneRelationships(@Param("id") Long id);
}
