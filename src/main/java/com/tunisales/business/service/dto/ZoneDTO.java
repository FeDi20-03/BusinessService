package com.tunisales.business.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.8 — DTO for {@link com.tunisales.business.domain.Zone}. */
public class ZoneDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(min = 2, max = 50)
    private String code;

    @Size(max = 1000)
    private String description;

    private Boolean isActive;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZoneDTO)) return false;
        ZoneDTO that = (ZoneDTO) o;
        return this.id != null && Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
