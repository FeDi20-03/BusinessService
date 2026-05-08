package com.tunisales.business.domain;

import com.tunisales.business.domain.enumeration.CreditNoteStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.6 — commercial credit note (avoir) granted to a client.
 *
 * <p>Three lifecycle steps: created in {@code DRAFT}, validated by an admin into
 * {@code ISSUED}, then optionally consumed against an order via {@code APPLIED}
 * (which decrements {@code Client.creditUsed}).</p>
 */
@Entity
@Table(name = "credit_note")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CreditNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "credit_note_number", length = 50, nullable = false, unique = true)
    private String creditNoteNumber;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Size(max = 1000)
    @Column(name = "reason", length = 1000)
    private String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CreditNoteStatus status;

    @Size(max = 100)
    @Column(name = "created_by_login", length = 100)
    private String createdByLogin;

    @Size(max = 100)
    @Column(name = "validated_by_login", length = 100)
    private String validatedByLogin;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "validated_at")
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
        if (!(o instanceof CreditNote)) return false;
        return id != null && id.equals(((CreditNote) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
