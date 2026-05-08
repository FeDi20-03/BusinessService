package com.tunisales.business.service;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.enumeration.ClientGrade;
import com.tunisales.business.domain.enumeration.PaymentMethod;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sub-step 2.2 — Payment eligibility assessment.
 *
 * <p>Decides whether an order can move from DRAFT to SUBMITTED, given its
 * payment method and the client's credit posture. Only the {@link PaymentMethod#CHECK}
 * path enforces credit/grade rules; cash, card, and transfer are always eligible.
 *
 * <p>Rules for CHECK:
 * <ul>
 *   <li>{@code creditUsed + totalAmount <= creditLimit} — base ceiling</li>
 *   <li>{@code grade != null} — a graded client only</li>
 *   <li>Grade C — additional cap at 50% of {@code creditLimit}</li>
 *   <li>Grades A and B — full {@code creditLimit} available</li>
 * </ul>
 */
@Service
public class PaymentEligibilityService {

    private static final BigDecimal HALF = new BigDecimal("0.5");

    private final Logger log = LoggerFactory.getLogger(PaymentEligibilityService.class);

    /**
     * Result of an eligibility assessment.
     *
     * <p>When {@code eligible} is true, {@code reason} is {@code null}.
     * When {@code eligible} is false, {@code reason} carries a human-readable
     * message suitable for surfacing to the caller (UI/API).
     */
    public static final class EligibilityResult {

        private final boolean eligible;
        private final String reason;

        private EligibilityResult(boolean eligible, String reason) {
            this.eligible = eligible;
            this.reason = reason;
        }

        public static EligibilityResult ok() {
            return new EligibilityResult(true, null);
        }

        public static EligibilityResult ko(String reason) {
            return new EligibilityResult(false, reason);
        }

        public boolean isEligible() {
            return eligible;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Assess whether the given order is eligible for submission.
     *
     * @param order the order to evaluate; must have a non-null client and totalAmount.
     * @return an {@link EligibilityResult} with the verdict and (if rejected) the reason.
     */
    public EligibilityResult assess(Order order) {
        log.debug("Assessing payment eligibility for Order : {}", order == null ? null : order.getId());

        // Non-CHECK payments bypass credit checks.
        if (order == null || order.getPaymentMethod() != PaymentMethod.CHECK) {
            return EligibilityResult.ok();
        }

        Client client = order.getClient();
        BigDecimal totalAmount = nullSafe(order.getTotalAmount());
        BigDecimal creditUsed = client == null ? BigDecimal.ZERO : nullSafe(client.getCreditUsed());
        BigDecimal creditLimit = client == null ? BigDecimal.ZERO : nullSafe(client.getCreditLimit());

        BigDecimal projectedUsed = creditUsed.add(totalAmount);

        // Base credit-limit ceiling.
        if (projectedUsed.compareTo(creditLimit) > 0) {
            return EligibilityResult.ko("Plafond de crédit dépassé");
        }

        // Grade is mandatory for check payments.
        ClientGrade grade = client == null ? null : client.getGrade();
        if (grade == null) {
            return EligibilityResult.ko("Grade client non défini");
        }

        // Grade C: capped at 50% of creditLimit.
        if (grade == ClientGrade.C) {
            BigDecimal halfLimit = creditLimit.multiply(HALF);
            if (projectedUsed.compareTo(halfLimit) > 0) {
                return EligibilityResult.ko("Grade C : limité à 50% du plafond de crédit");
            }
        }

        // Grades A and B: no extra constraint beyond the base ceiling already verified.
        return EligibilityResult.ok();
    }

    private static BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
