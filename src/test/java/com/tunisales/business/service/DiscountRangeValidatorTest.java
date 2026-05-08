package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.Product;
import com.tunisales.business.web.rest.errors.DiscountOutOfRangeException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for sub-step 2.3 — {@link DiscountRangeValidator}.
 */
class DiscountRangeValidatorTest {

    private DiscountRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DiscountRangeValidator();
    }

    private static Product product(String min, String max) {
        Product p = new Product();
        p.setSku("SKU-X");
        p.setMinDiscountPct(min == null ? null : new BigDecimal(min));
        p.setMaxDiscountPct(max == null ? null : new BigDecimal(max));
        return p;
    }

    private static OrderLine line(String applied) {
        OrderLine l = new OrderLine();
        l.setAppliedDiscountPct(applied == null ? null : new BigDecimal(applied));
        return l;
    }

    @Test
    void noopWhenLineOrProductIsNull() {
        validator.validate(null, product("0", "10"));
        validator.validate(line("5"), null);
    }

    @Test
    void acceptsDiscountWithinRange() {
        validator.validate(line("5"), product("0", "10"));
        validator.validate(line("0"), product("0", "10"));
        validator.validate(line("10"), product("0", "10"));
    }

    @Test
    void rejectsDiscountAboveMax() {
        assertThatThrownBy(() -> validator.validate(line("15"), product("0", "10"))).isInstanceOf(DiscountOutOfRangeException.class);
    }

    @Test
    void rejectsDiscountBelowMin() {
        assertThatThrownBy(() -> validator.validate(line("1"), product("5", "10"))).isInstanceOf(DiscountOutOfRangeException.class);
    }

    @Test
    void treatsNullsAsZero() {
        // Applied=null => 0; min=null => 0, max=null => 0 — applied (0) within [0, 0], OK.
        validator.validate(line(null), product(null, null));

        // Applied = 0 vs min=0/max=0 — boundary case, OK.
        validator.validate(line("0"), product(null, null));

        // Applied = 5 vs min=null/max=null (treated as 0) — out of range.
        assertThatThrownBy(() -> validator.validate(line("5"), product(null, null))).isInstanceOf(DiscountOutOfRangeException.class);
    }

    @Test
    void rejectsAtBoundaryWithStrictRange() {
        // Min=5, max=5 — only 5 is allowed.
        validator.validate(line("5"), product("5", "5"));
        assertThatThrownBy(() -> validator.validate(line("4"), product("5", "5"))).isInstanceOf(DiscountOutOfRangeException.class);
    }

    @Test
    void exceptionMessageMentionsSkuAndRange() {
        try {
            validator.validate(line("50"), product("0", "10"));
        } catch (DiscountOutOfRangeException ex) {
            assertThat(ex.getMessage()).contains("SKU-X").contains("50").contains("0").contains("10");
        }
    }
}
