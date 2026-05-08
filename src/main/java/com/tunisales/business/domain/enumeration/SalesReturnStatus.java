package com.tunisales.business.domain.enumeration;

/**
 * Sub-step 2.5 — lifecycle of a sales return.
 *
 * <pre>
 *   DRAFT -> SUBMITTED -> APPROVED -> REFUNDED
 *                     \-> REJECTED
 * </pre>
 */
public enum SalesReturnStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    REFUNDED,
}
