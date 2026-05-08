package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tunisales.business.client.PlatformPerformanceScoreClient;
import com.tunisales.business.domain.Mission;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.enumeration.MissionStatus;
import com.tunisales.business.repository.MissionRepository;
import com.tunisales.business.repository.OrderLineRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for sub-step 2.12 — {@link CommercialScoreCalculator}.
 */
class CommercialScoreCalculatorTest {

    private OrderLineRepository orderLineRepository;
    private MissionRepository missionRepository;
    private PlatformPerformanceScoreClient scoreClient;
    private CommercialScoreCalculator calculator;

    @BeforeEach
    void setUp() {
        orderLineRepository = mock(OrderLineRepository.class);
        missionRepository = mock(MissionRepository.class);
        scoreClient = mock(PlatformPerformanceScoreClient.class);
        calculator = new CommercialScoreCalculator(orderLineRepository, missionRepository, scoreClient);
        ReflectionTestUtils.setField(calculator, "monthlyTarget", new BigDecimal("100000"));
    }

    private static OrderLine orderLine(String login, String lineTotal, ZonedDateTime createdAt) {
        OrderLine l = new OrderLine();
        l.setVendeurLogin(login);
        l.setLineTotal(new BigDecimal(lineTotal));
        l.setCreatedAt(createdAt);
        return l;
    }

    private static Mission mission(String login, MissionStatus status, ZonedDateTime when) {
        Mission m = new Mission();
        m.setAssignedToLogin(login);
        m.setStatus(status);
        m.setMissionDate(when);
        return m;
    }

    @Test
    void scoreIsZeroForUnknownLogin() {
        when(orderLineRepository.findAll()).thenReturn(Collections.emptyList());
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());

        BigDecimal score = calculator.computeFor("ghost", YearMonth.of(2026, 5));

        assertThat(score).isEqualByComparingTo("0.0000");
    }

    @Test
    void scoreCombinesSalesAndMissionsWeights() {
        ZonedDateTime within = ZonedDateTime.of(2026, 5, 10, 10, 0, 0, 0, ZoneId.systemDefault());

        // Sales = 50 000 / target 100 000 = 0.5 normalized.
        OrderLine line = orderLine("alice", "50000", within);
        when(orderLineRepository.findAll()).thenReturn(Arrays.asList(line));

        // 2 missions, 1 completed → ratio 0.5.
        Mission m1 = mission("alice", MissionStatus.COMPLETED, within);
        Mission m2 = mission("alice", MissionStatus.PLANNED, within);
        when(missionRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        BigDecimal score = calculator.computeFor("alice", YearMonth.of(2026, 5));

        // 0.5 * 0.5 + 0.3 * 0.5 + 0.2 * 0.5 = 0.5
        assertThat(score).isEqualByComparingTo("0.5000");
    }

    @Test
    void salesAreCappedAtTarget() {
        ZonedDateTime within = ZonedDateTime.of(2026, 5, 10, 10, 0, 0, 0, ZoneId.systemDefault());

        // Way above target — normalized clamped to 1.
        OrderLine line = orderLine("alice", "999999", within);
        when(orderLineRepository.findAll()).thenReturn(Arrays.asList(line));
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());

        BigDecimal score = calculator.computeFor("alice", YearMonth.of(2026, 5));

        // 0.5 * 1 + 0.3 * 0 + 0.2 * 1 = 0.7
        assertThat(score).isEqualByComparingTo("0.7000");
    }

    @Test
    void recomputeIteratesAllLoginsAndPushesScores() {
        ZonedDateTime within = ZonedDateTime.of(2026, 5, 10, 10, 0, 0, 0, ZoneId.systemDefault());
        when(orderLineRepository.findAll()).thenReturn(Arrays.asList(orderLine("alice", "10000", within)));
        when(missionRepository.findAll()).thenReturn(Arrays.asList(mission("bob", MissionStatus.COMPLETED, within)));

        calculator.recompute(YearMonth.of(2026, 5));

        verify(scoreClient, atLeastOnce()).postScore(eq("alice"), any(BigDecimal.class), anyString());
        verify(scoreClient, atLeastOnce()).postScore(eq("bob"), any(BigDecimal.class), anyString());
    }

    @Test
    void ignoresLinesOutsidePeriod() {
        ZonedDateTime priorMonth = ZonedDateTime.of(2026, 4, 10, 10, 0, 0, 0, ZoneId.systemDefault());
        OrderLine line = orderLine("alice", "100000", priorMonth);
        when(orderLineRepository.findAll()).thenReturn(Arrays.asList(line));
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());

        BigDecimal score = calculator.computeFor("alice", YearMonth.of(2026, 5));

        assertThat(score).isEqualByComparingTo("0.0000");
    }
}
