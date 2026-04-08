package com.tunisales.business.web.rest;

import com.tunisales.business.repository.DeliveryRepository;
import com.tunisales.business.service.DeliveryQueryService;
import com.tunisales.business.service.DeliveryService;
import com.tunisales.business.service.criteria.DeliveryCriteria;
import com.tunisales.business.service.dto.DeliveryDTO;
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
 * REST controller for managing {@link com.tunisales.business.domain.Delivery}.
 */
@RestController
@RequestMapping("/api")
public class DeliveryResource {

    private final Logger log = LoggerFactory.getLogger(DeliveryResource.class);

    private static final String ENTITY_NAME = "businessServiceDelivery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeliveryService deliveryService;

    private final DeliveryRepository deliveryRepository;

    private final DeliveryQueryService deliveryQueryService;

    public DeliveryResource(
        DeliveryService deliveryService,
        DeliveryRepository deliveryRepository,
        DeliveryQueryService deliveryQueryService
    ) {
        this.deliveryService = deliveryService;
        this.deliveryRepository = deliveryRepository;
        this.deliveryQueryService = deliveryQueryService;
    }

    /**
     * {@code POST  /deliveries} : Create a new delivery.
     *
     * @param deliveryDTO the deliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliveryDTO, or with status {@code 400 (Bad Request)} if the delivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/deliveries")
    public ResponseEntity<DeliveryDTO> createDelivery(@Valid @RequestBody DeliveryDTO deliveryDTO) throws URISyntaxException {
        log.debug("REST request to save Delivery : {}", deliveryDTO);
        if (deliveryDTO.getId() != null) {
            throw new BadRequestAlertException("A new delivery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DeliveryDTO result = deliveryService.save(deliveryDTO);
        return ResponseEntity
            .created(new URI("/api/deliveries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /deliveries/:id} : Updates an existing delivery.
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/deliveries/{id}")
    public ResponseEntity<DeliveryDTO> updateDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Delivery : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliveryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DeliveryDTO result = deliveryService.update(deliveryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliveryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /deliveries/:id} : Partial updates given fields of an existing delivery, field will ignore if it is null
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/deliveries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeliveryDTO> partialUpdateDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Delivery partially : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliveryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeliveryDTO> result = deliveryService.partialUpdate(deliveryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliveryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /deliveries} : get all the deliveries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliveries in body.
     */
    @GetMapping("/deliveries")
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries(
        DeliveryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Deliveries by criteria: {}", criteria);
        Page<DeliveryDTO> page = deliveryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deliveries/count} : count all the deliveries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/deliveries/count")
    public ResponseEntity<Long> countDeliveries(DeliveryCriteria criteria) {
        log.debug("REST request to count Deliveries by criteria: {}", criteria);
        return ResponseEntity.ok().body(deliveryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /deliveries/:id} : get the "id" delivery.
     *
     * @param id the id of the deliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/deliveries/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable Long id) {
        log.debug("REST request to get Delivery : {}", id);
        Optional<DeliveryDTO> deliveryDTO = deliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryDTO);
    }

    /**
     * {@code DELETE  /deliveries/:id} : delete the "id" delivery.
     *
     * @param id the id of the deliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/deliveries/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        log.debug("REST request to delete Delivery : {}", id);
        deliveryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
