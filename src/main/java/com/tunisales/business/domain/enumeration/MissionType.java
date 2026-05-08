package com.tunisales.business.domain.enumeration;

/**
 * Sub-step 2.7 — kind of mission a commercial can be assigned.
 *
 * <ul>
 *   <li>{@link #VENTE} — direct sales visit (default).</li>
 *   <li>{@link #PROPOSITION} — proposal-only visit (no order).</li>
 *   <li>{@link #MARKETING} — marketing-driven visit (campaign, promo).</li>
 *   <li>{@link #VERIFICATION_STOCK} — stock verification at the client site.</li>
 * </ul>
 */
public enum MissionType {
    VENTE,
    PROPOSITION,
    MARKETING,
    VERIFICATION_STOCK,
}
