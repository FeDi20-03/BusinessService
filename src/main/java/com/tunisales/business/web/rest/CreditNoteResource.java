package com.tunisales.business.web.rest;

import com.tunisales.business.service.CreditNoteService;
import com.tunisales.business.service.dto.CreditNoteDTO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/** Sub-step 2.6 — REST controller for the credit-note (avoir) workflow. */
@RestController
@RequestMapping("/api")
public class CreditNoteResource {

    private final Logger log = LoggerFactory.getLogger(CreditNoteResource.class);

    private final CreditNoteService creditNoteService;

    public CreditNoteResource(CreditNoteService creditNoteService) {
        this.creditNoteService = creditNoteService;
    }

    @PostMapping("/credit-notes")
    public ResponseEntity<CreditNoteDTO> create(@Valid @RequestBody CreditNoteDTO dto) {
        log.debug("REST request to create CreditNote for client {}", dto.getClientId());
        return ResponseEntity.status(201).body(creditNoteService.create(dto));
    }

    @PostMapping("/credit-notes/{id}/validate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<CreditNoteDTO> validate(@PathVariable Long id) {
        log.debug("REST request to validate CreditNote {}", id);
        return ResponseEntity.ok(creditNoteService.validate(id));
    }

    @PostMapping("/credit-notes/{id}/apply")
    public ResponseEntity<CreditNoteDTO> apply(@PathVariable Long id, @Valid @RequestBody ApplyRequest request) {
        log.debug("REST request to apply CreditNote {} on order {}", id, request.orderId);
        return ResponseEntity.ok(creditNoteService.apply(id, request.orderId));
    }

    @GetMapping("/credit-notes/{id}")
    public ResponseEntity<CreditNoteDTO> get(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(creditNoteService.findOne(id));
    }

    public static class ApplyRequest {

        @NotNull
        public Long orderId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
    }
}
