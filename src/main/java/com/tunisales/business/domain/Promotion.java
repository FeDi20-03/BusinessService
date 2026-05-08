package com.tunisales.business.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.9 — time-bounded promotional discount on a product.
 *
 * <p>{@code conditionsJson} is a free-form text payload; consumers can store
 * structured rules (min quantity, client type, etc.) without requiring a
 * schema migration each time a new condition is introduced.</p>
 */
@Entity
@Table(name = "promotion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Promotion implements Serializable {

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
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "discount_pct", precision = 21, scale = 2, nullable = false)
    private BigDecimal discountPct;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Lob
    @Column(name = "conditions_json")
    private String conditionsJson;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getDiscountPct() {
        return discountPct;
    }

    public void setDiscountPct(BigDecimal discountPct) {
        this.discountPct = discountPct;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getConditionsJson() {
        return conditionsJson;
    }

    public void setConditionsJson(String conditionsJson) {
        this.conditionsJson = conditionsJson;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Promotion)) return false;
        return id != null && id.equals(((Promotion) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
