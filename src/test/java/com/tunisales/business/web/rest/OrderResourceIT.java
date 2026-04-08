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
import com.tunisales.business.domain.Delivery;
import com.tunisales.business.domain.Invoice;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.enumeration.OrderStatus;
import com.tunisales.business.repository.OrderRepository;
import com.tunisales.business.service.OrderService;
import com.tunisales.business.service.criteria.OrderCriteria;
import com.tunisales.business.service.dto.OrderDTO;
import com.tunisales.business.service.mapper.OrderMapper;
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
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final String DEFAULT_ORDER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_NUMBER = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DRAFT;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.SUBMITTED;

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_SUBTOTAL = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TAX_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_PAYMENT_TERMS_DAYS = 0;
    private static final Integer UPDATED_PAYMENT_TERMS_DAYS = 1;
    private static final Integer SMALLER_PAYMENT_TERMS_DAYS = 0 - 1;

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_REJECTION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REJECTION_REASON = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SUBMITTED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SUBMITTED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_SUBMITTED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_VALIDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALIDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_VALIDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Autowired
    private OrderMapper orderMapper;

    @Mock
    private OrderService orderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .tenantId(DEFAULT_TENANT_ID)
            .orderNumber(DEFAULT_ORDER_NUMBER)
            .status(DEFAULT_STATUS)
            .subtotal(DEFAULT_SUBTOTAL)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .paymentTermsDays(DEFAULT_PAYMENT_TERMS_DAYS)
            .dueDate(DEFAULT_DUE_DATE)
            .rejectionReason(DEFAULT_REJECTION_REASON)
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .validatedAt(DEFAULT_VALIDATED_AT)
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
        order.setClient(client);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order()
            .tenantId(UPDATED_TENANT_ID)
            .orderNumber(UPDATED_ORDER_NUMBER)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .dueDate(UPDATED_DUE_DATE)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .validatedAt(UPDATED_VALIDATED_AT)
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
        order.setClient(client);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(DEFAULT_ORDER_NUMBER);
        assertThat(testOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrder.getSubtotal()).isEqualByComparingTo(DEFAULT_SUBTOTAL);
        assertThat(testOrder.getDiscountAmount()).isEqualByComparingTo(DEFAULT_DISCOUNT_AMOUNT);
        assertThat(testOrder.getTaxAmount()).isEqualByComparingTo(DEFAULT_TAX_AMOUNT);
        assertThat(testOrder.getTotalAmount()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testOrder.getPaymentTermsDays()).isEqualTo(DEFAULT_PAYMENT_TERMS_DAYS);
        assertThat(testOrder.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testOrder.getRejectionReason()).isEqualTo(DEFAULT_REJECTION_REASON);
        assertThat(testOrder.getSubmittedAt()).isEqualTo(DEFAULT_SUBMITTED_AT);
        assertThat(testOrder.getValidatedAt()).isEqualTo(DEFAULT_VALIDATED_AT);
        assertThat(testOrder.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testOrder.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testOrder.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);
        OrderDTO orderDTO = orderMapper.toDto(order);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setTenantId(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setOrderNumber(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setStatus(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubtotalIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setSubtotal(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setTotalAmount(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setIsDeleted(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setCreatedAt(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderNumber").value(hasItem(DEFAULT_ORDER_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].paymentTermsDays").value(hasItem(DEFAULT_PAYMENT_TERMS_DAYS)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].rejectionReason").value(hasItem(DEFAULT_REJECTION_REASON)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(sameInstant(DEFAULT_SUBMITTED_AT))))
            .andExpect(jsonPath("$.[*].validatedAt").value(hasItem(sameInstant(DEFAULT_VALIDATED_AT))))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.orderNumber").value(DEFAULT_ORDER_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.taxAmount").value(sameNumber(DEFAULT_TAX_AMOUNT)))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.paymentTermsDays").value(DEFAULT_PAYMENT_TERMS_DAYS))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.rejectionReason").value(DEFAULT_REJECTION_REASON))
            .andExpect(jsonPath("$.submittedAt").value(sameInstant(DEFAULT_SUBMITTED_AT)))
            .andExpect(jsonPath("$.validatedAt").value(sameInstant(DEFAULT_VALIDATED_AT)))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        Long id = order.getId();

        defaultOrderShouldBeFound("id.equals=" + id);
        defaultOrderShouldNotBeFound("id.notEquals=" + id);

        defaultOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId equals to DEFAULT_TENANT_ID
        defaultOrderShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the orderList where tenantId equals to UPDATED_TENANT_ID
        defaultOrderShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultOrderShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the orderList where tenantId equals to UPDATED_TENANT_ID
        defaultOrderShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId is not null
        defaultOrderShouldBeFound("tenantId.specified=true");

        // Get all the orderList where tenantId is null
        defaultOrderShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultOrderShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the orderList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultOrderShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultOrderShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the orderList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultOrderShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId is less than DEFAULT_TENANT_ID
        defaultOrderShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the orderList where tenantId is less than UPDATED_TENANT_ID
        defaultOrderShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantId is greater than DEFAULT_TENANT_ID
        defaultOrderShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the orderList where tenantId is greater than SMALLER_TENANT_ID
        defaultOrderShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber equals to DEFAULT_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.equals=" + DEFAULT_ORDER_NUMBER);

        // Get all the orderList where orderNumber equals to UPDATED_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.equals=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber in DEFAULT_ORDER_NUMBER or UPDATED_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.in=" + DEFAULT_ORDER_NUMBER + "," + UPDATED_ORDER_NUMBER);

        // Get all the orderList where orderNumber equals to UPDATED_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.in=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber is not null
        defaultOrderShouldBeFound("orderNumber.specified=true");

        // Get all the orderList where orderNumber is null
        defaultOrderShouldNotBeFound("orderNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber contains DEFAULT_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.contains=" + DEFAULT_ORDER_NUMBER);

        // Get all the orderList where orderNumber contains UPDATED_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.contains=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber does not contain DEFAULT_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.doesNotContain=" + DEFAULT_ORDER_NUMBER);

        // Get all the orderList where orderNumber does not contain UPDATED_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.doesNotContain=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status equals to DEFAULT_STATUS
        defaultOrderShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the orderList where status equals to UPDATED_STATUS
        defaultOrderShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrderShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the orderList where status equals to UPDATED_STATUS
        defaultOrderShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status is not null
        defaultOrderShouldBeFound("status.specified=true");

        // Get all the orderList where status is null
        defaultOrderShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal equals to DEFAULT_SUBTOTAL
        defaultOrderShouldBeFound("subtotal.equals=" + DEFAULT_SUBTOTAL);

        // Get all the orderList where subtotal equals to UPDATED_SUBTOTAL
        defaultOrderShouldNotBeFound("subtotal.equals=" + UPDATED_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal in DEFAULT_SUBTOTAL or UPDATED_SUBTOTAL
        defaultOrderShouldBeFound("subtotal.in=" + DEFAULT_SUBTOTAL + "," + UPDATED_SUBTOTAL);

        // Get all the orderList where subtotal equals to UPDATED_SUBTOTAL
        defaultOrderShouldNotBeFound("subtotal.in=" + UPDATED_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal is not null
        defaultOrderShouldBeFound("subtotal.specified=true");

        // Get all the orderList where subtotal is null
        defaultOrderShouldNotBeFound("subtotal.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal is greater than or equal to DEFAULT_SUBTOTAL
        defaultOrderShouldBeFound("subtotal.greaterThanOrEqual=" + DEFAULT_SUBTOTAL);

        // Get all the orderList where subtotal is greater than or equal to UPDATED_SUBTOTAL
        defaultOrderShouldNotBeFound("subtotal.greaterThanOrEqual=" + UPDATED_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal is less than or equal to DEFAULT_SUBTOTAL
        defaultOrderShouldBeFound("subtotal.lessThanOrEqual=" + DEFAULT_SUBTOTAL);

        // Get all the orderList where subtotal is less than or equal to SMALLER_SUBTOTAL
        defaultOrderShouldNotBeFound("subtotal.lessThanOrEqual=" + SMALLER_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal is less than DEFAULT_SUBTOTAL
        defaultOrderShouldNotBeFound("subtotal.lessThan=" + DEFAULT_SUBTOTAL);

        // Get all the orderList where subtotal is less than UPDATED_SUBTOTAL
        defaultOrderShouldBeFound("subtotal.lessThan=" + UPDATED_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersBySubtotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where subtotal is greater than DEFAULT_SUBTOTAL
        defaultOrderShouldNotBeFound("subtotal.greaterThan=" + DEFAULT_SUBTOTAL);

        // Get all the orderList where subtotal is greater than SMALLER_SUBTOTAL
        defaultOrderShouldBeFound("subtotal.greaterThan=" + SMALLER_SUBTOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount equals to DEFAULT_DISCOUNT_AMOUNT
        defaultOrderShouldBeFound("discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT);

        // Get all the orderList where discountAmount equals to UPDATED_DISCOUNT_AMOUNT
        defaultOrderShouldNotBeFound("discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount in DEFAULT_DISCOUNT_AMOUNT or UPDATED_DISCOUNT_AMOUNT
        defaultOrderShouldBeFound("discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT);

        // Get all the orderList where discountAmount equals to UPDATED_DISCOUNT_AMOUNT
        defaultOrderShouldNotBeFound("discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount is not null
        defaultOrderShouldBeFound("discountAmount.specified=true");

        // Get all the orderList where discountAmount is null
        defaultOrderShouldNotBeFound("discountAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount is greater than or equal to DEFAULT_DISCOUNT_AMOUNT
        defaultOrderShouldBeFound("discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT);

        // Get all the orderList where discountAmount is greater than or equal to UPDATED_DISCOUNT_AMOUNT
        defaultOrderShouldNotBeFound("discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount is less than or equal to DEFAULT_DISCOUNT_AMOUNT
        defaultOrderShouldBeFound("discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT);

        // Get all the orderList where discountAmount is less than or equal to SMALLER_DISCOUNT_AMOUNT
        defaultOrderShouldNotBeFound("discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount is less than DEFAULT_DISCOUNT_AMOUNT
        defaultOrderShouldNotBeFound("discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT);

        // Get all the orderList where discountAmount is less than UPDATED_DISCOUNT_AMOUNT
        defaultOrderShouldBeFound("discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discountAmount is greater than DEFAULT_DISCOUNT_AMOUNT
        defaultOrderShouldNotBeFound("discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT);

        // Get all the orderList where discountAmount is greater than SMALLER_DISCOUNT_AMOUNT
        defaultOrderShouldBeFound("discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount equals to DEFAULT_TAX_AMOUNT
        defaultOrderShouldBeFound("taxAmount.equals=" + DEFAULT_TAX_AMOUNT);

        // Get all the orderList where taxAmount equals to UPDATED_TAX_AMOUNT
        defaultOrderShouldNotBeFound("taxAmount.equals=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount in DEFAULT_TAX_AMOUNT or UPDATED_TAX_AMOUNT
        defaultOrderShouldBeFound("taxAmount.in=" + DEFAULT_TAX_AMOUNT + "," + UPDATED_TAX_AMOUNT);

        // Get all the orderList where taxAmount equals to UPDATED_TAX_AMOUNT
        defaultOrderShouldNotBeFound("taxAmount.in=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount is not null
        defaultOrderShouldBeFound("taxAmount.specified=true");

        // Get all the orderList where taxAmount is null
        defaultOrderShouldNotBeFound("taxAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount is greater than or equal to DEFAULT_TAX_AMOUNT
        defaultOrderShouldBeFound("taxAmount.greaterThanOrEqual=" + DEFAULT_TAX_AMOUNT);

        // Get all the orderList where taxAmount is greater than or equal to UPDATED_TAX_AMOUNT
        defaultOrderShouldNotBeFound("taxAmount.greaterThanOrEqual=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount is less than or equal to DEFAULT_TAX_AMOUNT
        defaultOrderShouldBeFound("taxAmount.lessThanOrEqual=" + DEFAULT_TAX_AMOUNT);

        // Get all the orderList where taxAmount is less than or equal to SMALLER_TAX_AMOUNT
        defaultOrderShouldNotBeFound("taxAmount.lessThanOrEqual=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount is less than DEFAULT_TAX_AMOUNT
        defaultOrderShouldNotBeFound("taxAmount.lessThan=" + DEFAULT_TAX_AMOUNT);

        // Get all the orderList where taxAmount is less than UPDATED_TAX_AMOUNT
        defaultOrderShouldBeFound("taxAmount.lessThan=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTaxAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where taxAmount is greater than DEFAULT_TAX_AMOUNT
        defaultOrderShouldNotBeFound("taxAmount.greaterThan=" + DEFAULT_TAX_AMOUNT);

        // Get all the orderList where taxAmount is greater than SMALLER_TAX_AMOUNT
        defaultOrderShouldBeFound("taxAmount.greaterThan=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount equals to DEFAULT_TOTAL_AMOUNT
        defaultOrderShouldBeFound("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the orderList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultOrderShouldNotBeFound("totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount in DEFAULT_TOTAL_AMOUNT or UPDATED_TOTAL_AMOUNT
        defaultOrderShouldBeFound("totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT);

        // Get all the orderList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultOrderShouldNotBeFound("totalAmount.in=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount is not null
        defaultOrderShouldBeFound("totalAmount.specified=true");

        // Get all the orderList where totalAmount is null
        defaultOrderShouldNotBeFound("totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount is greater than or equal to DEFAULT_TOTAL_AMOUNT
        defaultOrderShouldBeFound("totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the orderList where totalAmount is greater than or equal to UPDATED_TOTAL_AMOUNT
        defaultOrderShouldNotBeFound("totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount is less than or equal to DEFAULT_TOTAL_AMOUNT
        defaultOrderShouldBeFound("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the orderList where totalAmount is less than or equal to SMALLER_TOTAL_AMOUNT
        defaultOrderShouldNotBeFound("totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount is less than DEFAULT_TOTAL_AMOUNT
        defaultOrderShouldNotBeFound("totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the orderList where totalAmount is less than UPDATED_TOTAL_AMOUNT
        defaultOrderShouldBeFound("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where totalAmount is greater than DEFAULT_TOTAL_AMOUNT
        defaultOrderShouldNotBeFound("totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the orderList where totalAmount is greater than SMALLER_TOTAL_AMOUNT
        defaultOrderShouldBeFound("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays equals to DEFAULT_PAYMENT_TERMS_DAYS
        defaultOrderShouldBeFound("paymentTermsDays.equals=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the orderList where paymentTermsDays equals to UPDATED_PAYMENT_TERMS_DAYS
        defaultOrderShouldNotBeFound("paymentTermsDays.equals=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays in DEFAULT_PAYMENT_TERMS_DAYS or UPDATED_PAYMENT_TERMS_DAYS
        defaultOrderShouldBeFound("paymentTermsDays.in=" + DEFAULT_PAYMENT_TERMS_DAYS + "," + UPDATED_PAYMENT_TERMS_DAYS);

        // Get all the orderList where paymentTermsDays equals to UPDATED_PAYMENT_TERMS_DAYS
        defaultOrderShouldNotBeFound("paymentTermsDays.in=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays is not null
        defaultOrderShouldBeFound("paymentTermsDays.specified=true");

        // Get all the orderList where paymentTermsDays is null
        defaultOrderShouldNotBeFound("paymentTermsDays.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays is greater than or equal to DEFAULT_PAYMENT_TERMS_DAYS
        defaultOrderShouldBeFound("paymentTermsDays.greaterThanOrEqual=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the orderList where paymentTermsDays is greater than or equal to UPDATED_PAYMENT_TERMS_DAYS
        defaultOrderShouldNotBeFound("paymentTermsDays.greaterThanOrEqual=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays is less than or equal to DEFAULT_PAYMENT_TERMS_DAYS
        defaultOrderShouldBeFound("paymentTermsDays.lessThanOrEqual=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the orderList where paymentTermsDays is less than or equal to SMALLER_PAYMENT_TERMS_DAYS
        defaultOrderShouldNotBeFound("paymentTermsDays.lessThanOrEqual=" + SMALLER_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays is less than DEFAULT_PAYMENT_TERMS_DAYS
        defaultOrderShouldNotBeFound("paymentTermsDays.lessThan=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the orderList where paymentTermsDays is less than UPDATED_PAYMENT_TERMS_DAYS
        defaultOrderShouldBeFound("paymentTermsDays.lessThan=" + UPDATED_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentTermsDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where paymentTermsDays is greater than DEFAULT_PAYMENT_TERMS_DAYS
        defaultOrderShouldNotBeFound("paymentTermsDays.greaterThan=" + DEFAULT_PAYMENT_TERMS_DAYS);

        // Get all the orderList where paymentTermsDays is greater than SMALLER_PAYMENT_TERMS_DAYS
        defaultOrderShouldBeFound("paymentTermsDays.greaterThan=" + SMALLER_PAYMENT_TERMS_DAYS);
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate equals to DEFAULT_DUE_DATE
        defaultOrderShouldBeFound("dueDate.equals=" + DEFAULT_DUE_DATE);

        // Get all the orderList where dueDate equals to UPDATED_DUE_DATE
        defaultOrderShouldNotBeFound("dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate in DEFAULT_DUE_DATE or UPDATED_DUE_DATE
        defaultOrderShouldBeFound("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE);

        // Get all the orderList where dueDate equals to UPDATED_DUE_DATE
        defaultOrderShouldNotBeFound("dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate is not null
        defaultOrderShouldBeFound("dueDate.specified=true");

        // Get all the orderList where dueDate is null
        defaultOrderShouldNotBeFound("dueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate is greater than or equal to DEFAULT_DUE_DATE
        defaultOrderShouldBeFound("dueDate.greaterThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the orderList where dueDate is greater than or equal to UPDATED_DUE_DATE
        defaultOrderShouldNotBeFound("dueDate.greaterThanOrEqual=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate is less than or equal to DEFAULT_DUE_DATE
        defaultOrderShouldBeFound("dueDate.lessThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the orderList where dueDate is less than or equal to SMALLER_DUE_DATE
        defaultOrderShouldNotBeFound("dueDate.lessThanOrEqual=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate is less than DEFAULT_DUE_DATE
        defaultOrderShouldNotBeFound("dueDate.lessThan=" + DEFAULT_DUE_DATE);

        // Get all the orderList where dueDate is less than UPDATED_DUE_DATE
        defaultOrderShouldBeFound("dueDate.lessThan=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByDueDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where dueDate is greater than DEFAULT_DUE_DATE
        defaultOrderShouldNotBeFound("dueDate.greaterThan=" + DEFAULT_DUE_DATE);

        // Get all the orderList where dueDate is greater than SMALLER_DUE_DATE
        defaultOrderShouldBeFound("dueDate.greaterThan=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByRejectionReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where rejectionReason equals to DEFAULT_REJECTION_REASON
        defaultOrderShouldBeFound("rejectionReason.equals=" + DEFAULT_REJECTION_REASON);

        // Get all the orderList where rejectionReason equals to UPDATED_REJECTION_REASON
        defaultOrderShouldNotBeFound("rejectionReason.equals=" + UPDATED_REJECTION_REASON);
    }

    @Test
    @Transactional
    void getAllOrdersByRejectionReasonIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where rejectionReason in DEFAULT_REJECTION_REASON or UPDATED_REJECTION_REASON
        defaultOrderShouldBeFound("rejectionReason.in=" + DEFAULT_REJECTION_REASON + "," + UPDATED_REJECTION_REASON);

        // Get all the orderList where rejectionReason equals to UPDATED_REJECTION_REASON
        defaultOrderShouldNotBeFound("rejectionReason.in=" + UPDATED_REJECTION_REASON);
    }

    @Test
    @Transactional
    void getAllOrdersByRejectionReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where rejectionReason is not null
        defaultOrderShouldBeFound("rejectionReason.specified=true");

        // Get all the orderList where rejectionReason is null
        defaultOrderShouldNotBeFound("rejectionReason.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByRejectionReasonContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where rejectionReason contains DEFAULT_REJECTION_REASON
        defaultOrderShouldBeFound("rejectionReason.contains=" + DEFAULT_REJECTION_REASON);

        // Get all the orderList where rejectionReason contains UPDATED_REJECTION_REASON
        defaultOrderShouldNotBeFound("rejectionReason.contains=" + UPDATED_REJECTION_REASON);
    }

    @Test
    @Transactional
    void getAllOrdersByRejectionReasonNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where rejectionReason does not contain DEFAULT_REJECTION_REASON
        defaultOrderShouldNotBeFound("rejectionReason.doesNotContain=" + DEFAULT_REJECTION_REASON);

        // Get all the orderList where rejectionReason does not contain UPDATED_REJECTION_REASON
        defaultOrderShouldBeFound("rejectionReason.doesNotContain=" + UPDATED_REJECTION_REASON);
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt equals to DEFAULT_SUBMITTED_AT
        defaultOrderShouldBeFound("submittedAt.equals=" + DEFAULT_SUBMITTED_AT);

        // Get all the orderList where submittedAt equals to UPDATED_SUBMITTED_AT
        defaultOrderShouldNotBeFound("submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt in DEFAULT_SUBMITTED_AT or UPDATED_SUBMITTED_AT
        defaultOrderShouldBeFound("submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT);

        // Get all the orderList where submittedAt equals to UPDATED_SUBMITTED_AT
        defaultOrderShouldNotBeFound("submittedAt.in=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt is not null
        defaultOrderShouldBeFound("submittedAt.specified=true");

        // Get all the orderList where submittedAt is null
        defaultOrderShouldNotBeFound("submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt is greater than or equal to DEFAULT_SUBMITTED_AT
        defaultOrderShouldBeFound("submittedAt.greaterThanOrEqual=" + DEFAULT_SUBMITTED_AT);

        // Get all the orderList where submittedAt is greater than or equal to UPDATED_SUBMITTED_AT
        defaultOrderShouldNotBeFound("submittedAt.greaterThanOrEqual=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt is less than or equal to DEFAULT_SUBMITTED_AT
        defaultOrderShouldBeFound("submittedAt.lessThanOrEqual=" + DEFAULT_SUBMITTED_AT);

        // Get all the orderList where submittedAt is less than or equal to SMALLER_SUBMITTED_AT
        defaultOrderShouldNotBeFound("submittedAt.lessThanOrEqual=" + SMALLER_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt is less than DEFAULT_SUBMITTED_AT
        defaultOrderShouldNotBeFound("submittedAt.lessThan=" + DEFAULT_SUBMITTED_AT);

        // Get all the orderList where submittedAt is less than UPDATED_SUBMITTED_AT
        defaultOrderShouldBeFound("submittedAt.lessThan=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersBySubmittedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where submittedAt is greater than DEFAULT_SUBMITTED_AT
        defaultOrderShouldNotBeFound("submittedAt.greaterThan=" + DEFAULT_SUBMITTED_AT);

        // Get all the orderList where submittedAt is greater than SMALLER_SUBMITTED_AT
        defaultOrderShouldBeFound("submittedAt.greaterThan=" + SMALLER_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt equals to DEFAULT_VALIDATED_AT
        defaultOrderShouldBeFound("validatedAt.equals=" + DEFAULT_VALIDATED_AT);

        // Get all the orderList where validatedAt equals to UPDATED_VALIDATED_AT
        defaultOrderShouldNotBeFound("validatedAt.equals=" + UPDATED_VALIDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt in DEFAULT_VALIDATED_AT or UPDATED_VALIDATED_AT
        defaultOrderShouldBeFound("validatedAt.in=" + DEFAULT_VALIDATED_AT + "," + UPDATED_VALIDATED_AT);

        // Get all the orderList where validatedAt equals to UPDATED_VALIDATED_AT
        defaultOrderShouldNotBeFound("validatedAt.in=" + UPDATED_VALIDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt is not null
        defaultOrderShouldBeFound("validatedAt.specified=true");

        // Get all the orderList where validatedAt is null
        defaultOrderShouldNotBeFound("validatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt is greater than or equal to DEFAULT_VALIDATED_AT
        defaultOrderShouldBeFound("validatedAt.greaterThanOrEqual=" + DEFAULT_VALIDATED_AT);

        // Get all the orderList where validatedAt is greater than or equal to UPDATED_VALIDATED_AT
        defaultOrderShouldNotBeFound("validatedAt.greaterThanOrEqual=" + UPDATED_VALIDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt is less than or equal to DEFAULT_VALIDATED_AT
        defaultOrderShouldBeFound("validatedAt.lessThanOrEqual=" + DEFAULT_VALIDATED_AT);

        // Get all the orderList where validatedAt is less than or equal to SMALLER_VALIDATED_AT
        defaultOrderShouldNotBeFound("validatedAt.lessThanOrEqual=" + SMALLER_VALIDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt is less than DEFAULT_VALIDATED_AT
        defaultOrderShouldNotBeFound("validatedAt.lessThan=" + DEFAULT_VALIDATED_AT);

        // Get all the orderList where validatedAt is less than UPDATED_VALIDATED_AT
        defaultOrderShouldBeFound("validatedAt.lessThan=" + UPDATED_VALIDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByValidatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where validatedAt is greater than DEFAULT_VALIDATED_AT
        defaultOrderShouldNotBeFound("validatedAt.greaterThan=" + DEFAULT_VALIDATED_AT);

        // Get all the orderList where validatedAt is greater than SMALLER_VALIDATED_AT
        defaultOrderShouldBeFound("validatedAt.greaterThan=" + SMALLER_VALIDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where isDeleted equals to DEFAULT_IS_DELETED
        defaultOrderShouldBeFound("isDeleted.equals=" + DEFAULT_IS_DELETED);

        // Get all the orderList where isDeleted equals to UPDATED_IS_DELETED
        defaultOrderShouldNotBeFound("isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllOrdersByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where isDeleted in DEFAULT_IS_DELETED or UPDATED_IS_DELETED
        defaultOrderShouldBeFound("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED);

        // Get all the orderList where isDeleted equals to UPDATED_IS_DELETED
        defaultOrderShouldNotBeFound("isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllOrdersByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where isDeleted is not null
        defaultOrderShouldBeFound("isDeleted.specified=true");

        // Get all the orderList where isDeleted is null
        defaultOrderShouldNotBeFound("isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt equals to DEFAULT_CREATED_AT
        defaultOrderShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the orderList where createdAt equals to UPDATED_CREATED_AT
        defaultOrderShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultOrderShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the orderList where createdAt equals to UPDATED_CREATED_AT
        defaultOrderShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt is not null
        defaultOrderShouldBeFound("createdAt.specified=true");

        // Get all the orderList where createdAt is null
        defaultOrderShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultOrderShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the orderList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultOrderShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultOrderShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the orderList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultOrderShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt is less than DEFAULT_CREATED_AT
        defaultOrderShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the orderList where createdAt is less than UPDATED_CREATED_AT
        defaultOrderShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdAt is greater than DEFAULT_CREATED_AT
        defaultOrderShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the orderList where createdAt is greater than SMALLER_CREATED_AT
        defaultOrderShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultOrderShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the orderList where updatedAt equals to UPDATED_UPDATED_AT
        defaultOrderShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultOrderShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the orderList where updatedAt equals to UPDATED_UPDATED_AT
        defaultOrderShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt is not null
        defaultOrderShouldBeFound("updatedAt.specified=true");

        // Get all the orderList where updatedAt is null
        defaultOrderShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultOrderShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the orderList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultOrderShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultOrderShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the orderList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultOrderShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultOrderShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the orderList where updatedAt is less than UPDATED_UPDATED_AT
        defaultOrderShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultOrderShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the orderList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultOrderShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderLinesIsEqualToSomething() throws Exception {
        OrderLine orderLines;
        if (TestUtil.findAll(em, OrderLine.class).isEmpty()) {
            orderRepository.saveAndFlush(order);
            orderLines = OrderLineResourceIT.createEntity(em);
        } else {
            orderLines = TestUtil.findAll(em, OrderLine.class).get(0);
        }
        em.persist(orderLines);
        em.flush();
        order.addOrderLines(orderLines);
        orderRepository.saveAndFlush(order);
        Long orderLinesId = orderLines.getId();

        // Get all the orderList where orderLines equals to orderLinesId
        defaultOrderShouldBeFound("orderLinesId.equals=" + orderLinesId);

        // Get all the orderList where orderLines equals to (orderLinesId + 1)
        defaultOrderShouldNotBeFound("orderLinesId.equals=" + (orderLinesId + 1));
    }

    @Test
    @Transactional
    void getAllOrdersByDeliveriesIsEqualToSomething() throws Exception {
        Delivery deliveries;
        if (TestUtil.findAll(em, Delivery.class).isEmpty()) {
            orderRepository.saveAndFlush(order);
            deliveries = DeliveryResourceIT.createEntity(em);
        } else {
            deliveries = TestUtil.findAll(em, Delivery.class).get(0);
        }
        em.persist(deliveries);
        em.flush();
        order.addDeliveries(deliveries);
        orderRepository.saveAndFlush(order);
        Long deliveriesId = deliveries.getId();

        // Get all the orderList where deliveries equals to deliveriesId
        defaultOrderShouldBeFound("deliveriesId.equals=" + deliveriesId);

        // Get all the orderList where deliveries equals to (deliveriesId + 1)
        defaultOrderShouldNotBeFound("deliveriesId.equals=" + (deliveriesId + 1));
    }

    @Test
    @Transactional
    void getAllOrdersByInvoicesIsEqualToSomething() throws Exception {
        Invoice invoices;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            orderRepository.saveAndFlush(order);
            invoices = InvoiceResourceIT.createEntity(em);
        } else {
            invoices = TestUtil.findAll(em, Invoice.class).get(0);
        }
        em.persist(invoices);
        em.flush();
        order.addInvoices(invoices);
        orderRepository.saveAndFlush(order);
        Long invoicesId = invoices.getId();

        // Get all the orderList where invoices equals to invoicesId
        defaultOrderShouldBeFound("invoicesId.equals=" + invoicesId);

        // Get all the orderList where invoices equals to (invoicesId + 1)
        defaultOrderShouldNotBeFound("invoicesId.equals=" + (invoicesId + 1));
    }

    @Test
    @Transactional
    void getAllOrdersByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            orderRepository.saveAndFlush(order);
            client = ClientResourceIT.createEntity(em);
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        order.setClient(client);
        orderRepository.saveAndFlush(order);
        Long clientId = client.getId();

        // Get all the orderList where client equals to clientId
        defaultOrderShouldBeFound("clientId.equals=" + clientId);

        // Get all the orderList where client equals to (clientId + 1)
        defaultOrderShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderShouldBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].orderNumber").value(hasItem(DEFAULT_ORDER_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].paymentTermsDays").value(hasItem(DEFAULT_PAYMENT_TERMS_DAYS)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].rejectionReason").value(hasItem(DEFAULT_REJECTION_REASON)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(sameInstant(DEFAULT_SUBMITTED_AT))))
            .andExpect(jsonPath("$.[*].validatedAt").value(hasItem(sameInstant(DEFAULT_VALIDATED_AT))))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderShouldNotBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .tenantId(UPDATED_TENANT_ID)
            .orderNumber(UPDATED_ORDER_NUMBER)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .dueDate(UPDATED_DUE_DATE)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .validatedAt(UPDATED_VALIDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(UPDATED_ORDER_NUMBER);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrder.getSubtotal()).isEqualByComparingTo(UPDATED_SUBTOTAL);
        assertThat(testOrder.getDiscountAmount()).isEqualByComparingTo(UPDATED_DISCOUNT_AMOUNT);
        assertThat(testOrder.getTaxAmount()).isEqualByComparingTo(UPDATED_TAX_AMOUNT);
        assertThat(testOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testOrder.getPaymentTermsDays()).isEqualTo(UPDATED_PAYMENT_TERMS_DAYS);
        assertThat(testOrder.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testOrder.getRejectionReason()).isEqualTo(UPDATED_REJECTION_REASON);
        assertThat(testOrder.getSubmittedAt()).isEqualTo(UPDATED_SUBMITTED_AT);
        assertThat(testOrder.getValidatedAt()).isEqualTo(UPDATED_VALIDATED_AT);
        assertThat(testOrder.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testOrder.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .tenantId(UPDATED_TENANT_ID)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .dueDate(UPDATED_DUE_DATE)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .validatedAt(UPDATED_VALIDATED_AT)
            .createdAt(UPDATED_CREATED_AT);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(DEFAULT_ORDER_NUMBER);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrder.getSubtotal()).isEqualByComparingTo(UPDATED_SUBTOTAL);
        assertThat(testOrder.getDiscountAmount()).isEqualByComparingTo(UPDATED_DISCOUNT_AMOUNT);
        assertThat(testOrder.getTaxAmount()).isEqualByComparingTo(UPDATED_TAX_AMOUNT);
        assertThat(testOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testOrder.getPaymentTermsDays()).isEqualTo(UPDATED_PAYMENT_TERMS_DAYS);
        assertThat(testOrder.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testOrder.getRejectionReason()).isEqualTo(UPDATED_REJECTION_REASON);
        assertThat(testOrder.getSubmittedAt()).isEqualTo(DEFAULT_SUBMITTED_AT);
        assertThat(testOrder.getValidatedAt()).isEqualTo(UPDATED_VALIDATED_AT);
        assertThat(testOrder.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testOrder.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .tenantId(UPDATED_TENANT_ID)
            .orderNumber(UPDATED_ORDER_NUMBER)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .paymentTermsDays(UPDATED_PAYMENT_TERMS_DAYS)
            .dueDate(UPDATED_DUE_DATE)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .validatedAt(UPDATED_VALIDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(UPDATED_ORDER_NUMBER);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrder.getSubtotal()).isEqualByComparingTo(UPDATED_SUBTOTAL);
        assertThat(testOrder.getDiscountAmount()).isEqualByComparingTo(UPDATED_DISCOUNT_AMOUNT);
        assertThat(testOrder.getTaxAmount()).isEqualByComparingTo(UPDATED_TAX_AMOUNT);
        assertThat(testOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testOrder.getPaymentTermsDays()).isEqualTo(UPDATED_PAYMENT_TERMS_DAYS);
        assertThat(testOrder.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testOrder.getRejectionReason()).isEqualTo(UPDATED_REJECTION_REASON);
        assertThat(testOrder.getSubmittedAt()).isEqualTo(UPDATED_SUBMITTED_AT);
        assertThat(testOrder.getValidatedAt()).isEqualTo(UPDATED_VALIDATED_AT);
        assertThat(testOrder.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testOrder.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
