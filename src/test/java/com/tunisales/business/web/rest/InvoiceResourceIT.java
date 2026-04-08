package com.tunisales.business.web.rest;

import static com.tunisales.business.web.rest.TestUtil.sameInstant;
import static com.tunisales.business.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Invoice;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.enumeration.InvoiceStatus;
import com.tunisales.business.repository.InvoiceRepository;
import com.tunisales.business.service.InvoiceService;
import com.tunisales.business.service.criteria.InvoiceCriteria;
import com.tunisales.business.service.dto.InvoiceDTO;
import com.tunisales.business.service.mapper.InvoiceMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InvoiceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InvoiceResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final String DEFAULT_INVOICE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NUMBER = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT_HT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT_HT = new BigDecimal(1);
    private static final BigDecimal SMALLER_AMOUNT_HT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TAX_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_AMOUNT_TTC = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT_TTC = new BigDecimal(1);
    private static final BigDecimal SMALLER_AMOUNT_TTC = new BigDecimal(0 - 1);

    private static final InvoiceStatus DEFAULT_STATUS = InvoiceStatus.DRAFT;
    private static final InvoiceStatus UPDATED_STATUS = InvoiceStatus.ISSUED;

    private static final ZonedDateTime DEFAULT_ISSUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ISSUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ISSUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_PAID_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PAID_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_PAID_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/invoices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceRepository invoiceRepositoryMock;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Mock
    private InvoiceService invoiceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceMockMvc;

    private Invoice invoice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .tenantId(DEFAULT_TENANT_ID)
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .amountHt(DEFAULT_AMOUNT_HT)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .amountTtc(DEFAULT_AMOUNT_TTC)
            .status(DEFAULT_STATUS)
            .issueDate(DEFAULT_ISSUE_DATE)
            .dueDate(DEFAULT_DUE_DATE)
            .paidAt(DEFAULT_PAID_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        invoice.setClient(client);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        invoice.setOrder(order);
        return invoice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .tenantId(UPDATED_TENANT_ID)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .amountHt(UPDATED_AMOUNT_HT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountTtc(UPDATED_AMOUNT_TTC)
            .status(UPDATED_STATUS)
            .issueDate(UPDATED_ISSUE_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .paidAt(UPDATED_PAID_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        invoice.setClient(client);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        invoice.setOrder(order);
        return invoice;
    }

    @BeforeEach
    public void initTest() {
        invoice = createEntity(em);
    }

    @Test
    @Transactional
    void createInvoice() throws Exception {
        int databaseSizeBeforeCreate = invoiceRepository.findAll().size();
        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);
        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isCreated());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate + 1);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(DEFAULT_INVOICE_NUMBER);
        assertThat(testInvoice.getAmountHt()).isEqualByComparingTo(DEFAULT_AMOUNT_HT);
        assertThat(testInvoice.getTaxAmount()).isEqualByComparingTo(DEFAULT_TAX_AMOUNT);
        assertThat(testInvoice.getAmountTtc()).isEqualByComparingTo(DEFAULT_AMOUNT_TTC);
        assertThat(testInvoice.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testInvoice.getIssueDate()).isEqualTo(DEFAULT_ISSUE_DATE);
        assertThat(testInvoice.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testInvoice.getPaidAt()).isEqualTo(DEFAULT_PAID_AT);
        assertThat(testInvoice.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testInvoice.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testInvoice.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createInvoiceWithExistingId() throws Exception {
        // Create the Invoice with an existing ID
        invoice.setId(1L);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        int databaseSizeBeforeCreate = invoiceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setTenantId(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInvoiceNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setInvoiceNumber(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountHtIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setAmountHt(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTaxAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setTaxAmount(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountTtcIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setAmountTtc(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setStatus(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setIssueDate(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setDueDate(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setIsDeleted(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setCreatedAt(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvoices() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].amountHt").value(hasItem(sameNumber(DEFAULT_AMOUNT_HT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].amountTtc").value(hasItem(sameNumber(DEFAULT_AMOUNT_TTC))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(sameInstant(DEFAULT_ISSUE_DATE))))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].paidAt").value(hasItem(sameInstant(DEFAULT_PAID_AT))))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoicesWithEagerRelationshipsIsEnabled() throws Exception {
        when(invoiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(invoiceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoicesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(invoiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(invoiceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get the invoice
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL_ID, invoice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoice.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.invoiceNumber").value(DEFAULT_INVOICE_NUMBER))
            .andExpect(jsonPath("$.amountHt").value(sameNumber(DEFAULT_AMOUNT_HT)))
            .andExpect(jsonPath("$.taxAmount").value(sameNumber(DEFAULT_TAX_AMOUNT)))
            .andExpect(jsonPath("$.amountTtc").value(sameNumber(DEFAULT_AMOUNT_TTC)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.issueDate").value(sameInstant(DEFAULT_ISSUE_DATE)))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.paidAt").value(sameInstant(DEFAULT_PAID_AT)))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getInvoicesByIdFiltering() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        Long id = invoice.getId();

        defaultInvoiceShouldBeFound("id.equals=" + id);
        defaultInvoiceShouldNotBeFound("id.notEquals=" + id);

        defaultInvoiceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInvoiceShouldNotBeFound("id.greaterThan=" + id);

        defaultInvoiceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInvoiceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId equals to DEFAULT_TENANT_ID
        defaultInvoiceShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the invoiceList where tenantId equals to UPDATED_TENANT_ID
        defaultInvoiceShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultInvoiceShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the invoiceList where tenantId equals to UPDATED_TENANT_ID
        defaultInvoiceShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId is not null
        defaultInvoiceShouldBeFound("tenantId.specified=true");

        // Get all the invoiceList where tenantId is null
        defaultInvoiceShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultInvoiceShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the invoiceList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultInvoiceShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultInvoiceShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the invoiceList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultInvoiceShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId is less than DEFAULT_TENANT_ID
        defaultInvoiceShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the invoiceList where tenantId is less than UPDATED_TENANT_ID
        defaultInvoiceShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where tenantId is greater than DEFAULT_TENANT_ID
        defaultInvoiceShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the invoiceList where tenantId is greater than SMALLER_TENANT_ID
        defaultInvoiceShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNumber equals to DEFAULT_INVOICE_NUMBER
        defaultInvoiceShouldBeFound("invoiceNumber.equals=" + DEFAULT_INVOICE_NUMBER);

        // Get all the invoiceList where invoiceNumber equals to UPDATED_INVOICE_NUMBER
        defaultInvoiceShouldNotBeFound("invoiceNumber.equals=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNumberIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNumber in DEFAULT_INVOICE_NUMBER or UPDATED_INVOICE_NUMBER
        defaultInvoiceShouldBeFound("invoiceNumber.in=" + DEFAULT_INVOICE_NUMBER + "," + UPDATED_INVOICE_NUMBER);

        // Get all the invoiceList where invoiceNumber equals to UPDATED_INVOICE_NUMBER
        defaultInvoiceShouldNotBeFound("invoiceNumber.in=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNumber is not null
        defaultInvoiceShouldBeFound("invoiceNumber.specified=true");

        // Get all the invoiceList where invoiceNumber is null
        defaultInvoiceShouldNotBeFound("invoiceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNumberContainsSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNumber contains DEFAULT_INVOICE_NUMBER
        defaultInvoiceShouldBeFound("invoiceNumber.contains=" + DEFAULT_INVOICE_NUMBER);

        // Get all the invoiceList where invoiceNumber contains UPDATED_INVOICE_NUMBER
        defaultInvoiceShouldNotBeFound("invoiceNumber.contains=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNumberNotContainsSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNumber does not contain DEFAULT_INVOICE_NUMBER
        defaultInvoiceShouldNotBeFound("invoiceNumber.doesNotContain=" + DEFAULT_INVOICE_NUMBER);

        // Get all the invoiceList where invoiceNumber does not contain UPDATED_INVOICE_NUMBER
        defaultInvoiceShouldBeFound("invoiceNumber.doesNotContain=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt equals to DEFAULT_AMOUNT_HT
        defaultInvoiceShouldBeFound("amountHt.equals=" + DEFAULT_AMOUNT_HT);

        // Get all the invoiceList where amountHt equals to UPDATED_AMOUNT_HT
        defaultInvoiceShouldNotBeFound("amountHt.equals=" + UPDATED_AMOUNT_HT);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt in DEFAULT_AMOUNT_HT or UPDATED_AMOUNT_HT
        defaultInvoiceShouldBeFound("amountHt.in=" + DEFAULT_AMOUNT_HT + "," + UPDATED_AMOUNT_HT);

        // Get all the invoiceList where amountHt equals to UPDATED_AMOUNT_HT
        defaultInvoiceShouldNotBeFound("amountHt.in=" + UPDATED_AMOUNT_HT);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt is not null
        defaultInvoiceShouldBeFound("amountHt.specified=true");

        // Get all the invoiceList where amountHt is null
        defaultInvoiceShouldNotBeFound("amountHt.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt is greater than or equal to DEFAULT_AMOUNT_HT
        defaultInvoiceShouldBeFound("amountHt.greaterThanOrEqual=" + DEFAULT_AMOUNT_HT);

        // Get all the invoiceList where amountHt is greater than or equal to UPDATED_AMOUNT_HT
        defaultInvoiceShouldNotBeFound("amountHt.greaterThanOrEqual=" + UPDATED_AMOUNT_HT);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt is less than or equal to DEFAULT_AMOUNT_HT
        defaultInvoiceShouldBeFound("amountHt.lessThanOrEqual=" + DEFAULT_AMOUNT_HT);

        // Get all the invoiceList where amountHt is less than or equal to SMALLER_AMOUNT_HT
        defaultInvoiceShouldNotBeFound("amountHt.lessThanOrEqual=" + SMALLER_AMOUNT_HT);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt is less than DEFAULT_AMOUNT_HT
        defaultInvoiceShouldNotBeFound("amountHt.lessThan=" + DEFAULT_AMOUNT_HT);

        // Get all the invoiceList where amountHt is less than UPDATED_AMOUNT_HT
        defaultInvoiceShouldBeFound("amountHt.lessThan=" + UPDATED_AMOUNT_HT);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountHtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountHt is greater than DEFAULT_AMOUNT_HT
        defaultInvoiceShouldNotBeFound("amountHt.greaterThan=" + DEFAULT_AMOUNT_HT);

        // Get all the invoiceList where amountHt is greater than SMALLER_AMOUNT_HT
        defaultInvoiceShouldBeFound("amountHt.greaterThan=" + SMALLER_AMOUNT_HT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount equals to DEFAULT_TAX_AMOUNT
        defaultInvoiceShouldBeFound("taxAmount.equals=" + DEFAULT_TAX_AMOUNT);

        // Get all the invoiceList where taxAmount equals to UPDATED_TAX_AMOUNT
        defaultInvoiceShouldNotBeFound("taxAmount.equals=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount in DEFAULT_TAX_AMOUNT or UPDATED_TAX_AMOUNT
        defaultInvoiceShouldBeFound("taxAmount.in=" + DEFAULT_TAX_AMOUNT + "," + UPDATED_TAX_AMOUNT);

        // Get all the invoiceList where taxAmount equals to UPDATED_TAX_AMOUNT
        defaultInvoiceShouldNotBeFound("taxAmount.in=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is not null
        defaultInvoiceShouldBeFound("taxAmount.specified=true");

        // Get all the invoiceList where taxAmount is null
        defaultInvoiceShouldNotBeFound("taxAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is greater than or equal to DEFAULT_TAX_AMOUNT
        defaultInvoiceShouldBeFound("taxAmount.greaterThanOrEqual=" + DEFAULT_TAX_AMOUNT);

        // Get all the invoiceList where taxAmount is greater than or equal to UPDATED_TAX_AMOUNT
        defaultInvoiceShouldNotBeFound("taxAmount.greaterThanOrEqual=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is less than or equal to DEFAULT_TAX_AMOUNT
        defaultInvoiceShouldBeFound("taxAmount.lessThanOrEqual=" + DEFAULT_TAX_AMOUNT);

        // Get all the invoiceList where taxAmount is less than or equal to SMALLER_TAX_AMOUNT
        defaultInvoiceShouldNotBeFound("taxAmount.lessThanOrEqual=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is less than DEFAULT_TAX_AMOUNT
        defaultInvoiceShouldNotBeFound("taxAmount.lessThan=" + DEFAULT_TAX_AMOUNT);

        // Get all the invoiceList where taxAmount is less than UPDATED_TAX_AMOUNT
        defaultInvoiceShouldBeFound("taxAmount.lessThan=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is greater than DEFAULT_TAX_AMOUNT
        defaultInvoiceShouldNotBeFound("taxAmount.greaterThan=" + DEFAULT_TAX_AMOUNT);

        // Get all the invoiceList where taxAmount is greater than SMALLER_TAX_AMOUNT
        defaultInvoiceShouldBeFound("taxAmount.greaterThan=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc equals to DEFAULT_AMOUNT_TTC
        defaultInvoiceShouldBeFound("amountTtc.equals=" + DEFAULT_AMOUNT_TTC);

        // Get all the invoiceList where amountTtc equals to UPDATED_AMOUNT_TTC
        defaultInvoiceShouldNotBeFound("amountTtc.equals=" + UPDATED_AMOUNT_TTC);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc in DEFAULT_AMOUNT_TTC or UPDATED_AMOUNT_TTC
        defaultInvoiceShouldBeFound("amountTtc.in=" + DEFAULT_AMOUNT_TTC + "," + UPDATED_AMOUNT_TTC);

        // Get all the invoiceList where amountTtc equals to UPDATED_AMOUNT_TTC
        defaultInvoiceShouldNotBeFound("amountTtc.in=" + UPDATED_AMOUNT_TTC);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc is not null
        defaultInvoiceShouldBeFound("amountTtc.specified=true");

        // Get all the invoiceList where amountTtc is null
        defaultInvoiceShouldNotBeFound("amountTtc.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc is greater than or equal to DEFAULT_AMOUNT_TTC
        defaultInvoiceShouldBeFound("amountTtc.greaterThanOrEqual=" + DEFAULT_AMOUNT_TTC);

        // Get all the invoiceList where amountTtc is greater than or equal to UPDATED_AMOUNT_TTC
        defaultInvoiceShouldNotBeFound("amountTtc.greaterThanOrEqual=" + UPDATED_AMOUNT_TTC);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc is less than or equal to DEFAULT_AMOUNT_TTC
        defaultInvoiceShouldBeFound("amountTtc.lessThanOrEqual=" + DEFAULT_AMOUNT_TTC);

        // Get all the invoiceList where amountTtc is less than or equal to SMALLER_AMOUNT_TTC
        defaultInvoiceShouldNotBeFound("amountTtc.lessThanOrEqual=" + SMALLER_AMOUNT_TTC);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc is less than DEFAULT_AMOUNT_TTC
        defaultInvoiceShouldNotBeFound("amountTtc.lessThan=" + DEFAULT_AMOUNT_TTC);

        // Get all the invoiceList where amountTtc is less than UPDATED_AMOUNT_TTC
        defaultInvoiceShouldBeFound("amountTtc.lessThan=" + UPDATED_AMOUNT_TTC);
    }

    @Test
    @Transactional
    void getAllInvoicesByAmountTtcIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where amountTtc is greater than DEFAULT_AMOUNT_TTC
        defaultInvoiceShouldNotBeFound("amountTtc.greaterThan=" + DEFAULT_AMOUNT_TTC);

        // Get all the invoiceList where amountTtc is greater than SMALLER_AMOUNT_TTC
        defaultInvoiceShouldBeFound("amountTtc.greaterThan=" + SMALLER_AMOUNT_TTC);
    }

    @Test
    @Transactional
    void getAllInvoicesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status equals to DEFAULT_STATUS
        defaultInvoiceShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the invoiceList where status equals to UPDATED_STATUS
        defaultInvoiceShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoicesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultInvoiceShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the invoiceList where status equals to UPDATED_STATUS
        defaultInvoiceShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoicesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status is not null
        defaultInvoiceShouldBeFound("status.specified=true");

        // Get all the invoiceList where status is null
        defaultInvoiceShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate equals to DEFAULT_ISSUE_DATE
        defaultInvoiceShouldBeFound("issueDate.equals=" + DEFAULT_ISSUE_DATE);

        // Get all the invoiceList where issueDate equals to UPDATED_ISSUE_DATE
        defaultInvoiceShouldNotBeFound("issueDate.equals=" + UPDATED_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate in DEFAULT_ISSUE_DATE or UPDATED_ISSUE_DATE
        defaultInvoiceShouldBeFound("issueDate.in=" + DEFAULT_ISSUE_DATE + "," + UPDATED_ISSUE_DATE);

        // Get all the invoiceList where issueDate equals to UPDATED_ISSUE_DATE
        defaultInvoiceShouldNotBeFound("issueDate.in=" + UPDATED_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate is not null
        defaultInvoiceShouldBeFound("issueDate.specified=true");

        // Get all the invoiceList where issueDate is null
        defaultInvoiceShouldNotBeFound("issueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate is greater than or equal to DEFAULT_ISSUE_DATE
        defaultInvoiceShouldBeFound("issueDate.greaterThanOrEqual=" + DEFAULT_ISSUE_DATE);

        // Get all the invoiceList where issueDate is greater than or equal to UPDATED_ISSUE_DATE
        defaultInvoiceShouldNotBeFound("issueDate.greaterThanOrEqual=" + UPDATED_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate is less than or equal to DEFAULT_ISSUE_DATE
        defaultInvoiceShouldBeFound("issueDate.lessThanOrEqual=" + DEFAULT_ISSUE_DATE);

        // Get all the invoiceList where issueDate is less than or equal to SMALLER_ISSUE_DATE
        defaultInvoiceShouldNotBeFound("issueDate.lessThanOrEqual=" + SMALLER_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate is less than DEFAULT_ISSUE_DATE
        defaultInvoiceShouldNotBeFound("issueDate.lessThan=" + DEFAULT_ISSUE_DATE);

        // Get all the invoiceList where issueDate is less than UPDATED_ISSUE_DATE
        defaultInvoiceShouldBeFound("issueDate.lessThan=" + UPDATED_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssueDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issueDate is greater than DEFAULT_ISSUE_DATE
        defaultInvoiceShouldNotBeFound("issueDate.greaterThan=" + DEFAULT_ISSUE_DATE);

        // Get all the invoiceList where issueDate is greater than SMALLER_ISSUE_DATE
        defaultInvoiceShouldBeFound("issueDate.greaterThan=" + SMALLER_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate equals to DEFAULT_DUE_DATE
        defaultInvoiceShouldBeFound("dueDate.equals=" + DEFAULT_DUE_DATE);

        // Get all the invoiceList where dueDate equals to UPDATED_DUE_DATE
        defaultInvoiceShouldNotBeFound("dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate in DEFAULT_DUE_DATE or UPDATED_DUE_DATE
        defaultInvoiceShouldBeFound("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE);

        // Get all the invoiceList where dueDate equals to UPDATED_DUE_DATE
        defaultInvoiceShouldNotBeFound("dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate is not null
        defaultInvoiceShouldBeFound("dueDate.specified=true");

        // Get all the invoiceList where dueDate is null
        defaultInvoiceShouldNotBeFound("dueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate is greater than or equal to DEFAULT_DUE_DATE
        defaultInvoiceShouldBeFound("dueDate.greaterThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the invoiceList where dueDate is greater than or equal to UPDATED_DUE_DATE
        defaultInvoiceShouldNotBeFound("dueDate.greaterThanOrEqual=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate is less than or equal to DEFAULT_DUE_DATE
        defaultInvoiceShouldBeFound("dueDate.lessThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the invoiceList where dueDate is less than or equal to SMALLER_DUE_DATE
        defaultInvoiceShouldNotBeFound("dueDate.lessThanOrEqual=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate is less than DEFAULT_DUE_DATE
        defaultInvoiceShouldNotBeFound("dueDate.lessThan=" + DEFAULT_DUE_DATE);

        // Get all the invoiceList where dueDate is less than UPDATED_DUE_DATE
        defaultInvoiceShouldBeFound("dueDate.lessThan=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByDueDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where dueDate is greater than DEFAULT_DUE_DATE
        defaultInvoiceShouldNotBeFound("dueDate.greaterThan=" + DEFAULT_DUE_DATE);

        // Get all the invoiceList where dueDate is greater than SMALLER_DUE_DATE
        defaultInvoiceShouldBeFound("dueDate.greaterThan=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt equals to DEFAULT_PAID_AT
        defaultInvoiceShouldBeFound("paidAt.equals=" + DEFAULT_PAID_AT);

        // Get all the invoiceList where paidAt equals to UPDATED_PAID_AT
        defaultInvoiceShouldNotBeFound("paidAt.equals=" + UPDATED_PAID_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt in DEFAULT_PAID_AT or UPDATED_PAID_AT
        defaultInvoiceShouldBeFound("paidAt.in=" + DEFAULT_PAID_AT + "," + UPDATED_PAID_AT);

        // Get all the invoiceList where paidAt equals to UPDATED_PAID_AT
        defaultInvoiceShouldNotBeFound("paidAt.in=" + UPDATED_PAID_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt is not null
        defaultInvoiceShouldBeFound("paidAt.specified=true");

        // Get all the invoiceList where paidAt is null
        defaultInvoiceShouldNotBeFound("paidAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt is greater than or equal to DEFAULT_PAID_AT
        defaultInvoiceShouldBeFound("paidAt.greaterThanOrEqual=" + DEFAULT_PAID_AT);

        // Get all the invoiceList where paidAt is greater than or equal to UPDATED_PAID_AT
        defaultInvoiceShouldNotBeFound("paidAt.greaterThanOrEqual=" + UPDATED_PAID_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt is less than or equal to DEFAULT_PAID_AT
        defaultInvoiceShouldBeFound("paidAt.lessThanOrEqual=" + DEFAULT_PAID_AT);

        // Get all the invoiceList where paidAt is less than or equal to SMALLER_PAID_AT
        defaultInvoiceShouldNotBeFound("paidAt.lessThanOrEqual=" + SMALLER_PAID_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt is less than DEFAULT_PAID_AT
        defaultInvoiceShouldNotBeFound("paidAt.lessThan=" + DEFAULT_PAID_AT);

        // Get all the invoiceList where paidAt is less than UPDATED_PAID_AT
        defaultInvoiceShouldBeFound("paidAt.lessThan=" + UPDATED_PAID_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaidAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paidAt is greater than DEFAULT_PAID_AT
        defaultInvoiceShouldNotBeFound("paidAt.greaterThan=" + DEFAULT_PAID_AT);

        // Get all the invoiceList where paidAt is greater than SMALLER_PAID_AT
        defaultInvoiceShouldBeFound("paidAt.greaterThan=" + SMALLER_PAID_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where isDeleted equals to DEFAULT_IS_DELETED
        defaultInvoiceShouldBeFound("isDeleted.equals=" + DEFAULT_IS_DELETED);

        // Get all the invoiceList where isDeleted equals to UPDATED_IS_DELETED
        defaultInvoiceShouldNotBeFound("isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllInvoicesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where isDeleted in DEFAULT_IS_DELETED or UPDATED_IS_DELETED
        defaultInvoiceShouldBeFound("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED);

        // Get all the invoiceList where isDeleted equals to UPDATED_IS_DELETED
        defaultInvoiceShouldNotBeFound("isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllInvoicesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where isDeleted is not null
        defaultInvoiceShouldBeFound("isDeleted.specified=true");

        // Get all the invoiceList where isDeleted is null
        defaultInvoiceShouldNotBeFound("isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt equals to DEFAULT_CREATED_AT
        defaultInvoiceShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the invoiceList where createdAt equals to UPDATED_CREATED_AT
        defaultInvoiceShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultInvoiceShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the invoiceList where createdAt equals to UPDATED_CREATED_AT
        defaultInvoiceShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt is not null
        defaultInvoiceShouldBeFound("createdAt.specified=true");

        // Get all the invoiceList where createdAt is null
        defaultInvoiceShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultInvoiceShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the invoiceList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultInvoiceShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultInvoiceShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the invoiceList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultInvoiceShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt is less than DEFAULT_CREATED_AT
        defaultInvoiceShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the invoiceList where createdAt is less than UPDATED_CREATED_AT
        defaultInvoiceShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where createdAt is greater than DEFAULT_CREATED_AT
        defaultInvoiceShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the invoiceList where createdAt is greater than SMALLER_CREATED_AT
        defaultInvoiceShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultInvoiceShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the invoiceList where updatedAt equals to UPDATED_UPDATED_AT
        defaultInvoiceShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultInvoiceShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the invoiceList where updatedAt equals to UPDATED_UPDATED_AT
        defaultInvoiceShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt is not null
        defaultInvoiceShouldBeFound("updatedAt.specified=true");

        // Get all the invoiceList where updatedAt is null
        defaultInvoiceShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultInvoiceShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the invoiceList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultInvoiceShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultInvoiceShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the invoiceList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultInvoiceShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultInvoiceShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the invoiceList where updatedAt is less than UPDATED_UPDATED_AT
        defaultInvoiceShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultInvoiceShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the invoiceList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultInvoiceShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            client = ClientResourceIT.createEntity(em);
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        invoice.setClient(client);
        invoiceRepository.saveAndFlush(invoice);
        Long clientId = client.getId();

        // Get all the invoiceList where client equals to clientId
        defaultInvoiceShouldBeFound("clientId.equals=" + clientId);

        // Get all the invoiceList where client equals to (clientId + 1)
        defaultInvoiceShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllInvoicesByOrderIsEqualToSomething() throws Exception {
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            order = OrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        invoice.setOrder(order);
        invoiceRepository.saveAndFlush(invoice);
        Long orderId = order.getId();

        // Get all the invoiceList where order equals to orderId
        defaultInvoiceShouldBeFound("orderId.equals=" + orderId);

        // Get all the invoiceList where order equals to (orderId + 1)
        defaultInvoiceShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInvoiceShouldBeFound(String filter) throws Exception {
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].amountHt").value(hasItem(sameNumber(DEFAULT_AMOUNT_HT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].amountTtc").value(hasItem(sameNumber(DEFAULT_AMOUNT_TTC))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(sameInstant(DEFAULT_ISSUE_DATE))))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].paidAt").value(hasItem(sameInstant(DEFAULT_PAID_AT))))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInvoiceShouldNotBeFound(String filter) throws Exception {
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInvoice() throws Exception {
        // Get the invoice
        restInvoiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).get();
        // Disconnect from session so that the updates on updatedInvoice are not directly saved in db
        em.detach(updatedInvoice);
        updatedInvoice
            .tenantId(UPDATED_TENANT_ID)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .amountHt(UPDATED_AMOUNT_HT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountTtc(UPDATED_AMOUNT_TTC)
            .status(UPDATED_STATUS)
            .issueDate(UPDATED_ISSUE_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .paidAt(UPDATED_PAID_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(updatedInvoice);

        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testInvoice.getAmountHt()).isEqualByComparingTo(UPDATED_AMOUNT_HT);
        assertThat(testInvoice.getTaxAmount()).isEqualByComparingTo(UPDATED_TAX_AMOUNT);
        assertThat(testInvoice.getAmountTtc()).isEqualByComparingTo(UPDATED_AMOUNT_TTC);
        assertThat(testInvoice.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testInvoice.getIssueDate()).isEqualTo(UPDATED_ISSUE_DATE);
        assertThat(testInvoice.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testInvoice.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
        assertThat(testInvoice.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testInvoice.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testInvoice.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();
        invoice.setId(count.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();
        invoice.setId(count.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();
        invoice.setId(count.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice
            .amountHt(UPDATED_AMOUNT_HT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountTtc(UPDATED_AMOUNT_TTC)
            .paidAt(UPDATED_PAID_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT);

        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInvoice))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(DEFAULT_INVOICE_NUMBER);
        assertThat(testInvoice.getAmountHt()).isEqualByComparingTo(UPDATED_AMOUNT_HT);
        assertThat(testInvoice.getTaxAmount()).isEqualByComparingTo(UPDATED_TAX_AMOUNT);
        assertThat(testInvoice.getAmountTtc()).isEqualByComparingTo(UPDATED_AMOUNT_TTC);
        assertThat(testInvoice.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testInvoice.getIssueDate()).isEqualTo(DEFAULT_ISSUE_DATE);
        assertThat(testInvoice.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testInvoice.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
        assertThat(testInvoice.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testInvoice.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testInvoice.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice
            .tenantId(UPDATED_TENANT_ID)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .amountHt(UPDATED_AMOUNT_HT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountTtc(UPDATED_AMOUNT_TTC)
            .status(UPDATED_STATUS)
            .issueDate(UPDATED_ISSUE_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .paidAt(UPDATED_PAID_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInvoice))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testInvoice.getAmountHt()).isEqualByComparingTo(UPDATED_AMOUNT_HT);
        assertThat(testInvoice.getTaxAmount()).isEqualByComparingTo(UPDATED_TAX_AMOUNT);
        assertThat(testInvoice.getAmountTtc()).isEqualByComparingTo(UPDATED_AMOUNT_TTC);
        assertThat(testInvoice.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testInvoice.getIssueDate()).isEqualTo(UPDATED_ISSUE_DATE);
        assertThat(testInvoice.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testInvoice.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
        assertThat(testInvoice.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testInvoice.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testInvoice.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();
        invoice.setId(count.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();
        invoice.setId(count.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();
        invoice.setId(count.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(invoiceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        int databaseSizeBeforeDelete = invoiceRepository.findAll().size();

        // Delete the invoice
        restInvoiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
