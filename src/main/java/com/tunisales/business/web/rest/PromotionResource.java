package com.tunisales.business.web.rest;

import com.tunisales.business.service.PromotionService;
import com.tunisales.business.service.dto.PromotionDTO;
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

/** Sub-step 2.9 — REST controller for promotions. */
@RestController
@RequestMapping("/api")
public class PromotionResource {

    private final Logger log = LoggerFactory.getLogger(PromotionResource.class);

    private final PromotionService promotionService;

    public PromotionResource(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/promotions")
    public ResponseEntity<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO dto) throws URISyntaxException {
        log.debug("REST request to save Promotion: {}", dto);
        PromotionDTO result = promotionService.save(dto);
        return ResponseEntity.created(new URI("/api/promotions/" + result.getId())).body(result);
    }

    @PutMapping("/promotions/{id}")
    public ResponseEntity<PromotionDTO> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDTO dto) {
        log.debug("REST request to update Promotion {}: {}", id, dto);
        dto.setId(id);
        return ResponseEntity.ok(promotionService.update(dto));
    }

    @GetMapping("/promotions")
    public ResponseEntity<List<PromotionDTO>> getAllPromotions(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Page<PromotionDTO> page = promotionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/promotions/{id}")
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(promotionService.findOne(id));
    }

    @DeleteMapping("/promotions/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
