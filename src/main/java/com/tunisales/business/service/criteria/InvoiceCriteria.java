package com.tunisales.business.service.criteria;

import com.tunisales.business.domain.enumeration.InvoiceStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tunisales.business.domain.Invoice} entity. This class is used
 * in {@link com.tunisales.business.web.rest.InvoiceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /invoices?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering InvoiceStatus
     */
    public static class InvoiceStatusFilter extends Filter<InvoiceStatus> {

        public InvoiceStatusFilter() {}

        public InvoiceStatusFilter(InvoiceStatusFilter filter) {
            super(filter);
        }

        @Override
        public InvoiceStatusFilter copy() {
            return new InvoiceStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tenantId;

    private StringFilter invoiceNumber;

    private BigDecimalFilter amountHt;

    private BigDecimalFilter taxAmount;

    private BigDecimalFilter amountTtc;

    private InvoiceStatusFilter status;

    private ZonedDateTimeFilter issueDate;

    private ZonedDateTimeFilter dueDate;

    private ZonedDateTimeFilter paidAt;

    private BooleanFilter isDeleted;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter clientId;

    private LongFilter orderId;

    private Boolean distinct;

    public InvoiceCriteria() {}

    public InvoiceCriteria(InvoiceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tenantId = other.tenantId == null ? null : other.tenantId.copy();
        this.invoiceNumber = other.invoiceNumber == null ? null : other.invoiceNumber.copy();
        this.amountHt = other.amountHt == null ? null : other.amountHt.copy();
        this.taxAmount = other.taxAmount == null ? null : other.taxAmount.copy();
        this.amountTtc = other.amountTtc == null ? null : other.amountTtc.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.issueDate = other.issueDate == null ? null : other.issueDate.copy();
        this.dueDate = other.dueDate == null ? null : other.dueDate.copy();
        this.paidAt = other.paidAt == null ? null : other.paidAt.copy();
        this.isDeleted = other.isDeleted == null ? null : other.isDeleted.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InvoiceCriteria copy() {
        return new InvoiceCriteria(this);
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

    public StringFilter getInvoiceNumber() {
        return invoiceNumber;
    }

    public StringFilter invoiceNumber() {
        if (invoiceNumber == null) {
            invoiceNumber = new StringFilter();
        }
        return invoiceNumber;
    }

    public void setInvoiceNumber(StringFilter invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimalFilter getAmountHt() {
        return amountHt;
    }

    public BigDecimalFilter amountHt() {
        if (amountHt == null) {
            amountHt = new BigDecimalFilter();
        }
        return amountHt;
    }

    public void setAmountHt(BigDecimalFilter amountHt) {
        this.amountHt = amountHt;
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

    public BigDecimalFilter getAmountTtc() {
        return amountTtc;
    }

    public BigDecimalFilter amountTtc() {
        if (amountTtc == null) {
            amountTtc = new BigDecimalFilter();
        }
        return amountTtc;
    }

    public void setAmountTtc(BigDecimalFilter amountTtc) {
        this.amountTtc = amountTtc;
    }

    public InvoiceStatusFilter getStatus() {
        return status;
    }

    public InvoiceStatusFilter status() {
        if (status == null) {
            status = new InvoiceStatusFilter();
        }
        return status;
    }

    public void setStatus(InvoiceStatusFilter status) {
        this.status = status;
    }

    public ZonedDateTimeFilter getIssueDate() {
        return issueDate;
    }

    public ZonedDateTimeFilter issueDate() {
        if (issueDate == null) {
            issueDate = new ZonedDateTimeFilter();
        }
        return issueDate;
    }

    public void setIssueDate(ZonedDateTimeFilter issueDate) {
        this.issueDate = issueDate;
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

    public ZonedDateTimeFilter getPaidAt() {
        return paidAt;
    }

    public ZonedDateTimeFilter paidAt() {
        if (paidAt == null) {
            paidAt = new ZonedDateTimeFilter();
        }
        return paidAt;
    }

    public void setPaidAt(ZonedDateTimeFilter paidAt) {
        this.paidAt = paidAt;
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
        final InvoiceCriteria that = (InvoiceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(invoiceNumber, that.invoiceNumber) &&
            Objects.equals(amountHt, that.amountHt) &&
            Objects.equals(taxAmount, that.taxAmount) &&
            Objects.equals(amountTtc, that.amountTtc) &&
            Objects.equals(status, that.status) &&
            Objects.equals(issueDate, that.issueDate) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(paidAt, that.paidAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            tenantId,
            invoiceNumber,
            amountHt,
            taxAmount,
            amountTtc,
            status,
            issueDate,
            dueDate,
            paidAt,
            isDeleted,
            createdAt,
            updatedAt,
            clientId,
            orderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tenantId != null ? "tenantId=" + tenantId + ", " : "") +
            (invoiceNumber != null ? "invoiceNumber=" + invoiceNumber + ", " : "") +
            (amountHt != null ? "amountHt=" + amountHt + ", " : "") +
            (taxAmount != null ? "taxAmount=" + taxAmount + ", " : "") +
            (amountTtc != null ? "amountTtc=" + amountTtc + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (issueDate != null ? "issueDate=" + issueDate + ", " : "") +
            (dueDate != null ? "dueDate=" + dueDate + ", " : "") +
            (paidAt != null ? "paidAt=" + paidAt + ", " : "") +
            (isDeleted != null ? "isDeleted=" + isDeleted + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
