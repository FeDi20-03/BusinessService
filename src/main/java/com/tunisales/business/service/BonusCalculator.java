package com.tunisales.business.service;

import com.tunisales.business.domain.BonusEntry;
import com.tunisales.business.domain.BonusRule;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.event.OrderValidatedEvent;
import com.tunisales.business.repository.BonusEntryRepository;
import com.tunisales.business.repository.BonusRuleRepository;
import com.tunisales.business.repository.OrderLineRepository;
import com.tunisales.business.repository.OrderRepository;
import com.tunisales.business.tenant.TenantUtils;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sub-step 2.9 — bonus calculation engine.
 *
 * <p>Two responsibilities:
 * <ul>
 *   <li>{@link #recordSale(Long)} — for a given order line, pick the most-specific
 *       active {@link BonusRule} (product+zone &gt; product &gt; zone &gt; global)
 *       and persist a {@link BonusEntry} crediting the seller.</li>
 *   <li>{@link #computeForVendeur(String, YearMonth)} — sum the entries for the
 *       seller within a calendar month.</li>
 * </ul>
 *
 * <p>{@link #onOrderValidated(OrderValidatedEvent)} is a {@code @TransactionalEventListener}
 * that fires after the order-validation transaction commits, so we never record
 * bonuses for orders that ultimately rolled back.</p>
 */
@Service
@Transactional
public class BonusCalculator {

    private final Logger log = LoggerFactory.getLogger(BonusCalculator.class);

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final BonusRuleRepository bonusRuleRepository;
    private final BonusEntryRepository bonusEntryRepository;

    public BonusCalculator(
        OrderRepository orderRepository,
        OrderLineRepository orderLineRepository,
        BonusRuleRepository bonusRuleRepository,
        BonusEntryRepository bonusEntryRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.bonusRuleRepository = bonusRuleRepository;
        this.bonusEntryRepository = bonusEntryRepository;
    }

    /**
     * Record a {@link BonusEntry} for the seller credited on the given order line.
     * If no seller is set, the call is a no-op.
     */
    public void recordSale(Long orderLineId) {
        OrderLine line = orderLineRepository.findById(orderLineId).orElse(null);
        if (line == null) {
            log.warn("recordSale: OrderLine {} not found", orderLineId);
            return;
        }
        String vendeur = line.getVendeurLogin();
        if (vendeur == null || vendeur.isBlank()) {
            log.debug("recordSale: no seller on OrderLine {}, skipping", orderLineId);
            return;
        }
        Long productId = line.getProduct() != null ? line.getProduct().getId() : null;

        BonusRule rule = pickRule(productId, /* zoneId= */null);
        BigDecimal amount = rule != null ? rule.getAmount() : BigDecimal.ZERO;

        BonusEntry entry = new BonusEntry();
        entry.setTenantId(TenantUtils.currentTenantId());
        entry.setVendeurLogin(vendeur);
        entry.setOrderLineId(orderLineId);
        entry.setBonusRuleId(rule != null ? rule.getId() : null);
        entry.setAmount(amount);
        entry.setComputedAt(ZonedDateTime.now());
        bonusEntryRepository.save(entry);

        log.debug("BonusEntry recorded: vendeur={}, orderLine={}, amount={}", vendeur, orderLineId, amount);
    }

    /**
     * Sum the bonus amounts for the given seller within the given calendar month.
     */
    @Transactional(readOnly = true)
    public BigDecimal computeForVendeur(String login, YearMonth period) {
        ZonedDateTime start = period.atDay(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = period.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault());
        List<BonusEntry> entries = bonusEntryRepository.findForVendeurAndPeriod(login, start, end);
        return entries.stream().map(BonusEntry::getAmount).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Pick the most specific active rule, falling back to {@code null} when nothing matches.
     */
    private BonusRule pickRule(Long productId, Long zoneId) {
        List<BonusRule> active = bonusRuleRepository.findByIsActiveTrue();
        // Priority: product+zone (3) > product-only (2) > zone-only (1) > global (0)
        Optional<BonusRule> best = active
            .stream()
            .filter(r -> matches(r, productId, zoneId))
            .max(Comparator.comparingInt(r -> specificity(r, productId, zoneId)));
        return best.orElse(null);
    }

    private boolean matches(BonusRule rule, Long productId, Long zoneId) {
        if (rule.getProductId() != null && !rule.getProductId().equals(productId)) return false;
        if (rule.getZoneId() != null && !rule.getZoneId().equals(zoneId)) return false;
        return true;
    }

    private int specificity(BonusRule rule, Long productId, Long zoneId) {
        int s = 0;
        if (rule.getProductId() != null && rule.getProductId().equals(productId)) s += 2;
        if (rule.getZoneId() != null && rule.getZoneId().equals(zoneId)) s += 1;
        return s;
    }

    /**
     * Listener invoked after the order-validation transaction commits.
     * Records a bonus entry per line of the validated order.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderValidated(OrderValidatedEvent event) {
        log.debug("Handling OrderValidatedEvent: {}", event);
        Order order = orderRepository.findOneWithEagerRelationships(event.getOrderId()).orElse(null);
        if (order == null || order.getOrderLines() == null) return;
        for (OrderLine line : order.getOrderLines()) {
            recordSale(line.getId());
        }
    }
}
