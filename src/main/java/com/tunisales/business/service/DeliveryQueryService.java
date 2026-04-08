package com.tunisales.business.service;

import com.tunisales.business.domain.*; // for static metamodels
import com.tunisales.business.domain.Delivery;
import com.tunisales.business.repository.DeliveryRepository;
import com.tunisales.business.service.criteria.DeliveryCriteria;
import com.tunisales.business.service.dto.DeliveryDTO;
import com.tunisales.business.service.mapper.DeliveryMapper;
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
 * Service for executing complex queries for {@link Delivery} entities in the database.
 * The main input is a {@link DeliveryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DeliveryDTO} or a {@link Page} of {@link DeliveryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeliveryQueryService extends QueryService<Delivery> {

    private final Logger log = LoggerFactory.getLogger(DeliveryQueryService.class);

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    public DeliveryQueryService(DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
    }

    /**
     * Return a {@link List} of {@link DeliveryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DeliveryDTO> findByCriteria(DeliveryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Delivery> specification = createSpecification(criteria);
        return deliveryMapper.toDto(deliveryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DeliveryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeliveryDTO> findByCriteria(DeliveryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Delivery> specification = createSpecification(criteria);
        return deliveryRepository.findAll(specification, page).map(deliveryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeliveryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Delivery> specification = createSpecification(criteria);
        return deliveryRepository.count(specification);
    }

    /**
     * Function to convert {@link DeliveryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Delivery> createSpecification(DeliveryCriteria criteria) {
        Specification<Delivery> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Delivery_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), Delivery_.tenantId));
            }
            if (criteria.getDeliveryNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDeliveryNumber(), Delivery_.deliveryNumber));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Delivery_.status));
            }
            if (criteria.getTrackingNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTrackingNumber(), Delivery_.trackingNumber));
            }
            if (criteria.getShippedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getShippedAt(), Delivery_.shippedAt));
            }
            if (criteria.getDeliveredAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeliveredAt(), Delivery_.deliveredAt));
            }
            if (criteria.getConfirmedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getConfirmedAt(), Delivery_.confirmedAt));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), Delivery_.notes));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Delivery_.createdAt));
            }
            if (criteria.getOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrderId(), root -> root.join(Delivery_.order, JoinType.LEFT).get(Order_.id))
                    );
            }
        }
        return specification;
    }
}
