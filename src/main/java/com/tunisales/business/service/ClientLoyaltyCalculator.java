package com.tunisales.business.service;

import com.tunisales.business.client.PlatformClientScoreClient;
import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Invoice;
import com.tunisales.business.domain.enumeration.InvoiceStatus;
import com.tunisales.business.repository.ClientRepository;
import com.tunisales.business.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sub-step 2.11 — periodic computation of a client's loyalty score.
 *
 * <p>Formula (per client):
 * <pre>
 *   score = (totalAchats / nbMois) * log(1 + nbAchats) * ancienneté(années)
 * </pre>
 *
 * <p>{@code totalAchats} = sum of {@code amountTtc} of paid/issued invoices,
 * {@code nbAchats} = number of those invoices,
 * {@code nbMois} = months between the first invoice and now (clamped to 1),
 * {@code ancienneté} = years since the client's {@code createdAt} (clamped to 1).</p>
 *
 * <p>Scheduled by default on the first of each month at 02:00; the cron expression
 * is overridable via {@code tunisales.business.loyalty-cron}.</p>
 */
@Service
public class ClientLoyaltyCalculator {

    private final Logger log = LoggerFactory.getLogger(ClientLoyaltyCalculator.class);

    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final PlatformClientScoreClient platformClientScoreClient;

    public ClientLoyaltyCalculator(
        ClientRepository clientRepository,
        InvoiceRepository invoiceRepository,
        PlatformClientScoreClient platformClientScoreClient
    ) {
        this.clientRepository = clientRepository;
        this.invoiceRepository = invoiceRepository;
        this.platformClientScoreClient = platformClientScoreClient;
    }

    /** Run on the first day of each month at 02:00 by default. */
    @Scheduled(cron = "${tunisales.business.loyalty-cron:0 0 2 1 * *}")
    @Transactional(readOnly = true)
    public void computeAndPushAll() {
        log.info("Starting loyalty score computation for all clients");
        for (Client client : clientRepository.findAll()) {
            try {
                BigDecimal score = computeFor(client);
                platformClientScoreClient.postScore(client.getId(), score);
            } catch (RuntimeException ex) {
                log.warn("Loyalty score failed for client {}: {}", client.getId(), ex.getMessage());
            }
        }
        log.info("Loyalty score computation finished");
    }

    /**
     * Compute the loyalty score for a single client. Returns {@link BigDecimal#ZERO}
     * if the client has no qualifying invoices.
     */
    public BigDecimal computeFor(Client client) {
        if (client == null || client.getId() == null) return BigDecimal.ZERO;

        List<Invoice> invoices = invoiceRepository
            .findAll()
            .stream()
            .filter(inv -> inv.getClient() != null && client.getId().equals(inv.getClient().getId()))
            .filter(inv -> inv.getStatus() == InvoiceStatus.ISSUED || inv.getStatus() == InvoiceStatus.PAID)
            .collect(java.util.stream.Collectors.toList());

        int nbAchats = invoices.size();
        if (nbAchats == 0) return BigDecimal.ZERO;

        BigDecimal totalAchats = invoices
            .stream()
            .map(inv -> inv.getAmountTtc() == null ? BigDecimal.ZERO : inv.getAmountTtc())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime firstInvoiceAt = invoices
            .stream()
            .map(Invoice::getIssueDate)
            .filter(java.util.Objects::nonNull)
            .min(ZonedDateTime::compareTo)
            .orElse(now);
        long nbMois = Math.max(1L, ChronoUnit.MONTHS.between(firstInvoiceAt, now));

        ZonedDateTime createdAt = client.getCreatedAt() == null ? now : client.getCreatedAt();
        long ancienneteAnnees = Math.max(1L, ChronoUnit.YEARS.between(createdAt, now));

        double avgPerMonth = totalAchats.doubleValue() / nbMois;
        double logFactor = Math.log(1.0 + nbAchats);
        double score = avgPerMonth * logFactor * ancienneteAnnees;

        return new BigDecimal(score, MathContext.DECIMAL64).setScale(4, RoundingMode.HALF_UP);
    }
}
