package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tunisales.business.client.PlatformClientScoreClient;
import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Invoice;
import com.tunisales.business.domain.enumeration.InvoiceStatus;
import com.tunisales.business.repository.ClientRepository;
import com.tunisales.business.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for sub-step 2.11 — {@link ClientLoyaltyCalculator}.
 */
class ClientLoyaltyCalculatorTest {

    private ClientRepository clientRepository;
    private InvoiceRepository invoiceRepository;
    private PlatformClientScoreClient scoreClient;
    private ClientLoyaltyCalculator calculator;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        invoiceRepository = mock(InvoiceRepository.class);
        scoreClient = mock(PlatformClientScoreClient.class);
        calculator = new ClientLoyaltyCalculator(clientRepository, invoiceRepository, scoreClient);
    }

    private static Client client(Long id, ZonedDateTime createdAt) {
        Client c = new Client();
        c.setId(id);
        c.setCreatedAt(createdAt);
        return c;
    }

    private static Invoice invoice(Client owner, String amount, ZonedDateTime when) {
        Invoice i = new Invoice();
        i.setClient(owner);
        i.setAmountTtc(new BigDecimal(amount));
        i.setStatus(InvoiceStatus.ISSUED);
        i.setIssueDate(when);
        return i;
    }

    @Test
    void returnsZeroForClientWithoutInvoices() {
        Client c = client(1L, ZonedDateTime.now().minusYears(2));
        when(invoiceRepository.findAll()).thenReturn(Collections.emptyList());

        BigDecimal score = calculator.computeFor(c);

        assertThat(score).isEqualByComparingTo("0");
    }

    @Test
    void computesPositiveScoreForActiveClient() {
        Client c = client(1L, ZonedDateTime.now().minusYears(2));
        Invoice i1 = invoice(c, "1000", ZonedDateTime.now().minusMonths(6));
        Invoice i2 = invoice(c, "500", ZonedDateTime.now().minusMonths(3));
        when(invoiceRepository.findAll()).thenReturn(Arrays.asList(i1, i2));

        BigDecimal score = calculator.computeFor(c);

        assertThat(score.signum()).isEqualTo(1);
    }

    @Test
    void scheduledRunIteratesAllClientsAndPushesScores() {
        Client c1 = client(1L, ZonedDateTime.now().minusYears(2));
        Client c2 = client(2L, ZonedDateTime.now().minusYears(1));
        when(clientRepository.findAll()).thenReturn(Arrays.asList(c1, c2));
        when(invoiceRepository.findAll()).thenReturn(Collections.emptyList());

        calculator.computeAndPushAll();

        verify(scoreClient, atLeastOnce()).postScore(any(), any(BigDecimal.class));
    }

    @Test
    void ignoresClientWithNullId() {
        Client c = new Client();
        BigDecimal score = calculator.computeFor(c);
        assertThat(score).isEqualByComparingTo("0");
    }
}
