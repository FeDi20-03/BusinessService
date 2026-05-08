package com.tunisales.business.service;

import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.Product;
import com.tunisales.business.web.rest.errors.DiscountOutOfRangeException;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sub-step 2.3 — validates that an {@link OrderLine#getAppliedDiscountPct()} value
 * falls within the range declared on the related {@link Product#getMinDiscountPct()}
 * and {@link Product#getMaxDiscountPct()}.
 *
 * <p>Called from {@code OrderService.submit(...)} for every line of the order.
 * Throws {@link DiscountOutOfRangeException} (HTTP 400) when a violation is detected.</p>
 *
 * <p>Null-safe: a missing applied discount is treated as 0, and missing min/max
 * defaults to 0 (i.e. no discount allowed by default).</p>
 */
@Service
public class DiscountRangeValidator {

    private final Logger log = LoggerFactory.getLogger(DiscountRangeValidator.class);

    /**
     * Validate the discount on a single line against its product's range.
     *
     * @param line the order line carrying the {@code appliedDiscountPct}.
     * @param product the product whose {@code minDiscountPct/maxDiscountPct} bound the allowed range.
     * @throws DiscountOutOfRangeException 400 when the value is outside [min, max].
     */
    public void validate(OrderLine line, Product product) {
        if (line == null || product == null) {
            return;
        }
        BigDecimal applied = nullSafe(line.getAppliedDiscountPct());
        BigDecimal min = nullSafe(product.getMinDiscountPct());
        BigDecimal max = nullSafe(product.getMaxDiscountPct());

        if (applied.compareTo(min) < 0 || applied.compareTo(max) > 0) {
            String msg = String.format(
                "Remise hors fourchette : %s%% sur le produit %s (autorisé %s%%–%s%%)",
                applied,
                product.getSku(),
                min,
                max
            );
            log.warn(
                "Discount out of range: line={}, product={}, applied={}, min={}, max={}",
                line.getId(),
                product.getId(),
                applied,
                min,
                max
            );
            throw new DiscountOutOfRangeException(msg);
        }
    }

    private static BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
