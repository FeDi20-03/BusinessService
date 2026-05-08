package com.tunisales.business.domain.enumeration;

/**
 * Sub-step 2.6 — lifecycle of a credit note (avoir commercial).
 *
 * <pre>
 *   DRAFT -> ISSUED -> APPLIED
 *                  \-> CANCELLED
 * </pre>
 */
public enum CreditNoteStatus {
    DRAFT,
    ISSUED,
    APPLIED,
    CANCELLED,
}
