package com.tunisales.business.domain.enumeration;

/**
 * The ClientGrade enumeration.
 *
 * <p>Used by {@code PaymentEligibilityService} (sub-step 2.2) to apply
 * differentiated credit rules when paying by check:
 * <ul>
 *   <li>Grade A — full creditLimit available</li>
 *   <li>Grade B — full creditLimit available</li>
 *   <li>Grade C — only 50% of creditLimit available</li>
 * </ul>
 * A {@code null} grade blocks any check payment.
 */
public enum ClientGrade {
    A,
    B,
    C,
}
