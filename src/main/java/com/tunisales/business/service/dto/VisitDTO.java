package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.VisitObjective;
import com.tunisales.business.domain.enumeration.VisitStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.business.domain.Visit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VisitDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer visitOrder;

    @NotNull
    private VisitObjective objective;

    @NotNull
    private VisitStatus status;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private ZonedDateTime checkinAt;

    private ZonedDateTime checkoutAt;

    @Size(max = 2000)
    private String notes;

    @NotNull
    private ZonedDateTime createdAt;

    private ClientDTO client;

    private MissionDTO mission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVisitOrder() {
        return visitOrder;
    }

    public void setVisitOrder(Integer visitOrder) {
        this.visitOrder = visitOrder;
    }

    public VisitObjective getObjective() {
        return objective;
    }

    public void setObjective(VisitObjective objective) {
        this.objective = objective;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public ZonedDateTime getCheckinAt() {
        return checkinAt;
    }

    public void setCheckinAt(ZonedDateTime checkinAt) {
        this.checkinAt = checkinAt;
    }

    public ZonedDateTime getCheckoutAt() {
        return checkoutAt;
    }

    public void setCheckoutAt(ZonedDateTime checkoutAt) {
        this.checkoutAt = checkoutAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public MissionDTO getMission() {
        return mission;
    }

    public void setMission(MissionDTO mission) {
        this.mission = mission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitDTO)) {
            return false;
        }

        VisitDTO visitDTO = (VisitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, visitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitDTO{" +
            "id=" + getId() +
            ", visitOrder=" + getVisitOrder() +
            ", objective='" + getObjective() + "'" +
            ", status='" + getStatus() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", checkinAt='" + getCheckinAt() + "'" +
            ", checkoutAt='" + getCheckoutAt() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", client=" + getClient() +
            ", mission=" + getMission() +
            "}";
    }
}
