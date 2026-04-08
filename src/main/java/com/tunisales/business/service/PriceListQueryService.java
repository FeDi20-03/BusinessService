package com.tunisales.business.service;

import com.tunisales.business.domain.*; // for static metamodels
import com.tunisales.business.domain.PriceList;
import com.tunisales.business.repository.PriceListRepository;
import com.tunisales.business.service.criteria.PriceListCriteria;
import com.tunisales.business.service.dto.PriceListDTO;
import com.tunisales.business.service.mapper.PriceListMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PriceList} entities in the database.
 * The main input is a {@link PriceListCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PriceListDTO} or a {@link Page} of {@link PriceListDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PriceListQueryService extends QueryService<PriceList> {

    private final Logger log = LoggerFactory.getLogger(PriceListQueryService.class);

    private final PriceListRepository priceListRepository;

    private final PriceListMapper priceListMapper;

    public PriceListQueryService(PriceListRepository priceListRepository, PriceListMapper priceListMapper) {
        this.priceListRepository = priceListRepository;
        this.priceListMapper = priceListMapper;
    }

    /**
     * Return a {@link List} of {@link PriceListDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PriceListDTO> findByCriteria(PriceListCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PriceList> specification = createSpecification(criteria);
        return priceListMapper.toDto(priceListRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PriceListDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PriceListDTO> findByCriteria(PriceListCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PriceList> specification = createSpecification(criteria);
        return priceListRepository.findAll(specification, page).map(priceListMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PriceListCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PriceList> specification = createSpecification(criteria);
        return priceListRepository.count(specification);
    }

    /**
     * Function to convert {@link PriceListCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PriceList> createSpecification(PriceListCriteria criteria) {
        Specification<PriceList> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PriceList_.id));
            }
            if (criteria.getUnitPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUnitPrice(), PriceList_.unitPrice));
            }
            if (criteria.getMaxDiscountPct() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMaxDiscountPct(), PriceList_.maxDiscountPct));
            }
            if (criteria.getValidFrom() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValidFrom(), PriceList_.validFrom));
            }
            if (criteria.getValidTo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValidTo(), PriceList_.validTo));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), PriceList_.isActive));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), PriceList_.createdAt));
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProductId(), root -> root.join(PriceList_.product, JoinType.LEFT).get(Product_.id))
                    );
            }
            if (criteria.getClientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getClientId(), root -> root.join(PriceList_.client, JoinType.LEFT).get(Client_.id))
                    );
            }
        }
        return specification;
    }
}
