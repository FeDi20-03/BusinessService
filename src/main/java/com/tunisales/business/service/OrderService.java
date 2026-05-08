package com.tunisales.business.service;

import com.tunisales.business.domain.Invoice;
import com.tunisales.business.domain.InvoiceLine;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.enumeration.InvoiceStatus;
import com.tunisales.business.domain.enumeration.OrderStatus;
import com.tunisales.business.event.OrderValidatedEvent;
import com.tunisales.business.repository.InvoiceLineRepository;
import com.tunisales.business.repository.InvoiceRepository;
import com.tunisales.business.repository.OrderRepository;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.PaymentEligibilityService.EligibilityResult;
import com.tunisales.business.service.dto.OrderDTO;
import com.tunisales.business.service.mapper.OrderMapper;
import com.tunisales.business.tenant.TenantUtils;
import com.tunisales.business.web.rest.errors.OrderNotEligibleException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final PaymentEligibilityService paymentEligibilityService;

    private final DiscountRangeValidator discountRangeValidator;

    private final SequenceService sequenceService;

    private final InvoiceRepository invoiceRepository;

    private final InvoiceLineRepository invoiceLineRepository;

    private final ApplicationEventPublisher eventPublisher;

    public OrderService(
        OrderRepository orderRepository,
        OrderMapper orderMapper,
        PaymentEligibilityService paymentEligibilityService,
        DiscountRangeValidator discountRangeValidator,
        SequenceService sequenceService,
        InvoiceRepository invoiceRepository,
        InvoiceLineRepository invoiceLineRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentEligibilityService = paymentEligibilityService;
        this.discountRangeValidator = discountRangeValidator;
        this.sequenceService = sequenceService;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        if (order.getOrderLines() != null) {
            Order finalOrder = order;
            order.getOrderLines().forEach(line -> line.setOrder(finalOrder));
        }
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Update a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderDTO update(OrderDTO orderDTO) {
        log.debug("Request to update Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Partially update a order.
     *
     * @param orderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        log.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .map(orderRepository::save)
            .map(orderMapper::toDto);
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    /**
     * Get all the orders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderRepository.findAllWithEagerRelationships(pageable).map(orderMapper::toDto);
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findOneWithEagerRelationships(id).map(orderMapper::toDto);
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    // ------------------------------------------------------------------------
    // Workflow transitions (sub-step 2.1 — NEGOTIATING)
    // ------------------------------------------------------------------------

    /**
     * Submit an order: DRAFT -> SUBMITTED.
     *
     * @param id the order id.
     * @return the updated DTO.
     * @throws ResponseStatusException 404 if not found, 409 if not in DRAFT state.
     */
    public OrderDTO submit(Long id) {
        log.debug("Request to submit Order : {}", id);
        Order order = loadOrThrow(id);
        requireStatus(order, OrderStatus.DRAFT, "submit");

        EligibilityResult eligibility = paymentEligibilityService.assess(order);
        if (!eligibility.isEligible()) {
            throw new OrderNotEligibleException(eligibility.getReason());
        }

        // Sub-step 2.3 — validate discount ranges on every line of the order.
        if (order.getOrderLines() != null) {
            for (OrderLine line : order.getOrderLines()) {
                discountRangeValidator.validate(line, line.getProduct());
            }
        }

        // Sub-step 2.9 — stamp the current commercial as the seller credited for each line.
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentLogin != null && order.getOrderLines() != null) {
            for (OrderLine line : order.getOrderLines()) {
                if (line.getVendeurLogin() == null) {
                    line.setVendeurLogin(currentLogin);
                }
            }
        }

        order.setStatus(OrderStatus.SUBMITTED);
        order.setSubmittedAt(ZonedDateTime.now());
        order.setUpdatedAt(ZonedDateTime.now());
        return orderMapper.toDto(orderRepository.save(order));
    }

    /**
     * Validate an order: SUBMITTED -> VALIDATED.
     *
     * @param id the order id.
     * @param adminLogin the login of the admin performing the validation (audit).
     * @return the updated DTO.
     * @throws ResponseStatusException 404 if not found, 409 if not in SUBMITTED state.
     */
    public OrderDTO validate(Long id, String adminLogin) {
        log.debug("Request to validate Order : {} by {}", id, adminLogin);
        Order order = loadOrThrow(id);
        requireStatus(order, OrderStatus.SUBMITTED, "validate");

        // Sub-step 2.4 — generate an Invoice (status ISSUED) with one InvoiceLine per OrderLine.
        Invoice invoice = createInvoiceFor(order);

        order.setStatus(OrderStatus.VALIDATED);
        order.setValidatedAt(ZonedDateTime.now());
        order.setUpdatedAt(ZonedDateTime.now());
        Order saved = orderRepository.save(order);

        // Sub-step 2.9 — publish OrderValidatedEvent so BonusCalculator records sales.
        eventPublisher.publishEvent(new OrderValidatedEvent(saved.getId(), invoice.getId()));

        return orderMapper.toDto(saved);
    }

    /**
     * Sub-step 2.4 — create and persist an Invoice and its InvoiceLines for the given order.
     * Numbers are produced by {@link SequenceService#nextInvoiceNumber()}, status is set to
     * {@link InvoiceStatus#ISSUED}, and {@code unitPriceAtPurchase} captures the line price
     * at validation time (immutable snapshot used downstream by SalesReturn refunds).
     */
    private Invoice createInvoiceFor(Order order) {
        ZonedDateTime now = ZonedDateTime.now();
        BigDecimal subtotal = order.getSubtotal() == null ? BigDecimal.ZERO : order.getSubtotal();
        BigDecimal taxAmount = order.getTaxAmount() == null ? BigDecimal.ZERO : order.getTaxAmount();
        BigDecimal total = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        int paymentDays = order.getPaymentTermsDays() == null ? 30 : order.getPaymentTermsDays();

        Invoice invoice = new Invoice()
            .tenantId(order.getTenantId() == null ? TenantUtils.currentTenantId() : order.getTenantId())
            .invoiceNumber(sequenceService.nextInvoiceNumber())
            .amountHt(subtotal)
            .taxAmount(taxAmount)
            .amountTtc(total)
            .status(InvoiceStatus.ISSUED)
            .issueDate(now)
            .dueDate(now.plusDays(paymentDays))
            .isDeleted(false)
            .createdAt(now)
            .client(order.getClient())
            .order(order);

        invoice = invoiceRepository.save(invoice);

        if (order.getOrderLines() != null) {
            for (OrderLine line : order.getOrderLines()) {
                InvoiceLine invoiceLine = new InvoiceLine()
                    .tenantId(invoice.getTenantId())
                    .quantity(line.getQuantity())
                    .unitPriceAtPurchase(line.getUnitPrice())
                    .lineTotal(line.getLineTotal())
                    .createdAt(now)
                    .invoice(invoice)
                    .product(line.getProduct());
                invoiceLineRepository.save(invoiceLine);
            }
        }

        log.info("Invoice {} ISSUED for order {} (amount={})", invoice.getInvoiceNumber(), order.getId(), total);
        return invoice;
    }

    /**
     * Move an order to negotiation: SUBMITTED -> NEGOTIATING.
     *
     * @param id the order id.
     * @param reason the reason for negotiation, persisted on the order.
     * @return the updated DTO.
     * @throws ResponseStatusException 404 if not found, 409 if not in SUBMITTED state.
     */
    public OrderDTO negotiate(Long id, String reason) {
        log.debug("Request to negotiate Order : {} (reason length: {})", id, reason == null ? 0 : reason.length());
        Order order = loadOrThrow(id);
        requireStatus(order, OrderStatus.SUBMITTED, "negotiate");

        order.setStatus(OrderStatus.NEGOTIATING);
        order.setNegotiationReason(reason);
        order.setUpdatedAt(ZonedDateTime.now());
        return orderMapper.toDto(orderRepository.save(order));
    }

    /**
     * Reject an order: SUBMITTED -> REJECTED.
     *
     * @param id the order id.
     * @param reason the reason for rejection, persisted on the order.
     * @return the updated DTO.
     * @throws ResponseStatusException 404 if not found, 409 if not in SUBMITTED state.
     */
    public OrderDTO reject(Long id, String reason) {
        log.debug("Request to reject Order : {} (reason length: {})", id, reason == null ? 0 : reason.length());
        Order order = loadOrThrow(id);
        requireStatus(order, OrderStatus.SUBMITTED, "reject");

        order.setStatus(OrderStatus.REJECTED);
        order.setRejectionReason(reason);
        order.setUpdatedAt(ZonedDateTime.now());
        return orderMapper.toDto(orderRepository.save(order));
    }

    /**
     * Loads the order or throws 404.
     */
    private Order loadOrThrow(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
    }

    /**
     * Idempotence guard: ensures the order is in the expected status, otherwise throws 409.
     */
    private void requireStatus(Order order, OrderStatus expected, String action) {
        if (order.getStatus() != expected) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cannot " + action + " order " + order.getId() + ": expected status " + expected + " but was " + order.getStatus()
            );
        }
    }
}
