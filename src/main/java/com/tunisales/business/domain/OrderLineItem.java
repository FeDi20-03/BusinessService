package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A OrderLineItem.
 */
@Entity
@Table(name = "order_line_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderLineItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "stock_item_id", nullable = false)
    private Long stockItemId;

    @Size(max = 15)
    @Column(name = "stock_item_imei", length = 15)
    private String stockItemImei;

    @NotNull
    @Column(name = "assigned_at", nullable = false)
    private ZonedDateTime assignedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "orderLineItems", "product", "order" }, allowSetters = true)
    private OrderLine orderLine;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderLineItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockItemId() {
        return this.stockItemId;
    }

    public OrderLineItem stockItemId(Long stockItemId) {
        this.setStockItemId(stockItemId);
        return this;
    }

    public void setStockItemId(Long stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getStockItemImei() {
        return this.stockItemImei;
    }

    public OrderLineItem stockItemImei(String stockItemImei) {
        this.setStockItemImei(stockItemImei);
        return this;
    }

    public void setStockItemImei(String stockItemImei) {
        this.stockItemImei = stockItemImei;
    }

    public ZonedDateTime getAssignedAt() {
        return this.assignedAt;
    }

    public OrderLineItem assignedAt(ZonedDateTime assignedAt) {
        this.setAssignedAt(assignedAt);
        return this;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public OrderLine getOrderLine() {
        return this.orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public OrderLineItem orderLine(OrderLine orderLine) {
        this.setOrderLine(orderLine);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderLineItem)) {
            return false;
        }
        return id != null && id.equals(((OrderLineItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderLineItem{" +
            "id=" + getId() +
            ", stockItemId=" + getStockItemId() +
            ", stockItemImei='" + getStockItemImei() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            "}";
    }
}
