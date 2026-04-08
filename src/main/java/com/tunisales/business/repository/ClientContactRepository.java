package com.tunisales.business.repository;

import com.tunisales.business.domain.ClientContact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientContact entity.
 */
@Repository
public interface ClientContactRepository extends JpaRepository<ClientContact, Long> {
    default Optional<ClientContact> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClientContact> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClientContact> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct clientContact from ClientContact clientContact left join fetch clientContact.client",
        countQuery = "select count(distinct clientContact) from ClientContact clientContact"
    )
    Page<ClientContact> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct clientContact from ClientContact clientContact left join fetch clientContact.client")
    List<ClientContact> findAllWithToOneRelationships();

    @Query("select clientContact from ClientContact clientContact left join fetch clientContact.client where clientContact.id =:id")
    Optional<ClientContact> findOneWithToOneRelationships(@Param("id") Long id);
}
