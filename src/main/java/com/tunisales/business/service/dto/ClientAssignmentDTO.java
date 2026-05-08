package com.tunisales.business.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.8 — DTO for {@link com.tunisales.business.domain.ClientAssignment}. */
public class ClientAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    private Long clientId;

    @NotNull
    @Size(max = 100)
    private String commercialLogin;

    @NotNull
    private Long zoneId;

    private ZonedDateTime assignedAt;

    @Size(max = 100)
    private String assignedByLogin;

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

    public String getCommercialLogin() {
        return commercialLogin;
    }

    public void setCommercialLogin(String commercialLogin) {
        this.commercialLogin = commercialLogin;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedByLogin() {
        return assignedByLogin;
    }

    public void setAssignedByLogin(String assignedByLogin) {
        this.assignedByLogin = assignedByLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientAssignmentDTO)) return false;
        ClientAssignmentDTO that = (ClientAssignmentDTO) o;
        return this.id != null && Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
