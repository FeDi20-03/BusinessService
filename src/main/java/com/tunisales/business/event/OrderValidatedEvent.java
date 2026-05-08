package com.tunisales.business.event;

/**
 * Sub-step 2.9 — published by {@code OrderService.validate(...)} after an
 * order moves to {@code VALIDATED} and an invoice has been issued.
 *
 * <p>Listeners include {@code BonusCalculator} which records a {@code BonusEntry}
 * per line for the seller credited on the order.</p>
 */
public class OrderValidatedEvent {

    private final Long orderId;
    private final Long invoiceId;

    public OrderValidatedEvent(Long orderId, Long invoiceId) {
        this.orderId = orderId;
        this.invoiceId = invoiceId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    @Override
    public String toString() {
        return "OrderValidatedEvent{orderId=" + orderId + ", invoiceId=" + invoiceId + "}";
    }
}
