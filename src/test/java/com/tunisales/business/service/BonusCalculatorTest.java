package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tunisales.business.domain.BonusEntry;
import com.tunisales.business.domain.BonusRule;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.Product;
import com.tunisales.business.repository.BonusEntryRepository;
import com.tunisales.business.repository.BonusRuleRepository;
import com.tunisales.business.repository.OrderLineRepository;
import com.tunisales.business.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit tests for sub-step 2.9 — {@link BonusCalculator}.
 *
 * <p>Mocks the JPA repositories to drive {@link BonusCalculator#recordSale(Long)}
 * and {@link BonusCalculator#computeForVendeur(String, YearMonth)} along their
 * main branches.</p>
 */
class BonusCalculatorTest {

    private OrderRepository orderRepository;
    private OrderLineRepository orderLineRepository;
    private BonusRuleRepository bonusRuleRepository;
    private BonusEntryRepository bonusEntryRepository;
    private BonusCalculator calculator;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderLineRepository = mock(OrderLineRepository.class);
        bonusRuleRepository = mock(BonusRuleRepository.class);
        bonusEntryRepository = mock(BonusEntryRepository.class);
        calculator = new BonusCalculator(orderRepository, orderLineRepository, bonusRuleRepository, bonusEntryRepository);
    }

    private static OrderLine line(Long id, String vendeurLogin, Long productId) {
        OrderLine l = new OrderLine();
        l.setId(id);
        l.setVendeurLogin(vendeurLogin);
        if (productId != null) {
            Product p = new Product();
            p.setId(productId);
            l.setProduct(p);
        }
        return l;
    }

    private static BonusRule rule(Long id, Long productId, Long zoneId, String amount) {
        BonusRule r = new BonusRule();
        r.setId(id);
        r.setProductId(productId);
        r.setZoneId(zoneId);
        r.setAmount(new BigDecimal(amount));
        r.setIsActive(Boolean.TRUE);
        return r;
    }

    @Test
    void recordSaleUsesProductSpecificRuleWhenAvailable() {
        OrderLine line = line(10L, "alice", 100L);
        when(orderLineRepository.findById(10L)).thenReturn(Optional.of(line));

        BonusRule global = rule(1L, null, null, "5");
        BonusRule productRule = rule(2L, 100L, null, "20");
        when(bonusRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(global, productRule));

        calculator.recordSale(10L);

        ArgumentCaptor<BonusEntry> captor = ArgumentCaptor.forClass(BonusEntry.class);
        verify(bonusEntryRepository, times(1)).save(captor.capture());
        BonusEntry saved = captor.getValue();
        assertThat(saved.getVendeurLogin()).isEqualTo("alice");
        assertThat(saved.getOrderLineId()).isEqualTo(10L);
        assertThat(saved.getBonusRuleId()).isEqualTo(2L);
        assertThat(saved.getAmount()).isEqualByComparingTo("20");
    }

    @Test
    void recordSaleFallsBackToGlobalRuleWhenNoSpecificMatch() {
        OrderLine line = line(11L, "bob", 999L);
        when(orderLineRepository.findById(11L)).thenReturn(Optional.of(line));

        BonusRule global = rule(1L, null, null, "5");
        when(bonusRuleRepository.findByIsActiveTrue()).thenReturn(Collections.singletonList(global));

        calculator.recordSale(11L);

        ArgumentCaptor<BonusEntry> captor = ArgumentCaptor.forClass(BonusEntry.class);
        verify(bonusEntryRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("5");
        assertThat(captor.getValue().getBonusRuleId()).isEqualTo(1L);
    }

    @Test
    void recordSaleSkipsLinesWithoutSeller() {
        OrderLine line = line(12L, null, 100L);
        when(orderLineRepository.findById(12L)).thenReturn(Optional.of(line));

        calculator.recordSale(12L);

        verify(bonusEntryRepository, times(0)).save(any());
    }

    @Test
    void recordSaleStoresZeroAmountWhenNoRuleMatches() {
        OrderLine line = line(13L, "carol", 100L);
        when(orderLineRepository.findById(13L)).thenReturn(Optional.of(line));
        when(bonusRuleRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

        calculator.recordSale(13L);

        ArgumentCaptor<BonusEntry> captor = ArgumentCaptor.forClass(BonusEntry.class);
        verify(bonusEntryRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("0");
        assertThat(captor.getValue().getBonusRuleId()).isNull();
    }

    @Test
    void computeForVendeurSumsAmountsInPeriod() {
        BonusEntry e1 = new BonusEntry();
        e1.setAmount(new BigDecimal("10"));
        BonusEntry e2 = new BonusEntry();
        e2.setAmount(new BigDecimal("15.50"));
        when(bonusEntryRepository.findForVendeurAndPeriod(any(), any(ZonedDateTime.class), any(ZonedDateTime.class)))
            .thenReturn(Arrays.asList(e1, e2));

        BigDecimal total = calculator.computeForVendeur("alice", YearMonth.of(2026, 5));

        assertThat(total).isEqualByComparingTo("25.50");
    }

    @Test
    void computeForVendeurReturnsZeroWhenNoEntries() {
        when(bonusEntryRepository.findForVendeurAndPeriod(any(), any(ZonedDateTime.class), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());
        BigDecimal total = calculator.computeForVendeur("noone", YearMonth.of(2026, 5));
        assertThat(total).isEqualByComparingTo("0");
    }
}
