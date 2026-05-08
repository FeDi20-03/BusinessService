package com.tunisales.business.service;

import com.tunisales.business.client.PlatformPerformanceScoreClient;
import com.tunisales.business.domain.Mission;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.enumeration.MissionStatus;
import com.tunisales.business.repository.MissionRepository;
import com.tunisales.business.repository.OrderLineRepository;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sub-step 2.12 — periodic computation of a commercial's performance score.
 *
 * <p>Formula:
 * <pre>
 *   score = 0.5 * poidsVentes_normalisé
 *         + 0.3 * respectMissions
 *         + 0.2 * tauxMensuel
 * </pre>
 *
 * <p>{@code poidsVentes_normalisé} = sum of {@code lineTotal} of order lines credited
 * to the seller in the period, divided by {@code commercial-monthly-target}, capped at 1.<br/>
 * {@code respectMissions} = ratio of {@link MissionStatus#COMPLETED} to total missions
 * scheduled in the period for that seller (0 when no missions).<br/>
 * {@code tauxMensuel} = same as {@code poidsVentes_normalisé} for now (placeholder for
 * a future month-over-month growth metric).</p>
 *
 * <p>Scheduled by default on the first of each month at 03:00; overridable via
 * {@code tunisales.business.commercial-score-cron}.</p>
 */
@Service
public class CommercialScoreCalculator {

    private final Logger log = LoggerFactory.getLogger(CommercialScoreCalculator.class);

    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final OrderLineRepository orderLineRepository;
    private final MissionRepository missionRepository;
    private final PlatformPerformanceScoreClient platformPerformanceScoreClient;

    @Value("${tunisales.business.commercial-monthly-target:100000}")
    private BigDecimal monthlyTarget;

    public CommercialScoreCalculator(
        OrderLineRepository orderLineRepository,
        MissionRepository missionRepository,
        PlatformPerformanceScoreClient platformPerformanceScoreClient
    ) {
        this.orderLineRepository = orderLineRepository;
        this.missionRepository = missionRepository;
        this.platformPerformanceScoreClient = platformPerformanceScoreClient;
    }

    /** Run on the first day of each month at 03:00 by default. */
    @Scheduled(cron = "${tunisales.business.commercial-score-cron:0 0 3 1 * *}")
    @Transactional(readOnly = true)
    public void computeAndPushAll() {
        YearMonth period = YearMonth.now().minusMonths(1);
        log.info("Starting commercial performance score computation for period {}", period);
        recompute(period);
    }

    /**
     * Compute and publish performance scores for all known commercial logins
     * (extracted from {@link OrderLine#getVendeurLogin()} and {@link Mission#getAssignedToLogin()}
     * within the period).
     */
    @Transactional(readOnly = true)
    public void recompute(YearMonth period) {
        Set<String> logins = new HashSet<>();
        for (OrderLine line : orderLineRepository.findAll()) {
            if (line.getVendeurLogin() != null) logins.add(line.getVendeurLogin());
        }
        for (Mission mission : missionRepository.findAll()) {
            if (mission.getAssignedToLogin() != null) logins.add(mission.getAssignedToLogin());
        }

        for (String login : logins) {
            try {
                BigDecimal score = computeFor(login, period);
                platformPerformanceScoreClient.postScore(login, score, period.format(PERIOD_FORMAT));
            } catch (RuntimeException ex) {
                log.warn("Commercial score failed for login {}: {}", login, ex.getMessage());
            }
        }
    }

    /**
     * Compute the performance score for a single seller during the given calendar month.
     */
    public BigDecimal computeFor(String login, YearMonth period) {
        if (login == null) return BigDecimal.ZERO;
        ZonedDateTime start = period.atDay(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = period.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault());

        // Sales weight in the period.
        BigDecimal salesAmount = orderLineRepository
            .findAll()
            .stream()
            .filter(line -> login.equals(line.getVendeurLogin()))
            .filter(line -> {
                ZonedDateTime created = line.getCreatedAt();
                return created != null && !created.isBefore(start) && created.isBefore(end);
            })
            .map(line -> line.getLineTotal() == null ? BigDecimal.ZERO : line.getLineTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal target = (monthlyTarget == null || monthlyTarget.signum() <= 0) ? BigDecimal.valueOf(100_000) : monthlyTarget;
        double salesNormalized = Math.min(1.0, salesAmount.doubleValue() / target.doubleValue());

        // Mission completion ratio in the period.
        List<Mission> missions = missionRepository
            .findAll()
            .stream()
            .filter(m -> login.equals(m.getAssignedToLogin()))
            .filter(m -> {
                ZonedDateTime when = m.getMissionDate();
                return when != null && !when.isBefore(start) && when.isBefore(end);
            })
            .collect(java.util.stream.Collectors.toList());
        double missionRatio;
        if (missions.isEmpty()) {
            missionRatio = 0.0;
        } else {
            long completed = missions.stream().filter(m -> m.getStatus() == MissionStatus.COMPLETED).count();
            missionRatio = ((double) completed) / missions.size();
        }

        // Monthly rate — placeholder reusing salesNormalized; replace with month-over-month growth later.
        double monthlyRate = salesNormalized;

        double score = 0.5 * salesNormalized + 0.3 * missionRatio + 0.2 * monthlyRate;
        return new BigDecimal(score, MathContext.DECIMAL64).setScale(4, RoundingMode.HALF_UP);
    }
}
