package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.ItemCondition;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.5 — DTO for {@link com.tunisales.business.domain.SalesReturnLine}. */
public class SalesReturnLineDTO implements Serializable {

    private Long id;

    @NotNull
    private Long invoiceLineId;

    @Size(max = 50)
    private String imei;

    private Long stockItemId;

    @NotNull
    private ItemCondition condition;

    @DecimalMin(value = "0")
    private BigDecimal refundAmount;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesReturnLineDTO)) return false;
        SalesReturnLineDTO that = (SalesReturnLineDTO) o;
        return this.id != null && Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
