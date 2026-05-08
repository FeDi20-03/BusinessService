package com.tunisales.business.web.rest;

import com.tunisales.business.service.CommercialScoreCalculator;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sub-step 2.12 — REST controller exposing manual recompute of commercial scores.
 */
@RestController
@RequestMapping("/api")
public class CommercialScoreResource {

    private final Logger log = LoggerFactory.getLogger(CommercialScoreResource.class);

    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CommercialScoreCalculator commercialScoreCalculator;

    public CommercialScoreResource(CommercialScoreCalculator commercialScoreCalculator) {
        this.commercialScoreCalculator = commercialScoreCalculator;
    }

    /**
     * Manually trigger recomputation of every commercial's score for the given period.
     * Restricted to commercial admins or system admins.
     */
    @PostMapping("/commercial-scores/recompute")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_COMMERCIAL','ROLE_ADMIN_SYSTEME')")
    public ResponseEntity<Void> recompute(@RequestParam("period") String periodStr) {
        YearMonth period = YearMonth.parse(periodStr, PERIOD_FORMAT);
        log.info("Manual recompute of commercial scores for period {}", period);
        commercialScoreCalculator.recompute(period);
        return ResponseEntity.accepted().build();
    }
}
