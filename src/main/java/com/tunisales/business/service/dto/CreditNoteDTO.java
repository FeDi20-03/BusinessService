package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.CreditNoteStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.6 — DTO for {@link com.tunisales.business.domain.CreditNote}. */
public class CreditNoteDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    private Long clientId;

    @Size(max = 50)
    private String creditNoteNumber;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @Size(max = 1000)
    private String reason;

    private CreditNoteStatus status;

    @Size(max = 100)
    private String createdByLogin;

    @Size(max = 100)
    private String validatedByLogin;

    private ZonedDateTime createdAt;

    private ZonedDateTime validatedAt;

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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCreditNoteNumber() {
        return creditNoteNumber;
    }

    public void setCreditNoteNumber(String creditNoteNumber) {
        this.creditNoteNumber = creditNoteNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public CreditNoteStatus getStatus() {
        return status;
    }

    public void setStatus(CreditNoteStatus status) {
        this.status = status;
    }

    public String getCreatedByLogin() {
        return createdByLogin;
    }

    public void setCreatedByLogin(String createdByLogin) {
        this.createdByLogin = createdByLogin;
    }

    public String getValidatedByLogin() {
        return validatedByLogin;
    }

    public void setValidatedByLogin(String validatedByLogin) {
        this.validatedByLogin = validatedByLogin;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(ZonedDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreditNoteDTO)) return false;
        CreditNoteDTO that = (CreditNoteDTO) o;
        return this.id != null && Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
