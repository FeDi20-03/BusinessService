package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.ComplaintStatus;
import com.tunisales.business.domain.enumeration.ComplaintType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.10 — DTO for {@link com.tunisales.business.domain.Complaint}. */
public class ComplaintDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @Size(max = 100)
    private String authorLogin;

    @NotNull
    private ComplaintType type;

    private String description;

    private ComplaintStatus status;

    private Long stockItemId;

    private ZonedDateTime createdAt;

    private ZonedDateTime resolvedAt;

    @Size(max = 100)
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
        if (!(o instanceof ComplaintDTO)) return false;
        ComplaintDTO that = (ComplaintDTO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
