package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A PriceList.
 */
@Entity
@Table(name = "price_list")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PriceList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "max_discount_pct", precision = 21, scale = 2)
    private BigDecimal maxDiscountPct;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private ZonedDateTime validFrom;

    @NotNull
    @Column(name = "valid_to", nullable = false)
    private ZonedDateTime validTo;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(optional = false)
    @NotNull
    private Product product;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "contacts", "priceLists", "orders" }, allowSetters = true)
    private Client client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PriceList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public PriceList unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getMaxDiscountPct() {
        return this.maxDiscountPct;
    }

    public PriceList maxDiscountPct(BigDecimal maxDiscountPct) {
        this.setMaxDiscountPct(maxDiscountPct);
        return this;
    }

    public void setMaxDiscountPct(BigDecimal maxDiscountPct) {
        this.maxDiscountPct = maxDiscountPct;
    }

    public ZonedDateTime getValidFrom() {
        return this.validFrom;
    }

    public PriceList validFrom(ZonedDateTime validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(ZonedDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public ZonedDateTime getValidTo() {
        return this.validTo;
    }

    public PriceList validTo(ZonedDateTime validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(ZonedDateTime validTo) {
        this.validTo = validTo;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public PriceList isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public PriceList createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PriceList product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PriceList client(Client client) {
        this.setClient(client);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriceList)) {
            return false;
        }
        return id != null && id.equals(((PriceList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PriceList{" +
            "id=" + getId() +
            ", unitPrice=" + getUnitPrice() +
            ", maxDiscountPct=" + getMaxDiscountPct() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
