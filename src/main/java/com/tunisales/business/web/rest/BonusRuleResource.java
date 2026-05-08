package com.tunisales.business.web.rest;

import com.tunisales.business.service.BonusRuleService;
import com.tunisales.business.service.dto.BonusRuleDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/** Sub-step 2.9 — REST controller for bonus rules. */
@RestController
@RequestMapping("/api")
public class BonusRuleResource {

    private final Logger log = LoggerFactory.getLogger(BonusRuleResource.class);

    private final BonusRuleService bonusRuleService;

    public BonusRuleResource(BonusRuleService bonusRuleService) {
        this.bonusRuleService = bonusRuleService;
    }

    @PostMapping("/bonus-rules")
    public ResponseEntity<BonusRuleDTO> createBonusRule(@Valid @RequestBody BonusRuleDTO dto) throws URISyntaxException {
        log.debug("REST request to save BonusRule: {}", dto);
        BonusRuleDTO result = bonusRuleService.save(dto);
        return ResponseEntity.created(new URI("/api/bonus-rules/" + result.getId())).body(result);
    }

    @PutMapping("/bonus-rules/{id}")
    public ResponseEntity<BonusRuleDTO> updateBonusRule(@PathVariable Long id, @Valid @RequestBody BonusRuleDTO dto) {
        log.debug("REST request to update BonusRule {}: {}", id, dto);
        dto.setId(id);
        return ResponseEntity.ok(bonusRuleService.update(dto));
    }

    @GetMapping("/bonus-rules")
    public ResponseEntity<List<BonusRuleDTO>> getAllBonusRules(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Page<BonusRuleDTO> page = bonusRuleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/bonus-rules/{id}")
    public ResponseEntity<BonusRuleDTO> getBonusRule(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(bonusRuleService.findOne(id));
    }

    @DeleteMapping("/bonus-rules/{id}")
    public ResponseEntity<Void> deleteBonusRule(@PathVariable Long id) {
        bonusRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
