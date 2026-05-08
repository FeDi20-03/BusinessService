package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.business.domain.enumeration.ItemCondition;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.5 — one returned physical item, identified by its IMEI,
 * attached to a {@link SalesReturn}.
 *
 * <p>The {@code refundAmount} is the unit price at purchase captured on the
 * matching {@link InvoiceLine} when the line was scanned (so price changes
 * after purchase do not affect refunds).</p>
 */
@Entity
@Table(name = "sales_return_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReturnLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "invoice_line_id", nullable = false)
    private Long invoiceLineId;

    @Size(max = 50)
    @Column(name = "imei", length = 50)
    private String imei;

    /** Stock-item id resolved from the IMEI via the inventory service (sub-step 2.5). */
    @Column(name = "stock_item_id")
    private Long stockItemId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    private ItemCondition condition;

    @DecimalMin(value = "0")
    @Column(name = "refund_amount", precision = 21, scale = 2)
    private BigDecimal refundAmount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "lines" }, allowSetters = true)
    private SalesReturn salesReturn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceLineId() {
        return invoiceLineId;
    }

    public void setInvoiceLineId(Long invoiceLineId) {
        this.invoiceLineId = invoiceLineId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Long getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(Long stockItemId) {
        this.stockItemId = stockItemId;
    }

    public ItemCondition getCondition() {
        return condition;
    }

    public void setCondition(ItemCondition condition) {
        this.condition = condition;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public SalesReturn getSalesReturn() {
        return salesReturn;
    }

    public void setSalesReturn(SalesReturn salesReturn) {
        this.salesReturn = salesReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesReturnLine)) return false;
        return id != null && id.equals(((SalesReturnLine) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
