package com.tunisales.business.domain.enumeration;

/**
 * The OrderStatus enumeration.
 *
 * <p>Order workflow:
 * <pre>
 *   DRAFT --submit()--> SUBMITTED --validate()--> VALIDATED
 *                                |--negotiate()--> NEGOTIATING
 *                                \--reject()-----> REJECTED
 * </pre>
 *
 * <p>The legacy French-language values (enAttente, valide, enCours, livre, rejete)
 * are retained for backward compatibility with existing data and faker fixtures.
 */
public enum OrderStatus {
    // Workflow values
    DRAFT,
    SUBMITTED,
    VALIDATED,
    NEGOTIATING,
    REJECTED,
    DELIVERED,

    // Legacy values
    enAttente,
    valide,
    enCours,
    livre,
    rejete,
}
