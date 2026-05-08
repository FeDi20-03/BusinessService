package com.tunisales.business.web.rest;

import com.tunisales.business.repository.OrderRepository;
import com.tunisales.business.service.OrderQueryService;
import com.tunisales.business.service.OrderService;
import com.tunisales.business.service.criteria.OrderCriteria;
import com.tunisales.business.service.dto.OrderDTO;
import com.tunisales.business.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.business.domain.Order}.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "businessServiceOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    private final OrderQueryService orderQueryService;

    public OrderResource(OrderService orderService, OrderRepository orderRepository, OrderQueryService orderQueryService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderQueryService = orderQueryService;
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param orderDTO the orderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderDTO, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException {
        log.debug("REST request to save Order : {}", orderDTO);
        if (orderDTO.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderDTO result = orderService.save(orderDTO);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing order.
     *
     * @param id the id of the orderDTO to save.
     * @param orderDTO the orderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderDTO,
     * or with status {@code 400 (Bad Request)} if the orderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderDTO orderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Order : {}, {}", id, orderDTO);
        if (orderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderDTO result = orderService.update(orderDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing order, field will ignore if it is null
     *
     * @param id the id of the orderDTO to save.
     * @param orderDTO the orderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderDTO,
     * or with status {@code 400 (Bad Request)} if the orderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderDTO> partialUpdateOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderDTO orderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Order partially : {}, {}", id, orderDTO);
        if (orderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderDTO> result = orderService.partialUpdate(orderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders(
        OrderCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Orders by criteria: {}", criteria);
        Page<OrderDTO> page = orderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/count} : count all the orders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/orders/count")
    public ResponseEntity<Long> countOrders(OrderCriteria criteria) {
        log.debug("REST request to count Orders by criteria: {}", criteria);
        return ResponseEntity.ok().body(orderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the orderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Optional<OrderDTO> orderDTO = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderDTO);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the orderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // ------------------------------------------------------------------------
    // Workflow endpoints (sub-step 2.1 — NEGOTIATING)
    // ------------------------------------------------------------------------

    /**
     * {@code POST  /orders/:id/submit} : submit a DRAFT order (commercial action).
     *
     * @param id the order id.
     * @return the updated order DTO.
     */
    @PostMapping("/orders/{id}/submit")
    public ResponseEntity<OrderDTO> submitOrder(@PathVariable Long id) {
        log.debug("REST request to submit Order : {}", id);
        OrderDTO result = orderService.submit(id);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST  /orders/:id/validate} : validate a SUBMITTED order (admin only).
     *
     * @param id the order id.
     * @param request body containing {@code adminLogin} (optional — falls back to Spring Security principal).
     * @param authentication the current authentication (for audit).
     * @return the updated order DTO.
     */
    @PostMapping("/orders/{id}/validate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_COMMERCIAL','ROLE_ADMIN_SYSTEME')")
    public ResponseEntity<OrderDTO> validateOrder(
        @PathVariable Long id,
        @RequestBody(required = false) ValidateRequest request,
        Authentication authentication
    ) {
        log.debug("REST request to validate Order : {}", id);
        String adminLogin = (request != null && request.getAdminLogin() != null)
            ? request.getAdminLogin()
            : (authentication != null ? authentication.getName() : null);
        OrderDTO result = orderService.validate(id, adminLogin);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST  /orders/:id/negotiate} : move a SUBMITTED order to NEGOTIATING (admin only).
     *
     * @param id the order id.
     * @param request body containing the negotiation {@code reason}.
     * @return the updated order DTO.
     */
    @PostMapping("/orders/{id}/negotiate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_COMMERCIAL','ROLE_ADMIN_SYSTEME')")
    public ResponseEntity<OrderDTO> negotiateOrder(@PathVariable Long id, @Valid @RequestBody ReasonRequest request) {
        log.debug("REST request to negotiate Order : {}", id);
        OrderDTO result = orderService.negotiate(id, request.getReason());
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST  /orders/:id/reject} : reject a SUBMITTED order (admin only).
     *
     * @param id the order id.
     * @param request body containing the rejection {@code reason}.
     * @return the updated order DTO.
     */
    @PostMapping("/orders/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_COMMERCIAL','ROLE_ADMIN_SYSTEME')")
    public ResponseEntity<OrderDTO> rejectOrder(@PathVariable Long id, @Valid @RequestBody ReasonRequest request) {
        log.debug("REST request to reject Order : {}", id);
        OrderDTO result = orderService.reject(id, request.getReason());
        return ResponseEntity.ok().body(result);
    }

    // ----- Workflow request bodies -----

    /** Request body for {@code POST /orders/:id/validate}. */
    public static class ValidateRequest {

        @Size(max = 50)
        private String adminLogin;

        public String getAdminLogin() {
            return adminLogin;
        }

        public void setAdminLogin(String adminLogin) {
            this.adminLogin = adminLogin;
        }
    }

    /** Request body for {@code POST /orders/:id/negotiate} and {@code POST /orders/:id/reject}. */
    public static class ReasonRequest {

        @NotNull
        @Size(min = 1, max = 500)
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
