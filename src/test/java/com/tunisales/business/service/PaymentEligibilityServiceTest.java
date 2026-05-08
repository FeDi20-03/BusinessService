package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.enumeration.ClientGrade;
import com.tunisales.business.domain.enumeration.PaymentMethod;
import com.tunisales.business.service.PaymentEligibilityService.EligibilityResult;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for sub-step 2.2 — {@link PaymentEligibilityService}.
 *
 * <p>Pure unit test (no Spring context). Covers every branch of the
 * eligibility decision tree documented in the service's javadoc.
 */
class PaymentEligibilityServiceTest {

    private PaymentEligibilityService service;

    @BeforeEach
    void setUp() {
        service = new PaymentEligibilityService();
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private static Client client(BigDecimal creditLimit, BigDecimal creditUsed, ClientGrade grade) {
        Client c = new Client();
        c.setCreditLimit(creditLimit);
        c.setCreditUsed(creditUsed);
        c.setGrade(grade);
        return c;
    }

    private static Order order(PaymentMethod method, BigDecimal totalAmount, Client client) {
        Order o = new Order();
        o.setPaymentMethod(method);
        o.setTotalAmount(totalAmount);
        o.setClient(client);
        return o;
    }

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    // ---------------------------------------------------------------------
    // Non-CHECK methods always pass.
    // ---------------------------------------------------------------------

    @Test
    void cashPaymentIsAlwaysEligible() {
        Client c = client(bd("100"), bd("90"), null); // even with no grade and saturated credit
        Order o = order(PaymentMethod.CASH, bd("9999"), c);

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    void cardPaymentIsAlwaysEligible() {
        Order o = order(PaymentMethod.CARD, bd("500"), client(bd("100"), bd("100"), null));
        assertThat(service.assess(o).isEligible()).isTrue();
    }

    @Test
    void transferPaymentIsAlwaysEligible() {
        Order o = order(PaymentMethod.TRANSFER, bd("500"), client(bd("100"), bd("100"), null));
        assertThat(service.assess(o).isEligible()).isTrue();
    }

    // ---------------------------------------------------------------------
    // CHECK + grade null → not eligible
    // ---------------------------------------------------------------------

    @Test
    void checkWithNullGradeIsNotEligible() {
        // Within credit limit but grade is null.
        Client c = client(bd("1000"), bd("0"), null);
        Order o = order(PaymentMethod.CHECK, bd("100"), c);

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).isEqualTo("Grade client non défini");
    }

    // ---------------------------------------------------------------------
    // CHECK + credit limit exceeded → not eligible (regardless of grade)
    // ---------------------------------------------------------------------

    @Test
    void checkExceedingCreditLimitIsNotEligibleEvenWithGradeA() {
        Client c = client(bd("1000"), bd("500"), ClientGrade.A);
        Order o = order(PaymentMethod.CHECK, bd("600"), c); // 500 + 600 = 1100 > 1000

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).isEqualTo("Plafond de crédit dépassé");
    }

    // ---------------------------------------------------------------------
    // CHECK + grade A within limit → eligible
    // ---------------------------------------------------------------------

    @Test
    void checkWithGradeAWithinLimitIsEligible() {
        Client c = client(bd("1000"), bd("200"), ClientGrade.A);
        Order o = order(PaymentMethod.CHECK, bd("700"), c); // 200 + 700 = 900 <= 1000

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    void checkWithGradeAExactlyAtLimitIsEligible() {
        Client c = client(bd("1000"), bd("400"), ClientGrade.A);
        Order o = order(PaymentMethod.CHECK, bd("600"), c); // 400 + 600 = 1000

        assertThat(service.assess(o).isEligible()).isTrue();
    }

    // ---------------------------------------------------------------------
    // CHECK + grade B within limit → eligible
    // ---------------------------------------------------------------------

    @Test
    void checkWithGradeBWithinLimitIsEligible() {
        Client c = client(bd("1000"), bd("100"), ClientGrade.B);
        Order o = order(PaymentMethod.CHECK, bd("800"), c); // 100 + 800 = 900 <= 1000

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    void checkWithGradeBAboveLimitIsNotEligible() {
        Client c = client(bd("1000"), bd("100"), ClientGrade.B);
        Order o = order(PaymentMethod.CHECK, bd("950"), c); // 100 + 950 = 1050 > 1000

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).isEqualTo("Plafond de crédit dépassé");
    }

    // ---------------------------------------------------------------------
    // CHECK + grade C within 50% → eligible
    // CHECK + grade C between 50% and 100% → not eligible (Grade C cap)
    // CHECK + grade C above 100% → not eligible (base ceiling)
    // ---------------------------------------------------------------------

    @Test
    void checkWithGradeCWithinHalfLimitIsEligible() {
        Client c = client(bd("1000"), bd("100"), ClientGrade.C);
        Order o = order(PaymentMethod.CHECK, bd("300"), c); // 100 + 300 = 400 <= 500 (50% of 1000)

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    void checkWithGradeCExactlyAtHalfLimitIsEligible() {
        Client c = client(bd("1000"), bd("0"), ClientGrade.C);
        Order o = order(PaymentMethod.CHECK, bd("500"), c); // 0 + 500 = 500 == 50% of 1000

        assertThat(service.assess(o).isEligible()).isTrue();
    }

    @Test
    void checkWithGradeCBetweenHalfAndFullLimitIsNotEligible() {
        Client c = client(bd("1000"), bd("100"), ClientGrade.C);
        Order o = order(PaymentMethod.CHECK, bd("500"), c); // 100 + 500 = 600 > 500 (half) but <= 1000 (full)

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).isEqualTo("Grade C : limité à 50% du plafond de crédit");
    }

    @Test
    void checkWithGradeCAboveFullLimitIsNotEligibleByBaseRule() {
        Client c = client(bd("1000"), bd("100"), ClientGrade.C);
        Order o = order(PaymentMethod.CHECK, bd("950"), c); // 100 + 950 = 1050 > 1000 (full)

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isFalse();
        // Base rule fires before the Grade-C cap because we check the ceiling first.
        assertThat(result.getReason()).isEqualTo("Plafond de crédit dépassé");
    }

    // ---------------------------------------------------------------------
    // Defensive: null inputs treated as zero on numeric fields.
    // ---------------------------------------------------------------------

    @Test
    void checkWithNullCreditFieldsAndNoGradeIsNotEligible() {
        // creditLimit null -> 0; creditUsed null -> 0; totalAmount = 1 -> 0+1 > 0 → exceeds limit
        Client c = client(null, null, ClientGrade.A);
        Order o = order(PaymentMethod.CHECK, bd("1"), c);

        EligibilityResult result = service.assess(o);

        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).isEqualTo("Plafond de crédit dépassé");
    }
}
