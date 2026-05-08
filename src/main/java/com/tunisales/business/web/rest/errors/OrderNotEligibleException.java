package com.tunisales.business.web.rest.errors;

/**
 * Thrown when an order cannot be submitted because it fails the
 * eligibility checks of {@code PaymentEligibilityService} (sub-step 2.2).
 *
 * <p>Returns HTTP 400 Bad Request via Zalando Problem (inherits the
 * mapping behaviour of {@link BadRequestAlertException}). The provided
 * message is the human-readable reason produced by the eligibility
 * assessment (e.g. "Plafond de crédit dépassé").
 */
@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class OrderNotEligibleException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    private static final String ENTITY_NAME = "order";
    private static final String ERROR_KEY = "ordernoteligible";

    public OrderNotEligibleException(String reason) {
        super(reason, ENTITY_NAME, ERROR_KEY);
    }
}
