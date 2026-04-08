package com.tunisales.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunisales.business.domain.enumeration.ClientStatus;
import com.tunisales.business.domain.enumeration.ClientType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Client implements Serializable {

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
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 50)
    @Column(name = "tax_id", length = 50, unique = true)
    private String taxId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @DecimalMin(value = "0")
    @Column(name = "credit_limit", precision = 21, scale = 2)
    private BigDecimal creditLimit;

    @DecimalMin(value = "0")
    @Column(name = "credit_used", precision = 21, scale = 2)
    private BigDecimal creditUsed;

    @Min(value = 0)
    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClientStatus status;

    @Column(name = "last_order_at")
    private ZonedDateTime lastOrderAt;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties(value = { "client" }, allowSetters = true)
    private Set<ClientContact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties(value = { "product", "client" }, allowSetters = true)
    private Set<PriceList> priceLists = new HashSet<>();

    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties(value = { "orderLines", "deliveries", "invoices", "client" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Client id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public Client tenantId(Long tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return this.name;
    }

    public Client name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxId() {
        return this.taxId;
    }

    public Client taxId(String taxId) {
        this.setTaxId(taxId);
        return this;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public ClientType getClientType() {
        return this.clientType;
    }

    public Client clientType(ClientType clientType) {
        this.setClientType(clientType);
        return this;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public BigDecimal getCreditLimit() {
        return this.creditLimit;
    }

    public Client creditLimit(BigDecimal creditLimit) {
        this.setCreditLimit(creditLimit);
        return this;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCreditUsed() {
        return this.creditUsed;
    }

    public Client creditUsed(BigDecimal creditUsed) {
        this.setCreditUsed(creditUsed);
        return this;
    }

    public void setCreditUsed(BigDecimal creditUsed) {
        this.creditUsed = creditUsed;
    }

    public Integer getPaymentTermsDays() {
        return this.paymentTermsDays;
    }

    public Client paymentTermsDays(Integer paymentTermsDays) {
        this.setPaymentTermsDays(paymentTermsDays);
        return this;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }

    public ClientStatus getStatus() {
        return this.status;
    }

    public Client status(ClientStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public ZonedDateTime getLastOrderAt() {
        return this.lastOrderAt;
    }

    public Client lastOrderAt(ZonedDateTime lastOrderAt) {
        this.setLastOrderAt(lastOrderAt);
        return this;
    }

    public void setLastOrderAt(ZonedDateTime lastOrderAt) {
        this.lastOrderAt = lastOrderAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Client isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Client createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Client updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ClientContact> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<ClientContact> clientContacts) {
        if (this.contacts != null) {
            this.contacts.forEach(i -> i.setClient(null));
        }
        if (clientContacts != null) {
            clientContacts.forEach(i -> i.setClient(this));
        }
        this.contacts = clientContacts;
    }

    public Client contacts(Set<ClientContact> clientContacts) {
        this.setContacts(clientContacts);
        return this;
    }

    public Client addContacts(ClientContact clientContact) {
        this.contacts.add(clientContact);
        clientContact.setClient(this);
        return this;
    }

    public Client removeContacts(ClientContact clientContact) {
        this.contacts.remove(clientContact);
        clientContact.setClient(null);
        return this;
    }

    public Set<PriceList> getPriceLists() {
        return this.priceLists;
    }

    public void setPriceLists(Set<PriceList> priceLists) {
        if (this.priceLists != null) {
            this.priceLists.forEach(i -> i.setClient(null));
        }
        if (priceLists != null) {
            priceLists.forEach(i -> i.setClient(this));
        }
        this.priceLists = priceLists;
    }

    public Client priceLists(Set<PriceList> priceLists) {
        this.setPriceLists(priceLists);
        return this;
    }

    public Client addPriceLists(PriceList priceList) {
        this.priceLists.add(priceList);
        priceList.setClient(this);
        return this;
    }

    public Client removePriceLists(PriceList priceList) {
        this.priceLists.remove(priceList);
        priceList.setClient(null);
        return this;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setClient(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setClient(this));
        }
        this.orders = orders;
    }

    public Client orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Client addOrders(Order order) {
        this.orders.add(order);
        order.setClient(this);
        return this;
    }

    public Client removeOrders(Order order) {
        this.orders.remove(order);
        order.setClient(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", tenantId=" + getTenantId() +
            ", name='" + getName() + "'" +
            ", taxId='" + getTaxId() + "'" +
            ", clientType='" + getClientType() + "'" +
            ", creditLimit=" + getCreditLimit() +
            ", creditUsed=" + getCreditUsed() +
            ", paymentTermsDays=" + getPaymentTermsDays() +
            ", status='" + getStatus() + "'" +
            ", lastOrderAt='" + getLastOrderAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
