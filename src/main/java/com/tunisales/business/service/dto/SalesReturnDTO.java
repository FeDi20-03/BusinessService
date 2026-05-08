package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.SalesReturnStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/** Sub-step 2.5 — DTO for {@link com.tunisales.business.domain.SalesReturn}. */
public class SalesReturnDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    private Long invoiceId;

    @Size(max = 50)
    private String returnNumber;

    @Size(max = 1000)
    private String reason;

    private SalesReturnStatus status;

    @DecimalMin(value = "0")
    private BigDecimal refundAmount;

    @Size(max = 100)
    private String createdByLogin;

    private ZonedDateTime createdAt;

    private ZonedDateTime processedAt;

    private Set<SalesReturnLineDTO> lines = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getReturnNumber() {
        return returnNumber;
    }

    public void setReturnNumber(String returnNumber) {
        this.returnNumber = returnNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SalesReturnStatus getStatus() {
        return status;
    }

    public void setStatus(SalesReturnStatus status) {
        this.status = status;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getCreatedByLogin() {
        return createdByLogin;
    }

    public void setCreatedByLogin(String createdByLogin) {
        this.createdByLogin = createdByLogin;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(ZonedDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Set<SalesReturnLineDTO> getLines() {
        return lines;
    }

    public void setLines(Set<SalesReturnLineDTO> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesReturnDTO)) return false;
        SalesReturnDTO that = (SalesReturnDTO) o;
        return this.id != null && Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
