package com.tunisales.business.domain;

import com.tunisales.business.domain.enumeration.ComplaintStatus;
import com.tunisales.business.domain.enumeration.ComplaintType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.10 — customer complaint raised by a commercial / admin.
 */
@Entity
@Table(name = "complaint")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Complaint implements Serializable {

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
    @Size(max = 100)
    @Column(name = "author_login", length = 100, nullable = false)
    private String authorLogin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ComplaintType type;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplaintStatus status = ComplaintStatus.NEW;

    @Column(name = "stock_item_id")
    private Long stockItemId;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "resolved_at")
    private ZonedDateTime resolvedAt;

    @Size(max = 100)
    @Column(name = "resolved_by_login", length = 100)
    private String resolvedByLogin;

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

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public ComplaintType getType() {
        return type;
    }

    public void setType(ComplaintType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public Long getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(Long stockItemId) {
        this.stockItemId = stockItemId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolvedByLogin() {
        return resolvedByLogin;
    }

    public void setResolvedByLogin(String resolvedByLogin) {
        this.resolvedByLogin = resolvedByLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;
        return id != null && id.equals(((Complaint) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
