package com.tunisales.business.web.rest;

import com.tunisales.business.repository.OrderLineRepository;
import com.tunisales.business.service.OrderLineService;
import com.tunisales.business.service.dto.OrderLineDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.business.domain.OrderLine}.
 */
@RestController
@RequestMapping("/api")
public class OrderLineResource {

    private final Logger log = LoggerFactory.getLogger(OrderLineResource.class);

    private static final String ENTITY_NAME = "businessServiceOrderLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderLineService orderLineService;

    private final OrderLineRepository orderLineRepository;

    public OrderLineResource(OrderLineService orderLineService, OrderLineRepository orderLineRepository) {
        this.orderLineService = orderLineService;
        this.orderLineRepository = orderLineRepository;
    }

    /**
     * {@code POST  /order-lines} : Create a new orderLine.
     *
     * @param orderLineDTO the orderLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderLineDTO, or with status {@code 400 (Bad Request)} if the orderLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-lines")
    public ResponseEntity<OrderLineDTO> createOrderLine(@Valid @RequestBody OrderLineDTO orderLineDTO) throws URISyntaxException {
        log.debug("REST request to save OrderLine : {}", orderLineDTO);
        if (orderLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderLineDTO result = orderLineService.save(orderLineDTO);
        return ResponseEntity
            .created(new URI("/api/order-lines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-lines/:id} : Updates an existing orderLine.
     *
     * @param id the id of the orderLineDTO to save.
     * @param orderLineDTO the orderLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderLineDTO,
     * or with status {@code 400 (Bad Request)} if the orderLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-lines/{id}")
    public ResponseEntity<OrderLineDTO> updateOrderLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderLineDTO orderLineDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderLine : {}, {}", id, orderLineDTO);
        if (orderLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderLineDTO result = orderLineService.update(orderLineDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderLineDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-lines/:id} : Partial updates given fields of an existing orderLine, field will ignore if it is null
     *
     * @param id the id of the orderLineDTO to save.
     * @param orderLineDTO the orderLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderLineDTO,
     * or with status {@code 400 (Bad Request)} if the orderLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-lines/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderLineDTO> partialUpdateOrderLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderLineDTO orderLineDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderLine partially : {}, {}", id, orderLineDTO);
        if (orderLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderLineDTO> result = orderLineService.partialUpdate(orderLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-lines} : get all the orderLines.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderLines in body.
     */
    @GetMapping("/order-lines")
    public ResponseEntity<List<OrderLineDTO>> getAllOrderLines(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of OrderLines");
        Page<OrderLineDTO> page;
        if (eagerload) {
            page = orderLineService.findAllWithEagerRelationships(pageable);
        } else {
            page = orderLineService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-lines/:id} : get the "id" orderLine.
     *
     * @param id the id of the orderLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-lines/{id}")
    public ResponseEntity<OrderLineDTO> getOrderLine(@PathVariable Long id) {
        log.debug("REST request to get OrderLine : {}", id);
        Optional<OrderLineDTO> orderLineDTO = orderLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderLineDTO);
    }

    /**
     * {@code DELETE  /order-lines/:id} : delete the "id" orderLine.
     *
     * @param id the id of the orderLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-lines/{id}")
    public ResponseEntity<Void> deleteOrderLine(@PathVariable Long id) {
        log.debug("REST request to delete OrderLine : {}", id);
        orderLineService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
