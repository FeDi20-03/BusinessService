package com.tunisales.business.service;

import com.tunisales.business.repository.InvoiceRepository;
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
 * current calendar year. Counters live in memory but are <em>seeded from the
 * database</em> on the first call for a given (prefix, year): the invoice
 * family is initialised from {@link InvoiceRepository#findMaxNumberWithPrefix}
 * so that the sequence resumes where it left off after an application restart,
 * instead of restarting at 1 and colliding with already-persisted numbers
 * (unique constraint {@code ux_invoice__invoice_number}).</p>
 *
 * <p>The {@code NNNNN} sequence is left-padded to 5 digits but expands
 * naturally past 99 999 (e.g. {@code INV-2026-100000}). Note that database
 * seeding relies on {@code max(invoiceNumber)} which is a lexicographic
 * comparison — correct while the running counter stays within 5 digits; past
 * 99 999 in a single year a dedicated DB sequence would be required.</p>
 *
 * <p>Only the invoice family ({@code INV}) is DB-backed today, as it is the
 * only one persisted with a unique number constraint reached by the workflow.
 * The {@code RET}/{@code AVO} families still start at 0 per JVM run.</p>
 */
@Service
public class SequenceService {

    private static final String INVOICE_PREFIX = "INV";
    private static final String RETURN_PREFIX = "RET";
    private static final String CREDIT_NOTE_PREFIX = "AVO";

    private final Logger log = LoggerFactory.getLogger(SequenceService.class);

    /** Per-(prefix,year) counter. Key is "{prefix}-{year}". */
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    private final InvoiceRepository invoiceRepository;

    public SequenceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

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
        AtomicLong counter = counters.computeIfAbsent(key, k -> new AtomicLong(seedFor(prefix, year)));
        long value = counter.incrementAndGet();
        String number = String.format("%s-%d-%05d", prefix, year, value);
        log.debug("Generated sequence number: {}", number);
        return number;
    }

    /**
     * Initial counter value for a (prefix, year): the highest sequence already persisted
     * in the database so the next {@code incrementAndGet()} resumes after it. Returns 0
     * when nothing exists yet (or for families that are not DB-backed).
     */
    private long seedFor(String prefix, int year) {
        if (!INVOICE_PREFIX.equals(prefix)) {
            return 0L;
        }
        String max = invoiceRepository.findMaxNumberWithPrefix(prefix + "-" + year + "-");
        long seed = parseSequence(max);
        if (seed > 0) {
            log.info("Seeding {} sequence for {} from existing max '{}' -> {}", prefix, year, max, seed);
        }
        return seed;
    }

    /**
     * Extracts the trailing numeric sequence from a document number such as
     * {@code INV-2026-00007}. Returns 0 for {@code null}, blank or unparseable input.
     */
    private long parseSequence(String number) {
        if (number == null) {
            return 0L;
        }
        int lastDash = number.lastIndexOf('-');
        if (lastDash < 0 || lastDash == number.length() - 1) {
            return 0L;
        }
        try {
            return Long.parseLong(number.substring(lastDash + 1).trim());
        } catch (NumberFormatException e) {
            log.warn("Could not parse sequence from document number '{}', seeding from 0", number);
            return 0L;
        }
    }
}
