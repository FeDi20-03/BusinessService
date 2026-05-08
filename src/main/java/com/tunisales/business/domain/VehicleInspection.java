package com.tunisales.business.domain;

import com.tunisales.business.domain.enumeration.VehicleState;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.13 — periodic inspection of a commercial's vehicle by the fleet manager.
 */
@Entity
@Table(name = "vehicle_inspection")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VehicleInspection implements Serializable {

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
    @Column(name = "commercial_login", length = 100, nullable = false)
    private String commercialLogin;

    @NotNull
    @Size(max = 100)
    @Column(name = "chef_parc_login", length = 100, nullable = false)
    private String chefParcLogin;

    @NotNull
    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_state", nullable = false)
    private VehicleState vehicleState;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "note", nullable = false)
    private Integer note;

    @NotNull
    @Column(name = "needs_repair", nullable = false)
    private Boolean needsRepair = Boolean.FALSE;

    @Size(max = 2000)
    @Column(name = "comments", length = 2000)
    private String comments;

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

    public String getCommercialLogin() {
        return commercialLogin;
    }

    public void setCommercialLogin(String commercialLogin) {
        this.commercialLogin = commercialLogin;
    }

    public String getChefParcLogin() {
        return chefParcLogin;
    }

    public void setChefParcLogin(String chefParcLogin) {
        this.chefParcLogin = chefParcLogin;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public VehicleState getVehicleState() {
        return vehicleState;
    }

    public void setVehicleState(VehicleState vehicleState) {
        this.vehicleState = vehicleState;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public Boolean getNeedsRepair() {
        return needsRepair;
    }

    public void setNeedsRepair(Boolean needsRepair) {
        this.needsRepair = needsRepair;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleInspection)) return false;
        return id != null && id.equals(((VehicleInspection) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
