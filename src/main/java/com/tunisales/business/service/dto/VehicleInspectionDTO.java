package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.VehicleState;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.13 — DTO for {@link com.tunisales.business.domain.VehicleInspection}. */
public class VehicleInspectionDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    @Size(max = 100)
    private String commercialLogin;

    @Size(max = 100)
    private String chefParcLogin;

    @NotNull
    private LocalDate inspectionDate;

    @NotNull
    private VehicleState vehicleState;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer note;

    private Boolean needsRepair;

    @Size(max = 2000)
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
        if (!(o instanceof VehicleInspectionDTO)) return false;
        VehicleInspectionDTO that = (VehicleInspectionDTO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
