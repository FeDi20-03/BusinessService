package com.tunisales.business.web.rest;

import static com.tunisales.business.web.rest.TestUtil.sameInstant;
import static com.tunisales.business.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.ClientContact;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.PriceList;
import com.tunisales.business.domain.enumeration.ClientStatus;
import com.tunisales.business.domain.enumeration.ClientType;
import com.tunisales.business.repository.ClientRepository;
import com.tunisales.business.service.criteria.ClientCriteria;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.mapper.ClientMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TAX_ID = "AAAAAAAAAA";
    private static final String UPDATED_TAX_ID = "BBBBBBBBBB";

    private static final ClientType DEFAULT_CLIENT_TYPE = ClientType.NATIONAL_DISTRIBUTOR;
    private static final ClientType UPDATED_CLIENT_TYPE = ClientType.REGIONAL_WHOLESALER;

    private static final BigDecimal DEFAULT_CREDIT_LIMIT = new BigDecimal(0);
    private static final BigDecimal UPDATED_CREDIT_LIMIT = new BigDecimal(1);
    private static final BigDecimal SMALLER_CREDIT_LIMIT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_CREDIT_USED = new BigDecimal(0);
    private static final BigDecimal UPDATED_CREDIT_USED = new BigDecimal(1);
    private static final BigDecimal SMALLER_CREDIT_USED = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_PAYMENT_TERMS_DAYS = 0;
    private static final Integer UPDATED_PAYMENT_TERMS_DAYS = 1;
    private static final Integer SMALLER_PAYMENT_TERMS_DAYS = 0 - 1;

    private static final ClientStatus DEFAULT_STATUS = ClientStatus.ACTIVE;
    private static final ClientStatus UPDATED_STATUS = ClientStatus.INACTIVE;

    private static final ZonedDateTime DEFAULT_LAST_ORDER_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_ORDER_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_ORDER_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity(EntityManager em) {
        Client client = new Client()
            .tenantId(DEFAULT_TENANT_ID)
            .name(DEFAULT_NAME)
            .taxId(DEFAULT_TAX_ID)
            .clientType(DEFAULT_CLIENT_TYPE)
            .creditLimit(DEFAULT_CREDIT_LIMIT)
            .creditUsed(DEFAULT_CREDIT_USED)
            .paymentTermsDays(DEFAULT_PAYMENT_TERMS_DAYS)
            .status(DEFAULT_STATUS)
            .lastOrderAt(DEFAULT_LAST_ORDER_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return client;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity(EntityManager em) {
        Client client = new Client()
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .taxId(UPDATED_TAX_ID)
            .clientType(UPDATED_CLIENT_TYPE)
            .creditLimit(UPDATED_CREDIT_LIMIT)
            .creditUsed(UPDATED_CREDIT_USED)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .status(UPDATED_STATUS)
            .lastOrderAt(UPDATED_LAST_ORDER_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return client;
    }

    @BeforeEach
    public void initTest() {
        client = createEntity(em);
    }

    @Test
    @Transactional
    void createClient() throws Exception {
        int databaseSizeBeforeCreate = clientRepository.findAll().size();
        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);
        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isCreated());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate + 1);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testClient.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClient.getTaxId()).isEqualTo(DEFAULT_TAX_ID);
        assertThat(testClient.getClientType()).isEqualTo(DEFAULT_CLIENT_TYPE);
        assertThat(testClient.getCreditLimit()).isEqualByComparingTo(DEFAULT_CREDIT_LIMIT);
        assertThat(testClient.getCreditUsed()).isEqualByComparingTo(DEFAULT_CREDIT_USED);
        assertThat(testClient.getPaymentTermsDays()).isEqualTo(DEFAULT_PAYMENT_TERMS_DAYS);
        assertThat(testClient.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testClient.getLastOrderAt()).isEqualTo(DEFAULT_LAST_ORDER_AT);
        assertThat(testClient.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testClient.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testClient.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createClientWithExistingId() throws Exception {
        // Create the Client with an existing ID
        client.setId(1L);
        ClientDTO clientDTO = clientMapper.toDto(client);

        int databaseSizeBeforeCreate = clientRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setTenantId(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setName(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkClientTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setClientType(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setStatus(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setIsDeleted(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setCreatedAt(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].taxId").value(hasItem(DEFAULT_TAX_ID)))
            .andExpect(jsonPath("$.[*].clientType").value(hasItem(DEFAULT_CLIENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].creditLimit").value(hasItem(sameNumber(DEFAULT_CREDIT_LIMIT))))
            .andExpect(jsonPath("$.[*].creditUsed").value(hasItem(sameNumber(DEFAULT_CREDIT_USED))))
            .andExpect(jsonPath("$.[*].paymentTermsDays").value(hasItem(DEFAULT_PAYMENT_TERMS_DAYS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastOrderAt").value(hasItem(sameInstant(DEFAULT_LAST_ORDER_AT))))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc
            .perform(get(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.taxId").value(DEFAULT_TAX_ID))
            .andExpect(jsonPath("$.clientType").value(DEFAULT_CLIENT_TYPE.toString()))
            .andExpect(jsonPath("$.creditLimit").value(sameNumber(DEFAULT_CREDIT_LIMIT)))
            .andExpect(jsonPath("$.creditUsed").value(sameNumber(DEFAULT_CREDIT_USED)))
            .andExpect(jsonPath("$.paymentTermsDays").value(DEFAULT_PAYMENT_TERMS_DAYS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastOrderAt").value(sameInstant(DEFAULT_LAST_ORDER_AT)))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getClientsByIdFiltering() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        Long id = client.getId();

        defaultClientShouldBeFound("id.equals=" + id);
        defaultClientShouldNotBeFound("id.notEquals=" + id);

        defaultClientShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClientShouldNotBeFound("id.greaterThan=" + id);

        defaultClientShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClientShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId equals to DEFAULT_TENANT_ID
        defaultClientShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the clientList where tenantId equals to UPDATED_TENANT_ID
        defaultClientShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultClientShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the clientList where tenantId equals to UPDATED_TENANT_ID
        defaultClientShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId is not null
        defaultClientShouldBeFound("tenantId.specified=true");

        // Get all the clientList where tenantId is null
        defaultClientShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultClientShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the clientList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultClientShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultClientShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the clientList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultClientShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId is less than DEFAULT_TENANT_ID
        defaultClientShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the clientList where tenantId is less than UPDATED_TENANT_ID
        defaultClientShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where tenantId is greater than DEFAULT_TENANT_ID
        defaultClientShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the clientList where tenantId is greater than SMALLER_TENANT_ID
        defaultClientShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllClientsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where name equals to DEFAULT_NAME
        defaultClientShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the clientList where name equals to UPDATED_NAME
        defaultClientShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where name in DEFAULT_NAME or UPDATED_NAME
        defaultClientShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the clientList where name equals to UPDATED_NAME
        defaultClientShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where name is not null
        defaultClientShouldBeFound("name.specified=true");

        // Get all the clientList where name is null
        defaultClientShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByNameContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where name contains DEFAULT_NAME
        defaultClientShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the clientList where name contains UPDATED_NAME
        defaultClientShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where name does not contain DEFAULT_NAME
        defaultClientShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the clientList where name does not contain UPDATED_NAME
        defaultClientShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByTaxIdIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where taxId equals to DEFAULT_TAX_ID
        defaultClientShouldBeFound("taxId.equals=" + DEFAULT_TAX_ID);

        // Get all the clientList where taxId equals to UPDATED_TAX_ID
        defaultClientShouldNotBeFound("taxId.equals=" + UPDATED_TAX_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTaxIdIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where taxId in DEFAULT_TAX_ID or UPDATED_TAX_ID
        defaultClientShouldBeFound("taxId.in=" + DEFAULT_TAX_ID + "," + UPDATED_TAX_ID);

        // Get all the clientList where taxId equals to UPDATED_TAX_ID
        defaultClientShouldNotBeFound("taxId.in=" + UPDATED_TAX_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTaxIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where taxId is not null
        defaultClientShouldBeFound("taxId.specified=true");

        // Get all the clientList where taxId is null
        defaultClientShouldNotBeFound("taxId.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByTaxIdContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where taxId contains DEFAULT_TAX_ID
        defaultClientShouldBeFound("taxId.contains=" + DEFAULT_TAX_ID);

        // Get all the clientList where taxId contains UPDATED_TAX_ID
        defaultClientShouldNotBeFound("taxId.contains=" + UPDATED_TAX_ID);
    }

    @Test
    @Transactional
    void getAllClientsByTaxIdNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where taxId does not contain DEFAULT_TAX_ID
        defaultClientShouldNotBeFound("taxId.doesNotContain=" + DEFAULT_TAX_ID);

        // Get all the clientList where taxId does not contain UPDATED_TAX_ID
        defaultClientShouldBeFound("taxId.doesNotContain=" + UPDATED_TAX_ID);
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where clientType equals to DEFAULT_CLIENT_TYPE
        defaultClientShouldBeFound("clientType.equals=" + DEFAULT_CLIENT_TYPE);

        // Get all the clientList where clientType equals to UPDATED_CLIENT_TYPE
        defaultClientShouldNotBeFound("clientType.equals=" + UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where clientType in DEFAULT_CLIENT_TYPE or UPDATED_CLIENT_TYPE
        defaultClientShouldBeFound("clientType.in=" + DEFAULT_CLIENT_TYPE + "," + UPDATED_CLIENT_TYPE);

        // Get all the clientList where clientType equals to UPDATED_CLIENT_TYPE
        defaultClientShouldNotBeFound("clientType.in=" + UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where clientType is not null
        defaultClientShouldBeFound("clientType.specified=true");

        // Get all the clientList where clientType is null
        defaultClientShouldNotBeFound("clientType.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit equals to DEFAULT_CREDIT_LIMIT
        defaultClientShouldBeFound("creditLimit.equals=" + DEFAULT_CREDIT_LIMIT);

        // Get all the clientList where creditLimit equals to UPDATED_CREDIT_LIMIT
        defaultClientShouldNotBeFound("creditLimit.equals=" + UPDATED_CREDIT_LIMIT);
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit in DEFAULT_CREDIT_LIMIT or UPDATED_CREDIT_LIMIT
        defaultClientShouldBeFound("creditLimit.in=" + DEFAULT_CREDIT_LIMIT + "," + UPDATED_CREDIT_LIMIT);

        // Get all the clientList where creditLimit equals to UPDATED_CREDIT_LIMIT
        defaultClientShouldNotBeFound("creditLimit.in=" + UPDATED_CREDIT_LIMIT);
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit is not null
        defaultClientShouldBeFound("creditLimit.specified=true");

        // Get all the clientList where creditLimit is null
        defaultClientShouldNotBeFound("creditLimit.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit is greater than or equal to DEFAULT_CREDIT_LIMIT
        defaultClientShouldBeFound("creditLimit.greaterThanOrEqual=" + DEFAULT_CREDIT_LIMIT);

        // Get all the clientList where creditLimit is greater than or equal to UPDATED_CREDIT_LIMIT
        defaultClientShouldNotBeFound("creditLimit.greaterThanOrEqual=" + UPDATED_CREDIT_LIMIT);
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit is less than or equal to DEFAULT_CREDIT_LIMIT
        defaultClientShouldBeFound("creditLimit.lessThanOrEqual=" + DEFAULT_CREDIT_LIMIT);

        // Get all the clientList where creditLimit is less than or equal to SMALLER_CREDIT_LIMIT
        defaultClientShouldNotBeFound("creditLimit.lessThanOrEqual=" + SMALLER_CREDIT_LIMIT);
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit is less than DEFAULT_CREDIT_LIMIT
        defaultClientShouldNotBeFound("creditLimit.lessThan=" + DEFAULT_CREDIT_LIMIT);

        // Get all the clientList where creditLimit is less than UPDATED_CREDIT_LIMIT
        defaultClientShouldBeFound("creditLimit.lessThan=" + UPDATED_CREDIT_LIMIT);
    }

    @Test
    @Transactional
    void getAllClientsByCreditLimitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditLimit is greater than DEFAULT_CREDIT_LIMIT
        defaultClientShouldNotBeFound("creditLimit.greaterThan=" + DEFAULT_CREDIT_LIMIT);

        // Get all the clientList where creditLimit is greater than SMALLER_CREDIT_LIMIT
        defaultClientShouldBeFound("creditLimit.greaterThan=" + SMALLER_CREDIT_LIMIT);
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed equals to DEFAULT_CREDIT_USED
        defaultClientShouldBeFound("creditUsed.equals=" + DEFAULT_CREDIT_USED);

        // Get all the clientList where creditUsed equals to UPDATED_CREDIT_USED
        defaultClientShouldNotBeFound("creditUsed.equals=" + UPDATED_CREDIT_USED);
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed in DEFAULT_CREDIT_USED or UPDATED_CREDIT_USED
        defaultClientShouldBeFound("creditUsed.in=" + DEFAULT_CREDIT_USED + "," + UPDATED_CREDIT_USED);

        // Get all the clientList where creditUsed equals to UPDATED_CREDIT_USED
        defaultClientShouldNotBeFound("creditUsed.in=" + UPDATED_CREDIT_USED);
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed is not null
        defaultClientShouldBeFound("creditUsed.specified=true");

        // Get all the clientList where creditUsed is null
        defaultClientShouldNotBeFound("creditUsed.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed is greater than or equal to DEFAULT_CREDIT_USED
        defaultClientShouldBeFound("creditUsed.greaterThanOrEqual=" + DEFAULT_CREDIT_USED);

        // Get all the clientList where creditUsed is greater than or equal to UPDATED_CREDIT_USED
        defaultClientShouldNotBeFound("creditUsed.greaterThanOrEqual=" + UPDATED_CREDIT_USED);
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed is less than or equal to DEFAULT_CREDIT_USED
        defaultClientShouldBeFound("creditUsed.lessThanOrEqual=" + DEFAULT_CREDIT_USED);

        // Get all the clientList where creditUsed is less than or equal to SMALLER_CREDIT_USED
        defaultClientShouldNotBeFound("creditUsed.lessThanOrEqual=" + SMALLER_CREDIT_USED);
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed is less than DEFAULT_CREDIT_USED
        defaultClientShouldNotBeFound("creditUsed.lessThan=" + DEFAULT_CREDIT_USED);

        // Get all the clientList where creditUsed is less than UPDATED_CREDIT_USED
        defaultClientShouldBeFound("creditUsed.lessThan=" + UPDATED_CREDIT_USED);
    }

    @Test
    @Transactional
    void getAllClientsByCreditUsedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where creditUsed is greater than DEFAULT_CREDIT_USED
        defaultClientShouldNotBeFound("creditUsed.greaterThan=" + DEFAULT_CREDIT_USED);

        // Get all the clientList where creditUsed is greater than SMALLER_CREDIT_USED
        defaultClientShouldBeFound("creditUsed.greaterThan=" + SMALLER_CREDIT_USED);
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays equals to DEFAULT_PAYMENT_TERMS_DAYS
        defaultClientShouldBeFound("paymentTermsDays.equals=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the clientList where paymentTermsDays equals to UPDATED_PAYMENT_TERMS_DAYS
        defaultClientShouldNotBeFound("paymentTermsDays.equals=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays in DEFAULT_PAYMENT_TERMS_DAYS or UPDATED_PAYMENT_TERMS_DAYS
        defaultClientShouldBeFound("paymentTermsDays.in=" + DEFAULT_PAYMENT_TERMS_DAYS + "," + UPDATED_PAYMENT_TERMS_DAYS);

        // Get all the clientList where paymentTermsDays equals to UPDATED_PAYMENT_TERMS_DAYS
        defaultClientShouldNotBeFound("paymentTermsDays.in=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays is not null
        defaultClientShouldBeFound("paymentTermsDays.specified=true");

        // Get all the clientList where paymentTermsDays is null
        defaultClientShouldNotBeFound("paymentTermsDays.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays is greater than or equal to DEFAULT_PAYMENT_TERMS_DAYS
        defaultClientShouldBeFound("paymentTermsDays.greaterThanOrEqual=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the clientList where paymentTermsDays is greater than or equal to UPDATED_PAYMENT_TERMS_DAYS
        defaultClientShouldNotBeFound("paymentTermsDays.greaterThanOrEqual=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays is less than or equal to DEFAULT_PAYMENT_TERMS_DAYS
        defaultClientShouldBeFound("paymentTermsDays.lessThanOrEqual=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the clientList where paymentTermsDays is less than or equal to SMALLER_PAYMENT_TERMS_DAYS
        defaultClientShouldNotBeFound("paymentTermsDays.lessThanOrEqual=" + SMALLER_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays is less than DEFAULT_PAYMENT_TERMS_DAYS
        defaultClientShouldNotBeFound("paymentTermsDays.lessThan=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the clientList where paymentTermsDays is less than UPDATED_PAYMENT_TERMS_DAYS
        defaultClientShouldBeFound("paymentTermsDays.lessThan=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllClientsByPaymentTermsDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where paymentTermsDays is greater than DEFAULT_PAYMENT_TERMS_DAYS
        defaultClientShouldNotBeFound("paymentTermsDays.greaterThan=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the clientList where paymentTermsDays is greater than SMALLER_PAYMENT_TERMS_DAYS
        defaultClientShouldBeFound("paymentTermsDays.greaterThan=" + SMALLER_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllClientsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where status equals to DEFAULT_STATUS
        defaultClientShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the clientList where status equals to UPDATED_STATUS
        defaultClientShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClientsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultClientShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the clientList where status equals to UPDATED_STATUS
        defaultClientShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllClientsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where status is not null
        defaultClientShouldBeFound("status.specified=true");

        // Get all the clientList where status is null
        defaultClientShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt equals to DEFAULT_LAST_ORDER_AT
        defaultClientShouldBeFound("lastOrderAt.equals=" + DEFAULT_LAST_ORDER_AT);

        // Get all the clientList where lastOrderAt equals to UPDATED_LAST_ORDER_AT
        defaultClientShouldNotBeFound("lastOrderAt.equals=" + UPDATED_LAST_ORDER_AT);
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt in DEFAULT_LAST_ORDER_AT or UPDATED_LAST_ORDER_AT
        defaultClientShouldBeFound("lastOrderAt.in=" + DEFAULT_LAST_ORDER_AT + "," + UPDATED_LAST_ORDER_AT);

        // Get all the clientList where lastOrderAt equals to UPDATED_LAST_ORDER_AT
        defaultClientShouldNotBeFound("lastOrderAt.in=" + UPDATED_LAST_ORDER_AT);
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt is not null
        defaultClientShouldBeFound("lastOrderAt.specified=true");

        // Get all the clientList where lastOrderAt is null
        defaultClientShouldNotBeFound("lastOrderAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt is greater than or equal to DEFAULT_LAST_ORDER_AT
        defaultClientShouldBeFound("lastOrderAt.greaterThanOrEqual=" + DEFAULT_LAST_ORDER_AT);

        // Get all the clientList where lastOrderAt is greater than or equal to UPDATED_LAST_ORDER_AT
        defaultClientShouldNotBeFound("lastOrderAt.greaterThanOrEqual=" + UPDATED_LAST_ORDER_AT);
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt is less than or equal to DEFAULT_LAST_ORDER_AT
        defaultClientShouldBeFound("lastOrderAt.lessThanOrEqual=" + DEFAULT_LAST_ORDER_AT);

        // Get all the clientList where lastOrderAt is less than or equal to SMALLER_LAST_ORDER_AT
        defaultClientShouldNotBeFound("lastOrderAt.lessThanOrEqual=" + SMALLER_LAST_ORDER_AT);
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt is less than DEFAULT_LAST_ORDER_AT
        defaultClientShouldNotBeFound("lastOrderAt.lessThan=" + DEFAULT_LAST_ORDER_AT);

        // Get all the clientList where lastOrderAt is less than UPDATED_LAST_ORDER_AT
        defaultClientShouldBeFound("lastOrderAt.lessThan=" + UPDATED_LAST_ORDER_AT);
    }

    @Test
    @Transactional
    void getAllClientsByLastOrderAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where lastOrderAt is greater than DEFAULT_LAST_ORDER_AT
        defaultClientShouldNotBeFound("lastOrderAt.greaterThan=" + DEFAULT_LAST_ORDER_AT);

        // Get all the clientList where lastOrderAt is greater than SMALLER_LAST_ORDER_AT
        defaultClientShouldBeFound("lastOrderAt.greaterThan=" + SMALLER_LAST_ORDER_AT);
    }

    @Test
    @Transactional
    void getAllClientsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isDeleted equals to DEFAULT_IS_DELETED
        defaultClientShouldBeFound("isDeleted.equals=" + DEFAULT_IS_DELETED);

        // Get all the clientList where isDeleted equals to UPDATED_IS_DELETED
        defaultClientShouldNotBeFound("isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllClientsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isDeleted in DEFAULT_IS_DELETED or UPDATED_IS_DELETED
        defaultClientShouldBeFound("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED);

        // Get all the clientList where isDeleted equals to UPDATED_IS_DELETED
        defaultClientShouldNotBeFound("isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllClientsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isDeleted is not null
        defaultClientShouldBeFound("isDeleted.specified=true");

        // Get all the clientList where isDeleted is null
        defaultClientShouldNotBeFound("isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt equals to DEFAULT_CREATED_AT
        defaultClientShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the clientList where createdAt equals to UPDATED_CREATED_AT
        defaultClientShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultClientShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the clientList where createdAt equals to UPDATED_CREATED_AT
        defaultClientShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt is not null
        defaultClientShouldBeFound("createdAt.specified=true");

        // Get all the clientList where createdAt is null
        defaultClientShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultClientShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the clientList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultClientShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultClientShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the clientList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultClientShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt is less than DEFAULT_CREATED_AT
        defaultClientShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the clientList where createdAt is less than UPDATED_CREATED_AT
        defaultClientShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where createdAt is greater than DEFAULT_CREATED_AT
        defaultClientShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the clientList where createdAt is greater than SMALLER_CREATED_AT
        defaultClientShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultClientShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the clientList where updatedAt equals to UPDATED_UPDATED_AT
        defaultClientShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultClientShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the clientList where updatedAt equals to UPDATED_UPDATED_AT
        defaultClientShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt is not null
        defaultClientShouldBeFound("updatedAt.specified=true");

        // Get all the clientList where updatedAt is null
        defaultClientShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultClientShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the clientList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultClientShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultClientShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the clientList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultClientShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultClientShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the clientList where updatedAt is less than UPDATED_UPDATED_AT
        defaultClientShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultClientShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the clientList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultClientShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllClientsByContactsIsEqualToSomething() throws Exception {
        ClientContact contacts;
        if (TestUtil.findAll(em, ClientContact.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            contacts = ClientContactResourceIT.createEntity(em);
        } else {
            contacts = TestUtil.findAll(em, ClientContact.class).get(0);
        }
        em.persist(contacts);
        em.flush();
        client.addContacts(contacts);
        clientRepository.saveAndFlush(client);
        Long contactsId = contacts.getId();

        // Get all the clientList where contacts equals to contactsId
        defaultClientShouldBeFound("contactsId.equals=" + contactsId);

        // Get all the clientList where contacts equals to (contactsId + 1)
        defaultClientShouldNotBeFound("contactsId.equals=" + (contactsId + 1));
    }

    @Test
    @Transactional
    void getAllClientsByPriceListsIsEqualToSomething() throws Exception {
        PriceList priceLists;
        if (TestUtil.findAll(em, PriceList.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            priceLists = PriceListResourceIT.createEntity(em);
        } else {
            priceLists = TestUtil.findAll(em, PriceList.class).get(0);
        }
        em.persist(priceLists);
        em.flush();
        client.addPriceLists(priceLists);
        clientRepository.saveAndFlush(client);
        Long priceListsId = priceLists.getId();

        // Get all the clientList where priceLists equals to priceListsId
        defaultClientShouldBeFound("priceListsId.equals=" + priceListsId);

        // Get all the clientList where priceLists equals to (priceListsId + 1)
        defaultClientShouldNotBeFound("priceListsId.equals=" + (priceListsId + 1));
    }

    @Test
    @Transactional
    void getAllClientsByOrdersIsEqualToSomething() throws Exception {
        Order orders;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            orders = OrderResourceIT.createEntity(em);
        } else {
            orders = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(orders);
        em.flush();
        client.addOrders(orders);
        clientRepository.saveAndFlush(client);
        Long ordersId = orders.getId();

        // Get all the clientList where orders equals to ordersId
        defaultClientShouldBeFound("ordersId.equals=" + ordersId);

        // Get all the clientList where orders equals to (ordersId + 1)
        defaultClientShouldNotBeFound("ordersId.equals=" + (ordersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientShouldBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].taxId").value(hasItem(DEFAULT_TAX_ID)))
            .andExpect(jsonPath("$.[*].clientType").value(hasItem(DEFAULT_CLIENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].creditLimit").value(hasItem(sameNumber(DEFAULT_CREDIT_LIMIT))))
            .andExpect(jsonPath("$.[*].creditUsed").value(hasItem(sameNumber(DEFAULT_CREDIT_USED))))
            .andExpect(jsonPath("$.[*].paymentTermsDays").value(hasItem(DEFAULT_PAYMENT_TERMS_DAYS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastOrderAt").value(hasItem(sameInstant(DEFAULT_LAST_ORDER_AT))))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientShouldNotBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).get();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .taxId(UPDATED_TAX_ID)
            .clientType(UPDATED_CLIENT_TYPE)
            .creditLimit(UPDATED_CREDIT_LIMIT)
            .creditUsed(UPDATED_CREDIT_USED)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .status(UPDATED_STATUS)
            .lastOrderAt(UPDATED_LAST_ORDER_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ClientDTO clientDTO = clientMapper.toDto(updatedClient);

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testClient.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClient.getTaxId()).isEqualTo(UPDATED_TAX_ID);
        assertThat(testClient.getClientType()).isEqualTo(UPDATED_CLIENT_TYPE);
        assertThat(testClient.getCreditLimit()).isEqualByComparingTo(UPDATED_CREDIT_LIMIT);
        assertThat(testClient.getCreditUsed()).isEqualByComparingTo(UPDATED_CREDIT_USED);
        assertThat(testClient.getPaymentTermsDays()).isEqualTo(UPDATED_PAYMENT_TERMS_DAYS);
        assertThat(testClient.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testClient.getLastOrderAt()).isEqualTo(UPDATED_LAST_ORDER_AT);
        assertThat(testClient.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testClient.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testClient.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientWithPatch() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .taxId(UPDATED_TAX_ID)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .lastOrderAt(UPDATED_LAST_ORDER_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testClient.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClient.getTaxId()).isEqualTo(UPDATED_TAX_ID);
        assertThat(testClient.getClientType()).isEqualTo(DEFAULT_CLIENT_TYPE);
        assertThat(testClient.getCreditLimit()).isEqualByComparingTo(DEFAULT_CREDIT_LIMIT);
        assertThat(testClient.getCreditUsed()).isEqualByComparingTo(DEFAULT_CREDIT_USED);
        assertThat(testClient.getPaymentTermsDays()).isEqualTo(UPDATED_PAYMENT_TERMS_DAYS);
        assertThat(testClient.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testClient.getLastOrderAt()).isEqualTo(UPDATED_LAST_ORDER_AT);
        assertThat(testClient.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testClient.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testClient.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateClientWithPatch() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .taxId(UPDATED_TAX_ID)
            .clientType(UPDATED_CLIENT_TYPE)
            .creditLimit(UPDATED_CREDIT_LIMIT)
            .creditUsed(UPDATED_CREDIT_USED)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .status(UPDATED_STATUS)
            .lastOrderAt(UPDATED_LAST_ORDER_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testClient.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClient.getTaxId()).isEqualTo(UPDATED_TAX_ID);
        assertThat(testClient.getClientType()).isEqualTo(UPDATED_CLIENT_TYPE);
        assertThat(testClient.getCreditLimit()).isEqualByComparingTo(UPDATED_CREDIT_LIMIT);
        assertThat(testClient.getCreditUsed()).isEqualByComparingTo(UPDATED_CREDIT_USED);
        assertThat(testClient.getPaymentTermsDays()).isEqualTo(UPDATED_PAYMENT_TERMS_DAYS);
        assertThat(testClient.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testClient.getLastOrderAt()).isEqualTo(UPDATED_LAST_ORDER_AT);
        assertThat(testClient.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testClient.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testClient.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeDelete = clientRepository.findAll().size();

        // Delete the client
        restClientMockMvc
            .perform(delete(ENTITY_API_URL_ID, client.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
