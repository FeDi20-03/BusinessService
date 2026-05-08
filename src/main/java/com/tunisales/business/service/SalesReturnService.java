package com.tunisales.business.service;

import com.tunisales.business.client.InventoryClient;
import com.tunisales.business.domain.InvoiceLine;
import com.tunisales.business.domain.SalesReturn;
import com.tunisales.business.domain.SalesReturnLine;
import com.tunisales.business.domain.enumeration.ItemCondition;
import com.tunisales.business.domain.enumeration.SalesReturnStatus;
import com.tunisales.business.repository.InvoiceLineRepository;
import com.tunisales.business.repository.SalesReturnLineRepository;
import com.tunisales.business.repository.SalesReturnRepository;
import com.tunisales.business.security.SecurityUtils;
import com.tunisales.business.service.dto.SalesReturnDTO;
import com.tunisales.business.service.dto.SalesReturnLineDTO;
import com.tunisales.business.service.mapper.SalesReturnMapper;
import com.tunisales.business.tenant.TenantUtils;
import com.tunisales.business.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Sub-step 2.5 — workflow service for sales returns (retours de vente).
 *
 * <p>Handles the lifecycle: create (DRAFT) → scan a line → approve (APPROVED → REFUNDED).
 * Idempotence is enforced on every transition via {@link #requireStatus(SalesReturn, SalesReturnStatus, String)}:
 * a second call on a return that has already moved on yields HTTP 409.</p>
 */
@Service
@Transactional
public class SalesReturnService {

    private final Logger log = LoggerFactory.getLogger(SalesReturnService.class);

    private final SalesReturnRepository salesReturnRepository;
    private final SalesReturnLineRepository salesReturnLineRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final SalesReturnMapper salesReturnMapper;
    private final SequenceService sequenceService;
    private final InventoryClient inventoryClient;

    public SalesReturnService(
        SalesReturnRepository salesReturnRepository,
        SalesReturnLineRepository salesReturnLineRepository,
        InvoiceLineRepository invoiceLineRepository,
        SalesReturnMapper salesReturnMapper,
        SequenceService sequenceService,
        InventoryClient inventoryClient
    ) {
        this.salesReturnRepository = salesReturnRepository;
        this.salesReturnLineRepository = salesReturnLineRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.salesReturnMapper = salesReturnMapper;
        this.sequenceService = sequenceService;
        this.inventoryClient = inventoryClient;
    }

    /** Create a new sales return in DRAFT state for the given invoice. */
    public SalesReturnDTO create(Long invoiceId, String reason) {
        log.debug("Creating SalesReturn for invoice {}", invoiceId);
        SalesReturn ret = new SalesReturn();
        ret.setTenantId(TenantUtils.currentTenantId());
        ret.setInvoiceId(invoiceId);
        ret.setReason(reason);
        ret.setStatus(SalesReturnStatus.DRAFT);
        ret.setReturnNumber(sequenceService.nextReturnNumber());
        ret.setCreatedByLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        ret.setCreatedAt(ZonedDateTime.now());
        ret.setRefundAmount(BigDecimal.ZERO);
        ret = salesReturnRepository.save(ret);
        return salesReturnMapper.toDto(ret);
    }

    /**
     * Scan a returned item, attach a line to the return, and (on success) capture
     * the refund amount as the {@code unitPriceAtPurchase} of the matching invoice line.
     */
    public SalesReturnDTO scanLine(Long returnId, String imei, Long invoiceLineId, ItemCondition condition) {
        log.debug("Scanning line for SalesReturn {}: imei={}, invoiceLineId={}", returnId, imei, invoiceLineId);
        SalesReturn ret = loadOrThrow(returnId);
        if (ret.getStatus() != SalesReturnStatus.DRAFT && ret.getStatus() != SalesReturnStatus.SUBMITTED) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cannot scan line on SalesReturn " + returnId + ": status " + ret.getStatus()
            );
        }

        InvoiceLine invoiceLine = invoiceLineRepository
            .findById(invoiceLineId)
            .orElseThrow(() -> new BadRequestAlertException("InvoiceLine not found", "salesReturn", "invoicelinenotfound"));

        if (invoiceLine.getInvoice() == null || !ret.getInvoiceId().equals(invoiceLine.getInvoice().getId())) {
            throw new BadRequestAlertException(
                "InvoiceLine does not belong to the invoice of this SalesReturn",
                "salesReturn",
                "invoicelinemismatch"
            );
        }

        // Resolve the stock item via the inventory service.
        Map<String, Object> stockItem = inventoryClient.scan(imei);
        Long stockItemId = stockItem.get("id") instanceof Number ? ((Number) stockItem.get("id")).longValue() : null;

        SalesReturnLine line = new SalesReturnLine();
        line.setSalesReturn(ret);
        line.setInvoiceLineId(invoiceLineId);
        line.setImei(imei);
        line.setStockItemId(stockItemId);
        line.setCondition(condition == null ? ItemCondition.INTACT : condition);
        line.setRefundAmount(invoiceLine.getUnitPriceAtPurchase());
        ret.getLines().add(line);
        salesReturnLineRepository.save(line);

        // Recompute total refund amount.
        BigDecimal total = ret
            .getLines()
            .stream()
            .map(SalesReturnLine::getRefundAmount)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        ret.setRefundAmount(total);

        return salesReturnMapper.toDto(salesReturnRepository.save(ret));
    }

    /**
     * Approve a sales return. Walks each line and tells the inventory service
     * what to do with the physical item; flips the status to APPROVED then REFUNDED.
     */
    public SalesReturnDTO approve(Long returnId) {
        log.debug("Approving SalesReturn {}", returnId);
        SalesReturn ret = loadOrThrow(returnId);
        if (ret.getStatus() == SalesReturnStatus.APPROVED || ret.getStatus() == SalesReturnStatus.REFUNDED) {
            // Idempotence: surface 409 to the caller.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SalesReturn already approved/refunded");
        }
        if (ret.getStatus() != SalesReturnStatus.DRAFT && ret.getStatus() != SalesReturnStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot approve in status " + ret.getStatus());
        }

        for (SalesReturnLine line : ret.getLines()) {
            if (line.getStockItemId() == null) {
                continue;
            }
            if (line.getCondition() == ItemCondition.DAMAGED) {
                inventoryClient.sendToRework(line.getStockItemId());
            } else {
                // TODO inter-services: Inventory must expose POST /api/stock-items/{id}/mark-returned
                // to put the item back into sellable stock; using markRepaired as a temporary substitute.
                inventoryClient.markRepaired(line.getStockItemId());
            }
        }

        ret.setStatus(SalesReturnStatus.APPROVED);
        ret.setProcessedAt(ZonedDateTime.now());
        ret = salesReturnRepository.save(ret);

        // Refund step is purely a status transition for now (payment integration is out of scope).
        ret.setStatus(SalesReturnStatus.REFUNDED);
        ret = salesReturnRepository.save(ret);

        return salesReturnMapper.toDto(ret);
    }

    @Transactional(readOnly = true)
    public Optional<SalesReturnDTO> findOne(Long id) {
        return salesReturnRepository.findById(id).map(salesReturnMapper::toDto);
    }

    private SalesReturn loadOrThrow(Long id) {
        return salesReturnRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SalesReturn not found: " + id));
    }

    private void requireStatus(SalesReturn ret, SalesReturnStatus expected, String action) {
        if (ret.getStatus() != expected) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cannot " + action + " SalesReturn " + ret.getId() + ": expected " + expected + " but was " + ret.getStatus()
            );
        }
    }
}
