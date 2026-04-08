package com.tunisales.business.web.rest;

import com.tunisales.business.repository.OrderLineItemRepository;
import com.tunisales.business.service.OrderLineItemService;
import com.tunisales.business.service.dto.OrderLineItemDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.business.domain.OrderLineItem}.
 */
@RestController
@RequestMapping("/api")
public class OrderLineItemResource {

    private final Logger log = LoggerFactory.getLogger(OrderLineItemResource.class);

    private static final String ENTITY_NAME = "businessServiceOrderLineItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderLineItemService orderLineItemService;

    private final OrderLineItemRepository orderLineItemRepository;

    public OrderLineItemResource(OrderLineItemService orderLineItemService, OrderLineItemRepository orderLineItemRepository) {
        this.orderLineItemService = orderLineItemService;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    /**
     * {@code POST  /order-line-items} : Create a new orderLineItem.
     *
     * @param orderLineItemDTO the orderLineItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderLineItemDTO, or with status {@code 400 (Bad Request)} if the orderLineItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-line-items")
    public ResponseEntity<OrderLineItemDTO> createOrderLineItem(@Valid @RequestBody OrderLineItemDTO orderLineItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save OrderLineItem : {}", orderLineItemDTO);
        if (orderLineItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderLineItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderLineItemDTO result = orderLineItemService.save(orderLineItemDTO);
        return ResponseEntity
            .created(new URI("/api/order-line-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-line-items/:id} : Updates an existing orderLineItem.
     *
     * @param id the id of the orderLineItemDTO to save.
     * @param orderLineItemDTO the orderLineItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderLineItemDTO,
     * or with status {@code 400 (Bad Request)} if the orderLineItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderLineItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-line-items/{id}")
    public ResponseEntity<OrderLineItemDTO> updateOrderLineItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderLineItemDTO orderLineItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderLineItem : {}, {}", id, orderLineItemDTO);
        if (orderLineItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderLineItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderLineItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderLineItemDTO result = orderLineItemService.update(orderLineItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderLineItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-line-items/:id} : Partial updates given fields of an existing orderLineItem, field will ignore if it is null
     *
     * @param id the id of the orderLineItemDTO to save.
     * @param orderLineItemDTO the orderLineItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderLineItemDTO,
     * or with status {@code 400 (Bad Request)} if the orderLineItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderLineItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderLineItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-line-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderLineItemDTO> partialUpdateOrderLineItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderLineItemDTO orderLineItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderLineItem partially : {}, {}", id, orderLineItemDTO);
        if (orderLineItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderLineItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderLineItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderLineItemDTO> result = orderLineItemService.partialUpdate(orderLineItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderLineItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-line-items} : get all the orderLineItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderLineItems in body.
     */
    @GetMapping("/order-line-items")
    public List<OrderLineItemDTO> getAllOrderLineItems() {
        log.debug("REST request to get all OrderLineItems");
        return orderLineItemService.findAll();
    }

    /**
     * {@code GET  /order-line-items/:id} : get the "id" orderLineItem.
     *
     * @param id the id of the orderLineItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderLineItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-line-items/{id}")
    public ResponseEntity<OrderLineItemDTO> getOrderLineItem(@PathVariable Long id) {
        log.debug("REST request to get OrderLineItem : {}", id);
        Optional<OrderLineItemDTO> orderLineItemDTO = orderLineItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderLineItemDTO);
    }

    /**
     * {@code DELETE  /order-line-items/:id} : delete the "id" orderLineItem.
     *
     * @param id the id of the orderLineItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-line-items/{id}")
    public ResponseEntity<Void> deleteOrderLineItem(@PathVariable Long id) {
        log.debug("REST request to delete OrderLineItem : {}", id);
        orderLineItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
