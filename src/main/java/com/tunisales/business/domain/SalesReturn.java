package com.tunisales.business.domain;

import com.tunisales.business.domain.enumeration.SalesReturnStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.5 — a return of (part of) an invoice raised by the buyer.
 *
 * <p>One {@link SalesReturn} aggregates one or more {@link SalesReturnLine}s.
 * The total {@code refundAmount} is the sum of line refund amounts at approval time.</p>
 */
@Entity
@Table(name = "sales_return")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReturn implements Serializable {

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
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "return_number", length = 50, nullable = false, unique = true)
    private String returnNumber;

    @Size(max = 1000)
    @Column(name = "reason", length = 1000)
    private String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SalesReturnStatus status;

    @DecimalMin(value = "0")
    @Column(name = "refund_amount", precision = 21, scale = 2)
    private BigDecimal refundAmount;

    @Size(max = 100)
    @Column(name = "created_by_login", length = 100)
    private String createdByLogin;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "processed_at")
    private ZonedDateTime processedAt;

    @OneToMany(mappedBy = "salesReturn", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<SalesReturnLine> lines = new HashSet<>();

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

    public Set<SalesReturnLine> getLines() {
        return lines;
    }

    public void setLines(Set<SalesReturnLine> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesReturn)) return false;
        return id != null && id.equals(((SalesReturn) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
