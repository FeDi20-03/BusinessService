package com.tunisales.business.service;

import com.tunisales.business.domain.*; // for static metamodels
import com.tunisales.business.domain.Visit;
import com.tunisales.business.repository.VisitRepository;
import com.tunisales.business.service.criteria.VisitCriteria;
import com.tunisales.business.service.dto.VisitDTO;
import com.tunisales.business.service.mapper.VisitMapper;
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
 * Service for executing complex queries for {@link Visit} entities in the database.
 * The main input is a {@link VisitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VisitDTO} or a {@link Page} of {@link VisitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VisitQueryService extends QueryService<Visit> {

    private final Logger log = LoggerFactory.getLogger(VisitQueryService.class);

    private final VisitRepository visitRepository;

    private final VisitMapper visitMapper;

    public VisitQueryService(VisitRepository visitRepository, VisitMapper visitMapper) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
    }

    /**
     * Return a {@link List} of {@link VisitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VisitDTO> findByCriteria(VisitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Visit> specification = createSpecification(criteria);
        return visitMapper.toDto(visitRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link VisitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VisitDTO> findByCriteria(VisitCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Visit> specification = createSpecification(criteria);
        return visitRepository.findAll(specification, page).map(visitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VisitCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Visit> specification = createSpecification(criteria);
        return visitRepository.count(specification);
    }

    /**
     * Function to convert {@link VisitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Visit> createSpecification(VisitCriteria criteria) {
        Specification<Visit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Visit_.id));
            }
            if (criteria.getVisitOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVisitOrder(), Visit_.visitOrder));
            }
            if (criteria.getObjective() != null) {
                specification = specification.and(buildSpecification(criteria.getObjective(), Visit_.objective));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Visit_.status));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), Visit_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), Visit_.longitude));
            }
            if (criteria.getCheckinAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCheckinAt(), Visit_.checkinAt));
            }
            if (criteria.getCheckoutAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCheckoutAt(), Visit_.checkoutAt));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), Visit_.notes));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Visit_.createdAt));
            }
            if (criteria.getClientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getClientId(), root -> root.join(Visit_.client, JoinType.LEFT).get(Client_.id))
                    );
            }
            if (criteria.getMissionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMissionId(), root -> root.join(Visit_.mission, JoinType.LEFT).get(Mission_.id))
                    );
            }
        }
        return specification;
    }
}
