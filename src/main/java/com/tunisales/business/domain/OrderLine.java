package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A OrderLine.
 */
@Entity
@Table(name = "order_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "discount_pct", precision = 21, scale = 2)
    private BigDecimal discountPct;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "line_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "orderLine")
    @JsonIgnoreProperties(value = { "orderLine" }, allowSetters = true)
    private Set<OrderLineItem> orderLineItems = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Product product;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "orderLines", "deliveries", "invoices", "client" }, allowSetters = true)
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public OrderLine unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountPct() {
        return this.discountPct;
    }

    public OrderLine discountPct(BigDecimal discountPct) {
        this.setDiscountPct(discountPct);
        return this;
    }

    public void setDiscountPct(BigDecimal discountPct) {
        this.discountPct = discountPct;
    }

    public BigDecimal getLineTotal() {
        return this.lineTotal;
    }

    public OrderLine lineTotal(BigDecimal lineTotal) {
        this.setLineTotal(lineTotal);
        return this;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public OrderLine createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<OrderLineItem> getOrderLineItems() {
        return this.orderLineItems;
    }

    public void setOrderLineItems(Set<OrderLineItem> orderLineItems) {
        if (this.orderLineItems != null) {
            this.orderLineItems.forEach(i -> i.setOrderLine(null));
        }
        if (orderLineItems != null) {
            orderLineItems.forEach(i -> i.setOrderLine(this));
        }
        this.orderLineItems = orderLineItems;
    }

    public OrderLine orderLineItems(Set<OrderLineItem> orderLineItems) {
        this.setOrderLineItems(orderLineItems);
        return this;
    }

    public OrderLine addOrderLineItems(OrderLineItem orderLineItem) {
        this.orderLineItems.add(orderLineItem);
        orderLineItem.setOrderLine(this);
        return this;
    }

    public OrderLine removeOrderLineItems(OrderLineItem orderLineItem) {
        this.orderLineItems.remove(orderLineItem);
        orderLineItem.setOrderLine(null);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderLine product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderLine order(Order order) {
        this.setOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderLine)) {
            return false;
        }
        return id != null && id.equals(((OrderLine) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderLine{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", discountPct=" + getDiscountPct() +
            ", lineTotal=" + getLineTotal() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
