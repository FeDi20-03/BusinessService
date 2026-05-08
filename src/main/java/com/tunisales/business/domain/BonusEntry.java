package com.tunisales.business.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Sub-step 2.9 — one bonus entry credited to a seller for a given order line.
 *
 * <p>Recorded by {@code BonusCalculator} after an order moves to {@code VALIDATED}.
 * Aggregations for the seller's monthly bonus are computed from these rows.</p>
 */
@Entity
@Table(name = "bonus_entry")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BonusEntry implements Serializable {

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
    @Size(max = 100)
    @Column(name = "vendeur_login", length = 100, nullable = false)
    private String vendeurLogin;

    @NotNull
    @Column(name = "order_line_id", nullable = false)
    private Long orderLineId;

    @Column(name = "bonus_rule_id")
    private Long bonusRuleId;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "computed_at", nullable = false)
    private ZonedDateTime computedAt;

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

    public String getVendeurLogin() {
        return vendeurLogin;
    }

    public void setVendeurLogin(String vendeurLogin) {
        this.vendeurLogin = vendeurLogin;
    }

    public Long getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(Long orderLineId) {
        this.orderLineId = orderLineId;
    }

    public Long getBonusRuleId() {
        return bonusRuleId;
    }

    public void setBonusRuleId(Long bonusRuleId) {
        this.bonusRuleId = bonusRuleId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ZonedDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(ZonedDateTime computedAt) {
        this.computedAt = computedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BonusEntry)) return false;
        return id != null && id.equals(((BonusEntry) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
