package com.tunisales.business.web.rest;

import com.tunisales.business.repository.MissionRepository;
import com.tunisales.business.service.MissionQueryService;
import com.tunisales.business.service.MissionService;
import com.tunisales.business.service.criteria.MissionCriteria;
import com.tunisales.business.service.dto.MissionDTO;
import com.tunisales.business.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.business.domain.Mission}.
 */
@RestController
@RequestMapping("/api")
public class MissionResource {

    private final Logger log = LoggerFactory.getLogger(MissionResource.class);

    private static final String ENTITY_NAME = "businessServiceMission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MissionService missionService;

    private final MissionRepository missionRepository;

    private final MissionQueryService missionQueryService;

    public MissionResource(MissionService missionService, MissionRepository missionRepository, MissionQueryService missionQueryService) {
        this.missionService = missionService;
        this.missionRepository = missionRepository;
        this.missionQueryService = missionQueryService;
    }

    /**
     * {@code POST  /missions} : Create a new mission.
     *
     * @param missionDTO the missionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new missionDTO, or with status {@code 400 (Bad Request)} if the mission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/missions")
    public ResponseEntity<MissionDTO> createMission(@Valid @RequestBody MissionDTO missionDTO) throws URISyntaxException {
        log.debug("REST request to save Mission : {}", missionDTO);
        if (missionDTO.getId() != null) {
            throw new BadRequestAlertException("A new mission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MissionDTO result = missionService.save(missionDTO);
        return ResponseEntity
            .created(new URI("/api/missions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /missions/:id} : Updates an existing mission.
     *
     * @param id the id of the missionDTO to save.
     * @param missionDTO the missionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionDTO,
     * or with status {@code 400 (Bad Request)} if the missionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the missionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/missions/{id}")
    public ResponseEntity<MissionDTO> updateMission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MissionDTO missionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Mission : {}, {}", id, missionDTO);
        if (missionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MissionDTO result = missionService.update(missionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, missionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /missions/:id} : Partial updates given fields of an existing mission, field will ignore if it is null
     *
     * @param id the id of the missionDTO to save.
     * @param missionDTO the missionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionDTO,
     * or with status {@code 400 (Bad Request)} if the missionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the missionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the missionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/missions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MissionDTO> partialUpdateMission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MissionDTO missionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Mission partially : {}, {}", id, missionDTO);
        if (missionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MissionDTO> result = missionService.partialUpdate(missionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, missionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /missions} : get all the missions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of missions in body.
     */
    @GetMapping("/missions")
    public ResponseEntity<List<MissionDTO>> getAllMissions(
        MissionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Missions by criteria: {}", criteria);
        Page<MissionDTO> page = missionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /missions/count} : count all the missions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/missions/count")
    public ResponseEntity<Long> countMissions(MissionCriteria criteria) {
        log.debug("REST request to count Missions by criteria: {}", criteria);
        return ResponseEntity.ok().body(missionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /missions/:id} : get the "id" mission.
     *
     * @param id the id of the missionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the missionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/missions/{id}")
    public ResponseEntity<MissionDTO> getMission(@PathVariable Long id) {
        log.debug("REST request to get Mission : {}", id);
        Optional<MissionDTO> missionDTO = missionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(missionDTO);
    }

    /**
     * {@code DELETE  /missions/:id} : delete the "id" mission.
     *
     * @param id the id of the missionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/missions/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        log.debug("REST request to delete Mission : {}", id);
        missionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
