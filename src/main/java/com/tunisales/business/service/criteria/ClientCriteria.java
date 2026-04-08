package com.tunisales.business.service.criteria;

import com.tunisales.business.domain.enumeration.ClientStatus;
import com.tunisales.business.domain.enumeration.ClientType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.Client} entity. This class is used
 * in {@link com.tunisales.business.web.rest.ClientResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /clients?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ClientType
     */
    public static class ClientTypeFilter extends Filter<ClientType> {

        public ClientTypeFilter() {}

        public ClientTypeFilter(ClientTypeFilter filter) {
            super(filter);
        }

        @Override
        public ClientTypeFilter copy() {
            return new ClientTypeFilter(this);
        }
    }

    /**
     * Class for filtering ClientStatus
     */
    public static class ClientStatusFilter extends Filter<ClientStatus> {

        public ClientStatusFilter() {}

        public ClientStatusFilter(ClientStatusFilter filter) {
            super(filter);
        }

        @Override
        public ClientStatusFilter copy() {
            return new ClientStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private StringFilter name;

    private StringFilter taxId;

    private ClientTypeFilter clientType;

    private BigDecimalFilter creditLimit;

    private BigDecimalFilter creditUsed;

    private IntegerFilter paymentTermsDays;

    private ClientStatusFilter status;

    private ZonedDateTimeFilter lastOrderAt;

    private BooleanFilter isDeleted;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter contactsId;

    private LongFilter priceListsId;

    private LongFilter ordersId;

    private Boolean distinct;

    public ClientCriteria() {}

    public ClientCriteria(ClientCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.taxId = other.taxId == null ? null : other.taxId.copy();
        this.clientType = other.clientType == null ? null : other.clientType.copy();
        this.creditLimit = other.creditLimit == null ? null : other.creditLimit.copy();
        this.creditUsed = other.creditUsed == null ? null : other.creditUsed.copy();
        this.paymentTermsDays = other.paymentTermsDays == null ? null : other.paymentTermsDays.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.lastOrderAt = other.lastOrderAt == null ? null : other.lastOrderAt.copy();
        this.isDeleted = other.isDeleted == null ? null : other.isDeleted.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.contactsId = other.contactsId == null ? null : other.contactsId.copy();
        this.priceListsId = other.priceListsId == null ? null : other.priceListsId.copy();
        this.ordersId = other.ordersId == null ? null : other.ordersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClientCriteria copy() {
        return new ClientCriteria(this);
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

    public LongFilter getTenantId() {
        return tenantId;
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            tenantId = new LongFilter();
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getTaxId() {
        return taxId;
    }

    public StringFilter taxId() {
        if (taxId == null) {
            taxId = new StringFilter();
        }
        return taxId;
    }

    public void setTaxId(StringFilter taxId) {
        this.taxId = taxId;
    }

    public ClientTypeFilter getClientType() {
        return clientType;
    }

    public ClientTypeFilter clientType() {
        if (clientType == null) {
            clientType = new ClientTypeFilter();
        }
        return clientType;
    }

    public void setClientType(ClientTypeFilter clientType) {
        this.clientType = clientType;
    }

    public BigDecimalFilter getCreditLimit() {
        return creditLimit;
    }

    public BigDecimalFilter creditLimit() {
        if (creditLimit == null) {
            creditLimit = new BigDecimalFilter();
        }
        return creditLimit;
    }

    public void setCreditLimit(BigDecimalFilter creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimalFilter getCreditUsed() {
        return creditUsed;
    }

    public BigDecimalFilter creditUsed() {
        if (creditUsed == null) {
            creditUsed = new BigDecimalFilter();
        }
        return creditUsed;
    }

    public void setCreditUsed(BigDecimalFilter creditUsed) {
        this.creditUsed = creditUsed;
    }

    public IntegerFilter getPaymentTermsDays() {
        return paymentTermsDays;
    }

    public IntegerFilter paymentTermsDays() {
        if (paymentTermsDays == null) {
            paymentTermsDays = new IntegerFilter();
        }
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(IntegerFilter paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }

    public ClientStatusFilter getStatus() {
        return status;
    }

    public ClientStatusFilter status() {
        if (status == null) {
            status = new ClientStatusFilter();
        }
        return status;
    }

    public void setStatus(ClientStatusFilter status) {
        this.status = status;
    }

    public ZonedDateTimeFilter getLastOrderAt() {
        return lastOrderAt;
    }

    public ZonedDateTimeFilter lastOrderAt() {
        if (lastOrderAt == null) {
            lastOrderAt = new ZonedDateTimeFilter();
        }
        return lastOrderAt;
    }

    public void setLastOrderAt(ZonedDateTimeFilter lastOrderAt) {
        this.lastOrderAt = lastOrderAt;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            isDeleted = new BooleanFilter();
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
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

    public ZonedDateTimeFilter getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTimeFilter updatedAt() {
        if (updatedAt == null) {
            updatedAt = new ZonedDateTimeFilter();
        }
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTimeFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getContactsId() {
        return contactsId;
    }

    public LongFilter contactsId() {
        if (contactsId == null) {
            contactsId = new LongFilter();
        }
        return contactsId;
    }

    public void setContactsId(LongFilter contactsId) {
        this.contactsId = contactsId;
    }

    public LongFilter getPriceListsId() {
        return priceListsId;
    }

    public LongFilter priceListsId() {
        if (priceListsId == null) {
            priceListsId = new LongFilter();
        }
        return priceListsId;
    }

    public void setPriceListsId(LongFilter priceListsId) {
        this.priceListsId = priceListsId;
    }

    public LongFilter getOrdersId() {
        return ordersId;
    }

    public LongFilter ordersId() {
        if (ordersId == null) {
            ordersId = new LongFilter();
        }
        return ordersId;
    }

    public void setOrdersId(LongFilter ordersId) {
        this.ordersId = ordersId;
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
        final ClientCriteria that = (ClientCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(taxId, that.taxId) &&
            Objects.equals(clientType, that.clientType) &&
            Objects.equals(creditLimit, that.creditLimit) &&
            Objects.equals(creditUsed, that.creditUsed) &&
            Objects.equals(paymentTermsDays, that.paymentTermsDays) &&
            Objects.equals(status, that.status) &&
            Objects.equals(lastOrderAt, that.lastOrderAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(contactsId, that.contactsId) &&
            Objects.equals(priceListsId, that.priceListsId) &&
            Objects.equals(ordersId, that.ordersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            name,
            taxId,
            clientType,
            creditLimit,
            creditUsed,
            paymentTermsDays,
            status,
            lastOrderAt,
            isDeleted,
            createdAt,
            updatedAt,
            contactsId,
            priceListsId,
            ordersId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (taxId != null ? "taxId=" + taxId + ", " : "") +
            (clientType != null ? "clientType=" + clientType + ", " : "") +
            (creditLimit != null ? "creditLimit=" + creditLimit + ", " : "") +
            (creditUsed != null ? "creditUsed=" + creditUsed + ", " : "") +
            (paymentTermsDays != null ? "paymentTermsDays=" + paymentTermsDays + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (lastOrderAt != null ? "lastOrderAt=" + lastOrderAt + ", " : "") +
            (isDeleted != null ? "isDeleted=" + isDeleted + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (contactsId != null ? "contactsId=" + contactsId + ", " : "") +
            (priceListsId != null ? "priceListsId=" + priceListsId + ", " : "") +
            (ordersId != null ? "ordersId=" + ordersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
