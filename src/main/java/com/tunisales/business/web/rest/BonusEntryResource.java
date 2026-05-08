package com.tunisales.business.web.rest;

import com.tunisales.business.repository.BonusEntryRepository;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.dto.BonusEntryDTO;
import com.tunisales.business.service.mapper.BonusEntryMapper;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sub-step 2.9 — REST controller for bonus entries.
 *
 * <p>Exposes {@code GET /bonus-entries/me?period=YYYY-MM} which lists the
 * bonus entries of the current login for the given calendar month.</p>
 */
@RestController
@RequestMapping("/api")
public class BonusEntryResource {

    private final Logger log = LoggerFactory.getLogger(BonusEntryResource.class);

    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final BonusEntryRepository bonusEntryRepository;
    private final BonusEntryMapper bonusEntryMapper;

    public BonusEntryResource(BonusEntryRepository bonusEntryRepository, BonusEntryMapper bonusEntryMapper) {
        this.bonusEntryRepository = bonusEntryRepository;
        this.bonusEntryMapper = bonusEntryMapper;
    }

    @GetMapping("/bonus-entries/me")
    public ResponseEntity<List<BonusEntryDTO>> getMyEntries(@RequestParam(name = "period", required = false) String periodStr) {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null) return ResponseEntity.ok(Collections.emptyList());
        YearMonth period = (periodStr == null || periodStr.isBlank()) ? YearMonth.now() : YearMonth.parse(periodStr, PERIOD_FORMAT);
        ZonedDateTime start = period.atDay(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = period.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault());
        log.debug("REST request to get my bonus entries (login={}, period={})", login, period);
        return ResponseEntity.ok(bonusEntryMapper.toDto(bonusEntryRepository.findForVendeurAndPeriod(login, start, end)));
    }
}
