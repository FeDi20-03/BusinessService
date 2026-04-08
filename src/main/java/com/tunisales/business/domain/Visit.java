package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.business.domain.enumeration.VisitObjective;
import com.tunisales.business.domain.enumeration.VisitStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Visit.
 */
@Entity
@Table(name = "visit")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Visit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "visit_order", nullable = false)
    private Integer visitOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "objective", nullable = false)
    private VisitObjective objective;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VisitStatus status;

    @Column(name = "latitude", precision = 21, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 21, scale = 2)
    private BigDecimal longitude;

    @Column(name = "checkin_at")
    private ZonedDateTime checkinAt;

    @Column(name = "checkout_at")
    private ZonedDateTime checkoutAt;

    @Size(max = 2000)
    @Column(name = "notes", length = 2000)
    private String notes;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "contacts", "priceLists", "orders" }, allowSetters = true)
    private Client client;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "visits" }, allowSetters = true)
    private Mission mission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Visit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVisitOrder() {
        return this.visitOrder;
    }

    public Visit visitOrder(Integer visitOrder) {
        this.setVisitOrder(visitOrder);
        return this;
    }

    public void setVisitOrder(Integer visitOrder) {
        this.visitOrder = visitOrder;
    }

    public VisitObjective getObjective() {
        return this.objective;
    }

    public Visit objective(VisitObjective objective) {
        this.setObjective(objective);
        return this;
    }

    public void setObjective(VisitObjective objective) {
        this.objective = objective;
    }

    public VisitStatus getStatus() {
        return this.status;
    }

    public Visit status(VisitStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public Visit latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public Visit longitude(BigDecimal longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public ZonedDateTime getCheckinAt() {
        return this.checkinAt;
    }

    public Visit checkinAt(ZonedDateTime checkinAt) {
        this.setCheckinAt(checkinAt);
        return this;
    }

    public void setCheckinAt(ZonedDateTime checkinAt) {
        this.checkinAt = checkinAt;
    }

    public ZonedDateTime getCheckoutAt() {
        return this.checkoutAt;
    }

    public Visit checkoutAt(ZonedDateTime checkoutAt) {
        this.setCheckoutAt(checkoutAt);
        return this;
    }

    public void setCheckoutAt(ZonedDateTime checkoutAt) {
        this.checkoutAt = checkoutAt;
    }

    public String getNotes() {
        return this.notes;
    }

    public Visit notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Visit createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Visit client(Client client) {
        this.setClient(client);
        return this;
    }

    public Mission getMission() {
        return this.mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Visit mission(Mission mission) {
        this.setMission(mission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visit)) {
            return false;
        }
        return id != null && id.equals(((Visit) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visit{" +
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
            "}";
    }
}
