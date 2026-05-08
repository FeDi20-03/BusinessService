package com.tunisales.business.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.8 — links a client to a commercial within a zone.
 *
 * <p>One row represents the most recent assignment. Replacing the assignment
 * for a client means updating this row (or deleting and recreating it) — the
 * service layer handles the policy.</p>
 */
@Entity
@Table(name = "client_assignment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientAssignment implements Serializable {

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
    @Size(max = 100)
    @Column(name = "commercial_login", length = 100, nullable = false)
    private String commercialLogin;

    @NotNull
    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @NotNull
    @Column(name = "assigned_at", nullable = false)
    private ZonedDateTime assignedAt;

    @Size(max = 100)
    @Column(name = "assigned_by_login", length = 100)
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
        if (!(o instanceof ClientAssignment)) return false;
        return id != null && id.equals(((ClientAssignment) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
