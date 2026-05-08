package com.tunisales.business.domain.enumeration;

/**
 * Sub-step 2.5 — physical state of a returned item.
 *
 * <ul>
 *   <li>{@link #INTACT} — eligible to go back to sellable stock.</li>
 *   <li>{@link #DAMAGED} — must go to the rework pipeline before being resold.</li>
 * </ul>
 */
public enum ItemCondition {
    INTACT,
    DAMAGED,
}
