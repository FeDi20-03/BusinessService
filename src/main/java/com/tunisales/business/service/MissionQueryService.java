package com.tunisales.business.service;

import com.tunisales.business.domain.*; // for static metamodels
import com.tunisales.business.domain.Mission;
import com.tunisales.business.repository.MissionRepository;
import com.tunisales.business.service.criteria.MissionCriteria;
import com.tunisales.business.service.dto.MissionDTO;
import com.tunisales.business.service.mapper.MissionMapper;
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
 * Service for executing complex queries for {@link Mission} entities in the database.
 * The main input is a {@link MissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MissionDTO} or a {@link Page} of {@link MissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MissionQueryService extends QueryService<Mission> {

    private final Logger log = LoggerFactory.getLogger(MissionQueryService.class);

    private final MissionRepository missionRepository;

    private final MissionMapper missionMapper;

    public MissionQueryService(MissionRepository missionRepository, MissionMapper missionMapper) {
        this.missionRepository = missionRepository;
        this.missionMapper = missionMapper;
    }

    /**
     * Return a {@link List} of {@link MissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MissionDTO> findByCriteria(MissionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Mission> specification = createSpecification(criteria);
        return missionMapper.toDto(missionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MissionDTO> findByCriteria(MissionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Mission> specification = createSpecification(criteria);
        return missionRepository.findAll(specification, page).map(missionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MissionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Mission> specification = createSpecification(criteria);
        return missionRepository.count(specification);
    }

    /**
     * Function to convert {@link MissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Mission> createSpecification(MissionCriteria criteria) {
        Specification<Mission> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Mission_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), Mission_.tenantId));
            }
            if (criteria.getAssignedToLogin() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAssignedToLogin(), Mission_.assignedToLogin));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Mission_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Mission_.description));
            }
            if (criteria.getMissionDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMissionDate(), Mission_.missionDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Mission_.status));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Mission_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Mission_.updatedAt));
            }
            if (criteria.getVisitsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVisitsId(), root -> root.join(Mission_.visits, JoinType.LEFT).get(Visit_.id))
                    );
            }
        }
        return specification;
    }
}
