package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for sub-step 2.4 — {@link SequenceService}.
 *
 * <p>Pure unit test (no Spring). Verifies format, monotonicity within a family,
 * independence across families, and thread-safety under contention.</p>
 */
class SequenceServiceTest {

    private SequenceService service;

    @BeforeEach
    void setUp() {
        service = new SequenceService();
    }

    @Test
    void invoiceNumbersAreFormattedAndMonotonic() {
        String first = service.nextInvoiceNumber();
        String second = service.nextInvoiceNumber();
        String third = service.nextInvoiceNumber();
        int year = Year.now().getValue();

        assertThat(first).isEqualTo(String.format("INV-%d-00001", year));
        assertThat(second).isEqualTo(String.format("INV-%d-00002", year));
        assertThat(third).isEqualTo(String.format("INV-%d-00003", year));
    }

    @Test
    void returnAndCreditNoteUseDistinctPrefixes() {
        String inv = service.nextInvoiceNumber();
        String ret = service.nextReturnNumber();
        String avo = service.nextCreditNoteNumber();

        assertThat(inv).startsWith("INV-");
        assertThat(ret).startsWith("RET-");
        assertThat(avo).startsWith("AVO-");
    }

    @Test
    void countersAreIndependentAcrossFamilies() {
        // Take three invoices then ask for a return — return should still start at 1.
        service.nextInvoiceNumber();
        service.nextInvoiceNumber();
        service.nextInvoiceNumber();
        String firstReturn = service.nextReturnNumber();
        int year = Year.now().getValue();
        assertThat(firstReturn).isEqualTo(String.format("RET-%d-00001", year));
    }

    @Test
    void concurrentCallsProduceUniqueNumbers() throws Exception {
        int threads = 16;
        int callsPerThread = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        Set<String> seen = java.util.Collections.synchronizedSet(new HashSet<>());
        try {
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < callsPerThread; i++) {
                        seen.add(service.nextInvoiceNumber());
                    }
                });
            }
            pool.shutdown();
            assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        } finally {
            pool.shutdownNow();
        }
        assertThat(seen).hasSize(threads * callsPerThread);
    }
}
