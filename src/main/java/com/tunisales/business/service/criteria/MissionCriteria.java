package com.tunisales.business.service.criteria;

import com.tunisales.business.domain.enumeration.MissionStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.Mission} entity. This class is used
 * in {@link com.tunisales.business.web.rest.MissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /missions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MissionStatus
     */
    public static class MissionStatusFilter extends Filter<MissionStatus> {

        public MissionStatusFilter() {}

        public MissionStatusFilter(MissionStatusFilter filter) {
            super(filter);
        }

        @Override
        public MissionStatusFilter copy() {
            return new MissionStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private StringFilter assignedToLogin;

    private StringFilter title;

    private StringFilter description;

    private ZonedDateTimeFilter missionDate;

    private MissionStatusFilter status;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter visitsId;

    private Boolean distinct;

    public MissionCriteria() {}

    public MissionCriteria(MissionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.assignedToLogin = other.assignedToLogin == null ? null : other.assignedToLogin.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.missionDate = other.missionDate == null ? null : other.missionDate.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.visitsId = other.visitsId == null ? null : other.visitsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MissionCriteria copy() {
        return new MissionCriteria(this);
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

    public StringFilter getAssignedToLogin() {
        return assignedToLogin;
    }

    public StringFilter assignedToLogin() {
        if (assignedToLogin == null) {
            assignedToLogin = new StringFilter();
        }
        return assignedToLogin;
    }

    public void setAssignedToLogin(StringFilter assignedToLogin) {
        this.assignedToLogin = assignedToLogin;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ZonedDateTimeFilter getMissionDate() {
        return missionDate;
    }

    public ZonedDateTimeFilter missionDate() {
        if (missionDate == null) {
            missionDate = new ZonedDateTimeFilter();
        }
        return missionDate;
    }

    public void setMissionDate(ZonedDateTimeFilter missionDate) {
        this.missionDate = missionDate;
    }

    public MissionStatusFilter getStatus() {
        return status;
    }

    public MissionStatusFilter status() {
        if (status == null) {
            status = new MissionStatusFilter();
        }
        return status;
    }

    public void setStatus(MissionStatusFilter status) {
        this.status = status;
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

    public LongFilter getVisitsId() {
        return visitsId;
    }

    public LongFilter visitsId() {
        if (visitsId == null) {
            visitsId = new LongFilter();
        }
        return visitsId;
    }

    public void setVisitsId(LongFilter visitsId) {
        this.visitsId = visitsId;
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
        final MissionCriteria that = (MissionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(assignedToLogin, that.assignedToLogin) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(missionDate, that.missionDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(visitsId, that.visitsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            assignedToLogin,
            title,
            description,
            missionDate,
            status,
            createdAt,
            updatedAt,
            visitsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (assignedToLogin != null ? "assignedToLogin=" + assignedToLogin + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (missionDate != null ? "missionDate=" + missionDate + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (visitsId != null ? "visitsId=" + visitsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
