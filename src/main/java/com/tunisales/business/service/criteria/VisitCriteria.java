package com.tunisales.business.service.criteria;

import com.tunisales.business.domain.enumeration.VisitObjective;
import com.tunisales.business.domain.enumeration.VisitStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.Visit} entity. This class is used
 * in {@link com.tunisales.business.web.rest.VisitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /visits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VisitCriteria implements Serializable, Criteria {

    /**
     * Class for filtering VisitObjective
     */
    public static class VisitObjectiveFilter extends Filter<VisitObjective> {

        public VisitObjectiveFilter() {}

        public VisitObjectiveFilter(VisitObjectiveFilter filter) {
            super(filter);
        }

        @Override
        public VisitObjectiveFilter copy() {
            return new VisitObjectiveFilter(this);
        }
    }

    /**
     * Class for filtering VisitStatus
     */
    public static class VisitStatusFilter extends Filter<VisitStatus> {

        public VisitStatusFilter() {}

        public VisitStatusFilter(VisitStatusFilter filter) {
            super(filter);
        }

        @Override
        public VisitStatusFilter copy() {
            return new VisitStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter visitOrder;

    private VisitObjectiveFilter objective;

    private VisitStatusFilter status;

    private BigDecimalFilter latitude;

    private BigDecimalFilter longitude;

    private ZonedDateTimeFilter checkinAt;

    private ZonedDateTimeFilter checkoutAt;

    private StringFilter notes;

    private ZonedDateTimeFilter createdAt;

    private LongFilter clientId;

    private LongFilter missionId;

    private Boolean distinct;

    public VisitCriteria() {}

    public VisitCriteria(VisitCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.visitOrder = other.visitOrder == null ? null : other.visitOrder.copy();
        this.objective = other.objective == null ? null : other.objective.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.checkinAt = other.checkinAt == null ? null : other.checkinAt.copy();
        this.checkoutAt = other.checkoutAt == null ? null : other.checkoutAt.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.missionId = other.missionId == null ? null : other.missionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public VisitCriteria copy() {
        return new VisitCriteria(this);
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

    public IntegerFilter getVisitOrder() {
        return visitOrder;
    }

    public IntegerFilter visitOrder() {
        if (visitOrder == null) {
            visitOrder = new IntegerFilter();
        }
        return visitOrder;
    }

    public void setVisitOrder(IntegerFilter visitOrder) {
        this.visitOrder = visitOrder;
    }

    public VisitObjectiveFilter getObjective() {
        return objective;
    }

    public VisitObjectiveFilter objective() {
        if (objective == null) {
            objective = new VisitObjectiveFilter();
        }
        return objective;
    }

    public void setObjective(VisitObjectiveFilter objective) {
        this.objective = objective;
    }

    public VisitStatusFilter getStatus() {
        return status;
    }

    public VisitStatusFilter status() {
        if (status == null) {
            status = new VisitStatusFilter();
        }
        return status;
    }

    public void setStatus(VisitStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getLatitude() {
        return latitude;
    }

    public BigDecimalFilter latitude() {
        if (latitude == null) {
            latitude = new BigDecimalFilter();
        }
        return latitude;
    }

    public void setLatitude(BigDecimalFilter latitude) {
        this.latitude = latitude;
    }

    public BigDecimalFilter getLongitude() {
        return longitude;
    }

    public BigDecimalFilter longitude() {
        if (longitude == null) {
            longitude = new BigDecimalFilter();
        }
        return longitude;
    }

    public void setLongitude(BigDecimalFilter longitude) {
        this.longitude = longitude;
    }

    public ZonedDateTimeFilter getCheckinAt() {
        return checkinAt;
    }

    public ZonedDateTimeFilter checkinAt() {
        if (checkinAt == null) {
            checkinAt = new ZonedDateTimeFilter();
        }
        return checkinAt;
    }

    public void setCheckinAt(ZonedDateTimeFilter checkinAt) {
        this.checkinAt = checkinAt;
    }

    public ZonedDateTimeFilter getCheckoutAt() {
        return checkoutAt;
    }

    public ZonedDateTimeFilter checkoutAt() {
        if (checkoutAt == null) {
            checkoutAt = new ZonedDateTimeFilter();
        }
        return checkoutAt;
    }

    public void setCheckoutAt(ZonedDateTimeFilter checkoutAt) {
        this.checkoutAt = checkoutAt;
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

    public LongFilter getMissionId() {
        return missionId;
    }

    public LongFilter missionId() {
        if (missionId == null) {
            missionId = new LongFilter();
        }
        return missionId;
    }

    public void setMissionId(LongFilter missionId) {
        this.missionId = missionId;
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
        final VisitCriteria that = (VisitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(visitOrder, that.visitOrder) &&
            Objects.equals(objective, that.objective) &&
            Objects.equals(status, that.status) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(checkinAt, that.checkinAt) &&
            Objects.equals(checkoutAt, that.checkoutAt) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(missionId, that.missionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            visitOrder,
            objective,
            status,
            latitude,
            longitude,
            checkinAt,
            checkoutAt,
            notes,
            createdAt,
            clientId,
            missionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (visitOrder != null ? "visitOrder=" + visitOrder + ", " : "") +
            (objective != null ? "objective=" + objective + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (checkinAt != null ? "checkinAt=" + checkinAt + ", " : "") +
            (checkoutAt != null ? "checkoutAt=" + checkoutAt + ", " : "") +
            (notes != null ? "notes=" + notes + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (missionId != null ? "missionId=" + missionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
