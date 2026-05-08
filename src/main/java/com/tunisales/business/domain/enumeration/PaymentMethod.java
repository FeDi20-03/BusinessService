package com.tunisales.business.domain.enumeration;

/**
 * The PaymentMethod enumeration.
 *
 * <p>Represents how an Order will be paid. Used by sub-step 2.2
 * ({@code PaymentEligibilityService}) to gate the {@code submit()} transition:
 * orders paid by {@link #CHECK} must satisfy credit-limit and grade rules
 * before they can move from DRAFT to SUBMITTED.
 */
public enum PaymentMethod {
    CASH,
    CHECK,
    CARD,
    TRANSFER,
}
