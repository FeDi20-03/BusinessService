package com.tunisales.business.service;

import java.time.Year;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sub-step 2.4 — thread-safe generator for human-readable document numbers.
 *
 * <p>Three families are supported:
 * <ul>
 *   <li>{@code INV-YYYY-NNNNN} — invoice numbers (sub-step 2.4)</li>
 *   <li>{@code RET-YYYY-NNNNN} — sales return numbers (sub-step 2.5)</li>
 *   <li>{@code AVO-YYYY-NNNNN} — credit note numbers (sub-step 2.6)</li>
 * </ul>
 *
 * <p>Each family keeps its own {@link AtomicLong} counter, scoped to the
 * current calendar year. Counters are kept in memory and reset on the
 * first call of a new year. For production-grade durability the counters
 * should be backed by a dedicated database sequence; this in-memory variant
 * is sufficient for the workflow tests and avoids a roundtrip per call.</p>
 *
 * <p>The {@code NNNNN} sequence is left-padded to 5 digits but expands
 * naturally past 99 999 (e.g. {@code INV-2026-100000}).</p>
 */
@Service
public class SequenceService {

    private static final String INVOICE_PREFIX = "INV";
    private static final String RETURN_PREFIX = "RET";
    private static final String CREDIT_NOTE_PREFIX = "AVO";

    private final Logger log = LoggerFactory.getLogger(SequenceService.class);

    /** Per-(prefix,year) counter. Key is "{prefix}-{year}". */
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    /** Generate the next invoice number, e.g. {@code INV-2026-00001}. */
    public synchronized String nextInvoiceNumber() {
        return next(INVOICE_PREFIX);
    }

    /** Generate the next sales-return number, e.g. {@code RET-2026-00001}. */
    public synchronized String nextReturnNumber() {
        return next(RETURN_PREFIX);
    }

    /** Generate the next credit-note number, e.g. {@code AVO-2026-00001}. */
    public synchronized String nextCreditNoteNumber() {
        return next(CREDIT_NOTE_PREFIX);
    }

    private String next(String prefix) {
        int year = Year.now().getValue();
        String key = prefix + "-" + year;
        AtomicLong counter = counters.computeIfAbsent(key, k -> new AtomicLong(0L));
        long value = counter.incrementAndGet();
        String number = String.format("%s-%d-%05d", prefix, year, value);
        log.debug("Generated sequence number: {}", number);
        return number;
    }
}
