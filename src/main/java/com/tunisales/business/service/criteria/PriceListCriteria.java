package com.tunisales.business.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.PriceList} entity. This class is used
 * in {@link com.tunisales.business.web.rest.PriceListResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /price-lists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PriceListCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter unitPrice;

    private BigDecimalFilter maxDiscountPct;

    private ZonedDateTimeFilter validFrom;

    private ZonedDateTimeFilter validTo;

    private BooleanFilter isActive;

    private ZonedDateTimeFilter createdAt;

    private LongFilter productId;

    private LongFilter clientId;

    private Boolean distinct;

    public PriceListCriteria() {}

    public PriceListCriteria(PriceListCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.unitPrice = other.unitPrice == null ? null : other.unitPrice.copy();
        this.maxDiscountPct = other.maxDiscountPct == null ? null : other.maxDiscountPct.copy();
        this.validFrom = other.validFrom == null ? null : other.validFrom.copy();
        this.validTo = other.validTo == null ? null : other.validTo.copy();
        this.isActive = other.isActive == null ? null : other.isActive.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PriceListCriteria copy() {
        return new PriceListCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getUnitPrice() {
        return unitPrice;
    }

    public BigDecimalFilter unitPrice() {
        if (unitPrice == null) {
            unitPrice = new BigDecimalFilter();
        }
        return unitPrice;
    }

    public void setUnitPrice(BigDecimalFilter unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimalFilter getMaxDiscountPct() {
        return maxDiscountPct;
    }

    public BigDecimalFilter maxDiscountPct() {
        if (maxDiscountPct == null) {
            maxDiscountPct = new BigDecimalFilter();
        }
        return maxDiscountPct;
    }

    public void setMaxDiscountPct(BigDecimalFilter maxDiscountPct) {
        this.maxDiscountPct = maxDiscountPct;
    }

    public ZonedDateTimeFilter getValidFrom() {
        return validFrom;
    }

    public ZonedDateTimeFilter validFrom() {
        if (validFrom == null) {
            validFrom = new ZonedDateTimeFilter();
        }
        return validFrom;
    }

    public void setValidFrom(ZonedDateTimeFilter validFrom) {
        this.validFrom = validFrom;
    }

    public ZonedDateTimeFilter getValidTo() {
        return validTo;
    }

    public ZonedDateTimeFilter validTo() {
        if (validTo == null) {
            validTo = new ZonedDateTimeFilter();
        }
        return validTo;
    }

    public void setValidTo(ZonedDateTimeFilter validTo) {
        this.validTo = validTo;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            isActive = new BooleanFilter();
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public LongFilter clientId() {
        if (clientId == null) {
            clientId = new LongFilter();
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PriceListCriteria that = (PriceListCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(maxDiscountPct, that.maxDiscountPct) &&
            Objects.equals(validFrom, that.validFrom) &&
            Objects.equals(validTo, that.validTo) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unitPrice, maxDiscountPct, validFrom, validTo, isActive, createdAt, productId, clientId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PriceListCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (unitPrice != null ? "unitPrice=" + unitPrice + ", " : "") +
            (maxDiscountPct != null ? "maxDiscountPct=" + maxDiscountPct + ", " : "") +
            (validFrom != null ? "validFrom=" + validFrom + ", " : "") +
            (validTo != null ? "validTo=" + validTo + ", " : "") +
            (isActive != null ? "isActive=" + isActive + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
