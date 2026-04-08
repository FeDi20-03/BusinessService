package com.tunisales.business.service;

import com.tunisales.business.domain.*; // for static metamodels
import com.tunisales.business.domain.Client;
import com.tunisales.business.repository.ClientRepository;
import com.tunisales.business.service.criteria.ClientCriteria;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.mapper.ClientMapper;
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
 * Service for executing complex queries for {@link Client} entities in the database.
 * The main input is a {@link ClientCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ClientDTO} or a {@link Page} of {@link ClientDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientQueryService extends QueryService<Client> {

    private final Logger log = LoggerFactory.getLogger(ClientQueryService.class);

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientQueryService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    /**
     * Return a {@link List} of {@link ClientDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ClientDTO> findByCriteria(ClientCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Client> specification = createSpecification(criteria);
        return clientMapper.toDto(clientRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ClientDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientDTO> findByCriteria(ClientCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Client> specification = createSpecification(criteria);
        return clientRepository.findAll(specification, page).map(clientMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClientCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Client> specification = createSpecification(criteria);
        return clientRepository.count(specification);
    }

    /**
     * Function to convert {@link ClientCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Client> createSpecification(ClientCriteria criteria) {
        Specification<Client> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Client_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), Client_.tenantId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Client_.name));
            }
            if (criteria.getTaxId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTaxId(), Client_.taxId));
            }
            if (criteria.getClientType() != null) {
                specification = specification.and(buildSpecification(criteria.getClientType(), Client_.clientType));
            }
            if (criteria.getCreditLimit() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreditLimit(), Client_.creditLimit));
            }
            if (criteria.getCreditUsed() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreditUsed(), Client_.creditUsed));
            }
            if (criteria.getPaymentTermsDays() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPaymentTermsDays(), Client_.paymentTermsDays));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Client_.status));
            }
            if (criteria.getLastOrderAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastOrderAt(), Client_.lastOrderAt));
            }
            if (criteria.getIsDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getIsDeleted(), Client_.isDeleted));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Client_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Client_.updatedAt));
            }
            if (criteria.getContactsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getContactsId(),
                            root -> root.join(Client_.contacts, JoinType.LEFT).get(ClientContact_.id)
                        )
                    );
            }
            if (criteria.getPriceListsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPriceListsId(),
                            root -> root.join(Client_.priceLists, JoinType.LEFT).get(PriceList_.id)
                        )
                    );
            }
            if (criteria.getOrdersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrdersId(), root -> root.join(Client_.orders, JoinType.LEFT).get(Order_.id))
                    );
            }
        }
        return specification;
    }
}
