package com.tunisales.business.service.dto;

import com.tunisales.business.domain.enumeration.OrderStatus;
import com.tunisales.business.domain.enumeration.PaymentMethod;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.tunisales.business.domain.Order} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    @Size(min = 5, max = 50)
    private String orderNumber;

    @NotNull
    private OrderStatus status;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0")
    private BigDecimal taxAmount;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalAmount;

    @Min(value = 0)
    private Integer paymentTermsDays;

    private ZonedDateTime dueDate;

    @Size(max = 1000)
    private String rejectionReason;

    @Size(max = 500)
    private String negotiationReason;

    @NotNull
    private PaymentMethod paymentMethod;

    private ZonedDateTime submittedAt;

    private ZonedDateTime validatedAt;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private ClientDTO client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPaymentTermsDays() {
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNegotiationReason() {
        return negotiationReason;
    }

    public void setNegotiationReason(String negotiationReason) {
        this.negotiationReason = negotiationReason;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ZonedDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(ZonedDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ZonedDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(ZonedDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    private Set<OrderLineDTO> orderLines;

    public Set<OrderLineDTO> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Set<OrderLineDTO> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", orderNumber='" + getOrderNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", subtotal=" + getSubtotal() +
            ", discountAmount=" + getDiscountAmount() +
            ", taxAmount=" + getTaxAmount() +
            ", totalAmount=" + getTotalAmount() +
            ", paymentTermsDays=" + getPaymentTermsDays() +
            ", dueDate='" + getDueDate() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            ", negotiationReason='" + getNegotiationReason() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", validatedAt='" + getValidatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", client=" + getClient() +
            "}";
    }
}
