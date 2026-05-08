package com.tunisales.business.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/** Sub-step 2.9 — DTO for {@link com.tunisales.business.domain.BonusEntry}. */
public class BonusEntryDTO implements Serializable {

    private Long id;

    @NotNull
    private Long tenantId;

    @NotNull
    @Size(max = 100)
    private String vendeurLogin;

    @NotNull
    private Long orderLineId;

    private Long bonusRuleId;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @NotNull
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
        if (!(o instanceof BonusEntryDTO)) return false;
        BonusEntryDTO that = (BonusEntryDTO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
