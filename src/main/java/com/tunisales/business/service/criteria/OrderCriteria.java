package com.tunisales.business.service.criteria;

import com.tunisales.business.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.Order} entity. This class is used
 * in {@link com.tunisales.business.web.rest.OrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private StringFilter orderNumber;

    private OrderStatusFilter status;

    private BigDecimalFilter subtotal;

    private BigDecimalFilter discountAmount;

    private BigDecimalFilter taxAmount;

    private BigDecimalFilter totalAmount;

    private IntegerFilter paymentTermsDays;

    private ZonedDateTimeFilter dueDate;

    private StringFilter rejectionReason;

    private ZonedDateTimeFilter submittedAt;

    private ZonedDateTimeFilter validatedAt;

    private BooleanFilter isDeleted;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter orderLinesId;

    private LongFilter deliveriesId;

    private LongFilter invoicesId;

    private LongFilter clientId;

    private Boolean distinct;

    public OrderCriteria() {}

    public OrderCriteria(OrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.orderNumber = other.orderNumber == null ? null : other.orderNumber.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.subtotal = other.subtotal == null ? null : other.subtotal.copy();
        this.discountAmount = other.discountAmount == null ? null : other.discountAmount.copy();
        this.taxAmount = other.taxAmount == null ? null : other.taxAmount.copy();
        this.totalAmount = other.totalAmount == null ? null : other.totalAmount.copy();
        this.paymentTermsDays = other.paymentTermsDays == null ? null : other.paymentTermsDays.copy();
        this.dueDate = other.dueDate == null ? null : other.dueDate.copy();
        this.rejectionReason = other.rejectionReason == null ? null : other.rejectionReason.copy();
        this.submittedAt = other.submittedAt == null ? null : other.submittedAt.copy();
        this.validatedAt = other.validatedAt == null ? null : other.validatedAt.copy();
        this.isDeleted = other.isDeleted == null ? null : other.isDeleted.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.orderLinesId = other.orderLinesId == null ? null : other.orderLinesId.copy();
        this.deliveriesId = other.deliveriesId == null ? null : other.deliveriesId.copy();
        this.invoicesId = other.invoicesId == null ? null : other.invoicesId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderCriteria copy() {
        return new OrderCriteria(this);
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

    public StringFilter getOrderNumber() {
        return orderNumber;
    }

    public StringFilter orderNumber() {
        if (orderNumber == null) {
            orderNumber = new StringFilter();
        }
        return orderNumber;
    }

    public void setOrderNumber(StringFilter orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatusFilter getStatus() {
        return status;
    }

    public OrderStatusFilter status() {
        if (status == null) {
            status = new OrderStatusFilter();
        }
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getSubtotal() {
        return subtotal;
    }

    public BigDecimalFilter subtotal() {
        if (subtotal == null) {
            subtotal = new BigDecimalFilter();
        }
        return subtotal;
    }

    public void setSubtotal(BigDecimalFilter subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimalFilter discountAmount() {
        if (discountAmount == null) {
            discountAmount = new BigDecimalFilter();
        }
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimalFilter getTaxAmount() {
        return taxAmount;
    }

    public BigDecimalFilter taxAmount() {
        if (taxAmount == null) {
            taxAmount = new BigDecimalFilter();
        }
        return taxAmount;
    }

    public void setTaxAmount(BigDecimalFilter taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            totalAmount = new BigDecimalFilter();
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
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

    public ZonedDateTimeFilter getDueDate() {
        return dueDate;
    }

    public ZonedDateTimeFilter dueDate() {
        if (dueDate == null) {
            dueDate = new ZonedDateTimeFilter();
        }
        return dueDate;
    }

    public void setDueDate(ZonedDateTimeFilter dueDate) {
        this.dueDate = dueDate;
    }

    public StringFilter getRejectionReason() {
        return rejectionReason;
    }

    public StringFilter rejectionReason() {
        if (rejectionReason == null) {
            rejectionReason = new StringFilter();
        }
        return rejectionReason;
    }

    public void setRejectionReason(StringFilter rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public ZonedDateTimeFilter getSubmittedAt() {
        return submittedAt;
    }

    public ZonedDateTimeFilter submittedAt() {
        if (submittedAt == null) {
            submittedAt = new ZonedDateTimeFilter();
        }
        return submittedAt;
    }

    public void setSubmittedAt(ZonedDateTimeFilter submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ZonedDateTimeFilter getValidatedAt() {
        return validatedAt;
    }

    public ZonedDateTimeFilter validatedAt() {
        if (validatedAt == null) {
            validatedAt = new ZonedDateTimeFilter();
        }
        return validatedAt;
    }

    public void setValidatedAt(ZonedDateTimeFilter validatedAt) {
        this.validatedAt = validatedAt;
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

    public LongFilter getOrderLinesId() {
        return orderLinesId;
    }

    public LongFilter orderLinesId() {
        if (orderLinesId == null) {
            orderLinesId = new LongFilter();
        }
        return orderLinesId;
    }

    public void setOrderLinesId(LongFilter orderLinesId) {
        this.orderLinesId = orderLinesId;
    }

    public LongFilter getDeliveriesId() {
        return deliveriesId;
    }

    public LongFilter deliveriesId() {
        if (deliveriesId == null) {
            deliveriesId = new LongFilter();
        }
        return deliveriesId;
    }

    public void setDeliveriesId(LongFilter deliveriesId) {
        this.deliveriesId = deliveriesId;
    }

    public LongFilter getInvoicesId() {
        return invoicesId;
    }

    public LongFilter invoicesId() {
        if (invoicesId == null) {
            invoicesId = new LongFilter();
        }
        return invoicesId;
    }

    public void setInvoicesId(LongFilter invoicesId) {
        this.invoicesId = invoicesId;
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
        final OrderCriteria that = (OrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(orderNumber, that.orderNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(subtotal, that.subtotal) &&
            Objects.equals(discountAmount, that.discountAmount) &&
            Objects.equals(taxAmount, that.taxAmount) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(paymentTermsDays, that.paymentTermsDays) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(rejectionReason, that.rejectionReason) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(validatedAt, that.validatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(orderLinesId, that.orderLinesId) &&
            Objects.equals(deliveriesId, that.deliveriesId) &&
            Objects.equals(invoicesId, that.invoicesId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            orderNumber,
            status,
            subtotal,
            discountAmount,
            taxAmount,
            totalAmount,
            paymentTermsDays,
            dueDate,
            rejectionReason,
            submittedAt,
            validatedAt,
            isDeleted,
            createdAt,
            updatedAt,
            orderLinesId,
            deliveriesId,
            invoicesId,
            clientId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (orderNumber != null ? "orderNumber=" + orderNumber + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (subtotal != null ? "subtotal=" + subtotal + ", " : "") +
            (discountAmount != null ? "discountAmount=" + discountAmount + ", " : "") +
            (taxAmount != null ? "taxAmount=" + taxAmount + ", " : "") +
            (totalAmount != null ? "totalAmount=" + totalAmount + ", " : "") +
            (paymentTermsDays != null ? "paymentTermsDays=" + paymentTermsDays + ", " : "") +
            (dueDate != null ? "dueDate=" + dueDate + ", " : "") +
            (rejectionReason != null ? "rejectionReason=" + rejectionReason + ", " : "") +
            (submittedAt != null ? "submittedAt=" + submittedAt + ", " : "") +
            (validatedAt != null ? "validatedAt=" + validatedAt + ", " : "") +
            (isDeleted != null ? "isDeleted=" + isDeleted + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (orderLinesId != null ? "orderLinesId=" + orderLinesId + ", " : "") +
            (deliveriesId != null ? "deliveriesId=" + deliveriesId + ", " : "") +
            (invoicesId != null ? "invoicesId=" + invoicesId + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
