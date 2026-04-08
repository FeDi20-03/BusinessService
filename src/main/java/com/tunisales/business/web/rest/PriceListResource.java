package com.tunisales.business.web.rest;

import com.tunisales.business.repository.PriceListRepository;
import com.tunisales.business.service.PriceListQueryService;
import com.tunisales.business.service.PriceListService;
import com.tunisales.business.service.criteria.PriceListCriteria;
import com.tunisales.business.service.dto.PriceListDTO;
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
 * REST controller for managing {@link com.tunisales.business.domain.PriceList}.
 */
@RestController
@RequestMapping("/api")
public class PriceListResource {

    private final Logger log = LoggerFactory.getLogger(PriceListResource.class);

    private static final String ENTITY_NAME = "businessServicePriceList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PriceListService priceListService;

    private final PriceListRepository priceListRepository;

    private final PriceListQueryService priceListQueryService;

    public PriceListResource(
        PriceListService priceListService,
        PriceListRepository priceListRepository,
        PriceListQueryService priceListQueryService
    ) {
        this.priceListService = priceListService;
        this.priceListRepository = priceListRepository;
        this.priceListQueryService = priceListQueryService;
    }

    /**
     * {@code POST  /price-lists} : Create a new priceList.
     *
     * @param priceListDTO the priceListDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new priceListDTO, or with status {@code 400 (Bad Request)} if the priceList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/price-lists")
    public ResponseEntity<PriceListDTO> createPriceList(@Valid @RequestBody PriceListDTO priceListDTO) throws URISyntaxException {
        log.debug("REST request to save PriceList : {}", priceListDTO);
        if (priceListDTO.getId() != null) {
            throw new BadRequestAlertException("A new priceList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PriceListDTO result = priceListService.save(priceListDTO);
        return ResponseEntity
            .created(new URI("/api/price-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /price-lists/:id} : Updates an existing priceList.
     *
     * @param id the id of the priceListDTO to save.
     * @param priceListDTO the priceListDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priceListDTO,
     * or with status {@code 400 (Bad Request)} if the priceListDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the priceListDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/price-lists/{id}")
    public ResponseEntity<PriceListDTO> updatePriceList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PriceListDTO priceListDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PriceList : {}, {}", id, priceListDTO);
        if (priceListDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priceListDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priceListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PriceListDTO result = priceListService.update(priceListDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, priceListDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /price-lists/:id} : Partial updates given fields of an existing priceList, field will ignore if it is null
     *
     * @param id the id of the priceListDTO to save.
     * @param priceListDTO the priceListDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priceListDTO,
     * or with status {@code 400 (Bad Request)} if the priceListDTO is not valid,
     * or with status {@code 404 (Not Found)} if the priceListDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the priceListDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/price-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PriceListDTO> partialUpdatePriceList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PriceListDTO priceListDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PriceList partially : {}, {}", id, priceListDTO);
        if (priceListDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priceListDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priceListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PriceListDTO> result = priceListService.partialUpdate(priceListDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, priceListDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /price-lists} : get all the priceLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of priceLists in body.
     */
    @GetMapping("/price-lists")
    public ResponseEntity<List<PriceListDTO>> getAllPriceLists(
        PriceListCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PriceLists by criteria: {}", criteria);
        Page<PriceListDTO> page = priceListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /price-lists/count} : count all the priceLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/price-lists/count")
    public ResponseEntity<Long> countPriceLists(PriceListCriteria criteria) {
        log.debug("REST request to count PriceLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(priceListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /price-lists/:id} : get the "id" priceList.
     *
     * @param id the id of the priceListDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the priceListDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/price-lists/{id}")
    public ResponseEntity<PriceListDTO> getPriceList(@PathVariable Long id) {
        log.debug("REST request to get PriceList : {}", id);
        Optional<PriceListDTO> priceListDTO = priceListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(priceListDTO);
    }

    /**
     * {@code DELETE  /price-lists/:id} : delete the "id" priceList.
     *
     * @param id the id of the priceListDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/price-lists/{id}")
    public ResponseEntity<Void> deletePriceList(@PathVariable Long id) {
        log.debug("REST request to delete PriceList : {}", id);
        priceListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
