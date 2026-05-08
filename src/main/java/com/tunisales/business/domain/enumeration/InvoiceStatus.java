package com.tunisales.business.domain.enumeration;

/**
 * The InvoiceStatus enumeration.
 *
 * <p>Sub-step 2.4 added {@link #ISSUED} for invoices freshly generated when
 * the order is validated, prior to payment.</p>
 */
public enum InvoiceStatus {
    ISSUED,
    PAID,
    NOT_PAID,
}
