package com.tunisales.business.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.business.domain.OrderLineItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderLineItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Long stockItemId;

    @Size(max = 15)
    private String stockItemImei;

    @NotNull
    private ZonedDateTime assignedAt;

    private OrderLineDTO orderLine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(Long stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getStockItemImei() {
        return stockItemImei;
    }

    public void setStockItemImei(String stockItemImei) {
        this.stockItemImei = stockItemImei;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public OrderLineDTO getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLineDTO orderLine) {
        this.orderLine = orderLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderLineItemDTO)) {
            return false;
        }

        OrderLineItemDTO orderLineItemDTO = (OrderLineItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderLineItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderLineItemDTO{" +
            "id=" + getId() +
            ", stockItemId=" + getStockItemId() +
            ", stockItemImei='" + getStockItemImei() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", orderLine=" + getOrderLine() +
            "}";
    }
}
