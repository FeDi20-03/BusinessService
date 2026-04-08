package com.tunisales.business.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.business.domain.PriceList} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PriceListDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal maxDiscountPct;

    @NotNull
    private ZonedDateTime validFrom;

    @NotNull
    private ZonedDateTime validTo;

    @NotNull
    private Boolean isActive;

    @NotNull
    private ZonedDateTime createdAt;

    private ProductDTO product;

    private ClientDTO client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getMaxDiscountPct() {
        return maxDiscountPct;
    }

    public void setMaxDiscountPct(BigDecimal maxDiscountPct) {
        this.maxDiscountPct = maxDiscountPct;
    }

    public ZonedDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(ZonedDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public ZonedDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(ZonedDateTime validTo) {
        this.validTo = validTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriceListDTO)) {
            return false;
        }

        PriceListDTO priceListDTO = (PriceListDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, priceListDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PriceListDTO{" +
            "id=" + getId() +
            ", unitPrice=" + getUnitPrice() +
            ", maxDiscountPct=" + getMaxDiscountPct() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", product=" + getProduct() +
            ", client=" + getClient() +
            "}";
    }
}
