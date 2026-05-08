package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A InvoiceLine.
 *
 * <p>Sub-step 2.4 — one row per validated {@link OrderLine} at the time the
 * invoice is issued. {@code unitPriceAtPurchase} captures the unit price at
 * the moment the order was validated, so that downstream sales-returns
 * refund the price actually charged (not the current product price).</p>
 */
@Entity
@Table(name = "invoice_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "unit_price_at_purchase", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPriceAtPurchase;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "line_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "client", "order" }, allowSetters = true)
    private Invoice invoice;

    @ManyToOne(optional = false)
    @NotNull
    private Product product;

    public Long getId() {
        return this.id;
    }

    public InvoiceLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public InvoiceLine tenantId(Long tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public InvoiceLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceAtPurchase() {
        return this.unitPriceAtPurchase;
    }

    public InvoiceLine unitPriceAtPurchase(BigDecimal unitPriceAtPurchase) {
        this.setUnitPriceAtPurchase(unitPriceAtPurchase);
        return this;
    }

    public void setUnitPriceAtPurchase(BigDecimal unitPriceAtPurchase) {
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }

    public BigDecimal getLineTotal() {
        return this.lineTotal;
    }

    public InvoiceLine lineTotal(BigDecimal lineTotal) {
        this.setLineTotal(lineTotal);
        return this;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public InvoiceLine createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public InvoiceLine invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public InvoiceLine product(Product product) {
        this.setProduct(product);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceLine)) {
            return false;
        }
        return id != null && id.equals(((InvoiceLine) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "InvoiceLine{" +
            "id=" +
            getId() +
            ", tenantId=" +
            getTenantId() +
            ", quantity=" +
            getQuantity() +
            ", unitPriceAtPurchase=" +
            getUnitPriceAtPurchase() +
            ", lineTotal=" +
            getLineTotal() +
            ", createdAt='" +
            getCreatedAt() +
            "'" +
            "}"
        );
    }
}
