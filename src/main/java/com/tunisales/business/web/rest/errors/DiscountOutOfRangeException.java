package com.tunisales.business.web.rest.errors;

/**
 * Sub-step 2.3 — thrown when an {@code OrderLine.appliedDiscountPct}
 * falls outside the {@code [Product.minDiscountPct, Product.maxDiscountPct]} range.
 *
 * <p>Returns HTTP 400 Bad Request via Zalando Problem (inherits the mapping
 * behaviour of {@link BadRequestAlertException}).</p>
 */
@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class DiscountOutOfRangeException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    private static final String ENTITY_NAME = "orderLine";
    private static final String ERROR_KEY = "discountoutofrange";

    public DiscountOutOfRangeException(String message) {
        super(message, ENTITY_NAME, ERROR_KEY);
    }
}
