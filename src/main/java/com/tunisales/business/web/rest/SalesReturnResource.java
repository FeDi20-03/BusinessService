package com.tunisales.business.web.rest;

import com.tunisales.business.domain.enumeration.ItemCondition;
import com.tunisales.business.service.SalesReturnService;
import com.tunisales.business.service.dto.SalesReturnDTO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Sub-step 2.5 — REST controller for the SalesReturn workflow.
 *
 * <p>Exposes the create/scan/approve cycle. The CRUD on individual lines is
 * intentionally not exposed: lines are only ever added via {@code /scan-line}
 * so that the inventory side-effects always run together with the persistence.</p>
 */
@RestController
@RequestMapping("/api")
public class SalesReturnResource {

    private final Logger log = LoggerFactory.getLogger(SalesReturnResource.class);

    private final SalesReturnService salesReturnService;

    public SalesReturnResource(SalesReturnService salesReturnService) {
        this.salesReturnService = salesReturnService;
    }

    @PostMapping("/sales-returns")
    public ResponseEntity<SalesReturnDTO> create(@Valid @RequestBody CreateRequest request) {
        log.debug("REST request to create SalesReturn for invoice {}", request.invoiceId);
        SalesReturnDTO result = salesReturnService.create(request.invoiceId, request.reason);
        return ResponseEntity.status(201).body(result);
    }

    @PostMapping("/sales-returns/{id}/scan-line")
    public ResponseEntity<SalesReturnDTO> scanLine(@PathVariable Long id, @Valid @RequestBody ScanLineRequest request) {
        log.debug("REST request to scan line on SalesReturn {}", id);
        SalesReturnDTO result = salesReturnService.scanLine(id, request.imei, request.invoiceLineId, request.condition);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sales-returns/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<SalesReturnDTO> approve(@PathVariable Long id) {
        log.debug("REST request to approve SalesReturn {}", id);
        SalesReturnDTO result = salesReturnService.approve(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sales-returns/{id}")
    public ResponseEntity<SalesReturnDTO> get(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(salesReturnService.findOne(id));
    }

    public static class CreateRequest {

        @NotNull
        public Long invoiceId;

        @Size(max = 1000)
        public String reason;

        public Long getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(Long invoiceId) {
            this.invoiceId = invoiceId;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class ScanLineRequest {

        @NotNull
        @Size(max = 50)
        public String imei;

        @NotNull
        public Long invoiceLineId;

        public ItemCondition condition;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public Long getInvoiceLineId() {
            return invoiceLineId;
        }

        public void setInvoiceLineId(Long invoiceLineId) {
            this.invoiceLineId = invoiceLineId;
        }

        public ItemCondition getCondition() {
            return condition;
        }

        public void setCondition(ItemCondition condition) {
            this.condition = condition;
        }
    }
}
