package com.tunisales.business.service.criteria;

import com.tunisales.business.domain.enumeration.DeliveryStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.Delivery} entity. This class is used
 * in {@link com.tunisales.business.web.rest.DeliveryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /deliveries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DeliveryStatus
     */
    public static class DeliveryStatusFilter extends Filter<DeliveryStatus> {

        public DeliveryStatusFilter() {}

        public DeliveryStatusFilter(DeliveryStatusFilter filter) {
            super(filter);
        }

        @Override
        public DeliveryStatusFilter copy() {
            return new DeliveryStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private StringFilter deliveryNumber;

    private DeliveryStatusFilter status;

    private StringFilter trackingNumber;

    private ZonedDateTimeFilter shippedAt;

    private ZonedDateTimeFilter deliveredAt;

    private ZonedDateTimeFilter confirmedAt;

    private StringFilter notes;

    private ZonedDateTimeFilter createdAt;

    private LongFilter orderId;

    private Boolean distinct;

    public DeliveryCriteria() {}

    public DeliveryCriteria(DeliveryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.deliveryNumber = other.deliveryNumber == null ? null : other.deliveryNumber.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.trackingNumber = other.trackingNumber == null ? null : other.trackingNumber.copy();
        this.shippedAt = other.shippedAt == null ? null : other.shippedAt.copy();
        this.deliveredAt = other.deliveredAt == null ? null : other.deliveredAt.copy();
        this.confirmedAt = other.confirmedAt == null ? null : other.confirmedAt.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DeliveryCriteria copy() {
        return new DeliveryCriteria(this);
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

    public StringFilter getDeliveryNumber() {
        return deliveryNumber;
    }

    public StringFilter deliveryNumber() {
        if (deliveryNumber == null) {
            deliveryNumber = new StringFilter();
        }
        return deliveryNumber;
    }

    public void setDeliveryNumber(StringFilter deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public DeliveryStatusFilter getStatus() {
        return status;
    }

    public DeliveryStatusFilter status() {
        if (status == null) {
            status = new DeliveryStatusFilter();
        }
        return status;
    }

    public void setStatus(DeliveryStatusFilter status) {
        this.status = status;
    }

    public StringFilter getTrackingNumber() {
        return trackingNumber;
    }

    public StringFilter trackingNumber() {
        if (trackingNumber == null) {
            trackingNumber = new StringFilter();
        }
        return trackingNumber;
    }

    public void setTrackingNumber(StringFilter trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public ZonedDateTimeFilter getShippedAt() {
        return shippedAt;
    }

    public ZonedDateTimeFilter shippedAt() {
        if (shippedAt == null) {
            shippedAt = new ZonedDateTimeFilter();
        }
        return shippedAt;
    }

    public void setShippedAt(ZonedDateTimeFilter shippedAt) {
        this.shippedAt = shippedAt;
    }

    public ZonedDateTimeFilter getDeliveredAt() {
        return deliveredAt;
    }

    public ZonedDateTimeFilter deliveredAt() {
        if (deliveredAt == null) {
            deliveredAt = new ZonedDateTimeFilter();
        }
        return deliveredAt;
    }

    public void setDeliveredAt(ZonedDateTimeFilter deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public ZonedDateTimeFilter getConfirmedAt() {
        return confirmedAt;
    }

    public ZonedDateTimeFilter confirmedAt() {
        if (confirmedAt == null) {
            confirmedAt = new ZonedDateTimeFilter();
        }
        return confirmedAt;
    }

    public void setConfirmedAt(ZonedDateTimeFilter confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public StringFilter notes() {
        if (notes == null) {
            notes = new StringFilter();
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
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

    public LongFilter getOrderId() {
        return orderId;
    }

    public LongFilter orderId() {
        if (orderId == null) {
            orderId = new LongFilter();
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
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
        final DeliveryCriteria that = (DeliveryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(deliveryNumber, that.deliveryNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(trackingNumber, that.trackingNumber) &&
            Objects.equals(shippedAt, that.shippedAt) &&
            Objects.equals(deliveredAt, that.deliveredAt) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            deliveryNumber,
            status,
            trackingNumber,
            shippedAt,
            deliveredAt,
            confirmedAt,
            notes,
            createdAt,
            orderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (deliveryNumber != null ? "deliveryNumber=" + deliveryNumber + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (trackingNumber != null ? "trackingNumber=" + trackingNumber + ", " : "") +
            (shippedAt != null ? "shippedAt=" + shippedAt + ", " : "") +
            (deliveredAt != null ? "deliveredAt=" + deliveredAt + ", " : "") +
            (confirmedAt != null ? "confirmedAt=" + confirmedAt + ", " : "") +
            (notes != null ? "notes=" + notes + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
