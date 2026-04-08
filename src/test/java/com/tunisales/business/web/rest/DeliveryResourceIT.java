package com.tunisales.business.web.rest;

import static com.tunisales.business.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.Delivery;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.enumeration.DeliveryStatus;
import com.tunisales.business.repository.DeliveryRepository;
import com.tunisales.business.service.DeliveryService;
import com.tunisales.business.service.criteria.DeliveryCriteria;
import com.tunisales.business.service.dto.DeliveryDTO;
import com.tunisales.business.service.mapper.DeliveryMapper;
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
 * Integration tests for the {@link DeliveryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DeliveryResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final String DEFAULT_DELIVERY_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_NUMBER = "BBBBBBBBBB";

    private static final DeliveryStatus DEFAULT_STATUS = DeliveryStatus.PENDING;
    private static final DeliveryStatus UPDATED_STATUS = DeliveryStatus.IN_PREPARATION;

    private static final String DEFAULT_TRACKING_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TRACKING_NUMBER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SHIPPED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SHIPPED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_SHIPPED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_DELIVERED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELIVERED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DELIVERED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_CONFIRMED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CONFIRMED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CONFIRMED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryRepository deliveryRepositoryMock;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Mock
    private DeliveryService deliveryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryMockMvc;

    private Delivery delivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createEntity(EntityManager em) {
        Delivery delivery = new Delivery()
            .tenantId(DEFAULT_TENANT_ID)
            .deliveryNumber(DEFAULT_DELIVERY_NUMBER)
            .status(DEFAULT_STATUS)
            .trackingNumber(DEFAULT_TRACKING_NUMBER)
            .shippedAt(DEFAULT_SHIPPED_AT)
            .deliveredAt(DEFAULT_DELIVERED_AT)
            .confirmedAt(DEFAULT_CONFIRMED_AT)
            .notes(DEFAULT_NOTES)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        delivery.setOrder(order);
        return delivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createUpdatedEntity(EntityManager em) {
        Delivery delivery = new Delivery()
            .tenantId(UPDATED_TENANT_ID)
            .deliveryNumber(UPDATED_DELIVERY_NUMBER)
            .status(UPDATED_STATUS)
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .shippedAt(UPDATED_SHIPPED_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        delivery.setOrder(order);
        return delivery;
    }

    @BeforeEach
    public void initTest() {
        delivery = createEntity(em);
    }

    @Test
    @Transactional
    void createDelivery() throws Exception {
        int databaseSizeBeforeCreate = deliveryRepository.findAll().size();
        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);
        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isCreated());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeCreate + 1);
        Delivery testDelivery = deliveryList.get(deliveryList.size() - 1);
        assertThat(testDelivery.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testDelivery.getDeliveryNumber()).isEqualTo(DEFAULT_DELIVERY_NUMBER);
        assertThat(testDelivery.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDelivery.getTrackingNumber()).isEqualTo(DEFAULT_TRACKING_NUMBER);
        assertThat(testDelivery.getShippedAt()).isEqualTo(DEFAULT_SHIPPED_AT);
        assertThat(testDelivery.getDeliveredAt()).isEqualTo(DEFAULT_DELIVERED_AT);
        assertThat(testDelivery.getConfirmedAt()).isEqualTo(DEFAULT_CONFIRMED_AT);
        assertThat(testDelivery.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testDelivery.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createDeliveryWithExistingId() throws Exception {
        // Create the Delivery with an existing ID
        delivery.setId(1L);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        int databaseSizeBeforeCreate = deliveryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = deliveryRepository.findAll().size();
        // set the field null
        delivery.setTenantId(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDeliveryNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = deliveryRepository.findAll().size();
        // set the field null
        delivery.setDeliveryNumber(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = deliveryRepository.findAll().size();
        // set the field null
        delivery.setStatus(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = deliveryRepository.findAll().size();
        // set the field null
        delivery.setCreatedAt(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDeliveries() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].deliveryNumber").value(hasItem(DEFAULT_DELIVERY_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].trackingNumber").value(hasItem(DEFAULT_TRACKING_NUMBER)))
            .andExpect(jsonPath("$.[*].shippedAt").value(hasItem(sameInstant(DEFAULT_SHIPPED_AT))))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(sameInstant(DEFAULT_DELIVERED_AT))))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(sameInstant(DEFAULT_CONFIRMED_AT))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliveryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(deliveryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliveryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(deliveryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDelivery() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get the delivery
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, delivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(delivery.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.deliveryNumber").value(DEFAULT_DELIVERY_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.trackingNumber").value(DEFAULT_TRACKING_NUMBER))
            .andExpect(jsonPath("$.shippedAt").value(sameInstant(DEFAULT_SHIPPED_AT)))
            .andExpect(jsonPath("$.deliveredAt").value(sameInstant(DEFAULT_DELIVERED_AT)))
            .andExpect(jsonPath("$.confirmedAt").value(sameInstant(DEFAULT_CONFIRMED_AT)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getDeliveriesByIdFiltering() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        Long id = delivery.getId();

        defaultDeliveryShouldBeFound("id.equals=" + id);
        defaultDeliveryShouldNotBeFound("id.notEquals=" + id);

        defaultDeliveryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDeliveryShouldNotBeFound("id.greaterThan=" + id);

        defaultDeliveryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDeliveryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId equals to DEFAULT_TENANT_ID
        defaultDeliveryShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the deliveryList where tenantId equals to UPDATED_TENANT_ID
        defaultDeliveryShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultDeliveryShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the deliveryList where tenantId equals to UPDATED_TENANT_ID
        defaultDeliveryShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId is not null
        defaultDeliveryShouldBeFound("tenantId.specified=true");

        // Get all the deliveryList where tenantId is null
        defaultDeliveryShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultDeliveryShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the deliveryList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultDeliveryShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultDeliveryShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the deliveryList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultDeliveryShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId is less than DEFAULT_TENANT_ID
        defaultDeliveryShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the deliveryList where tenantId is less than UPDATED_TENANT_ID
        defaultDeliveryShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where tenantId is greater than DEFAULT_TENANT_ID
        defaultDeliveryShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the deliveryList where tenantId is greater than SMALLER_TENANT_ID
        defaultDeliveryShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryNumber equals to DEFAULT_DELIVERY_NUMBER
        defaultDeliveryShouldBeFound("deliveryNumber.equals=" + DEFAULT_DELIVERY_NUMBER);

        // Get all the deliveryList where deliveryNumber equals to UPDATED_DELIVERY_NUMBER
        defaultDeliveryShouldNotBeFound("deliveryNumber.equals=" + UPDATED_DELIVERY_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryNumberIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryNumber in DEFAULT_DELIVERY_NUMBER or UPDATED_DELIVERY_NUMBER
        defaultDeliveryShouldBeFound("deliveryNumber.in=" + DEFAULT_DELIVERY_NUMBER + "," + UPDATED_DELIVERY_NUMBER);

        // Get all the deliveryList where deliveryNumber equals to UPDATED_DELIVERY_NUMBER
        defaultDeliveryShouldNotBeFound("deliveryNumber.in=" + UPDATED_DELIVERY_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryNumber is not null
        defaultDeliveryShouldBeFound("deliveryNumber.specified=true");

        // Get all the deliveryList where deliveryNumber is null
        defaultDeliveryShouldNotBeFound("deliveryNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryNumberContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryNumber contains DEFAULT_DELIVERY_NUMBER
        defaultDeliveryShouldBeFound("deliveryNumber.contains=" + DEFAULT_DELIVERY_NUMBER);

        // Get all the deliveryList where deliveryNumber contains UPDATED_DELIVERY_NUMBER
        defaultDeliveryShouldNotBeFound("deliveryNumber.contains=" + UPDATED_DELIVERY_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryNumberNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryNumber does not contain DEFAULT_DELIVERY_NUMBER
        defaultDeliveryShouldNotBeFound("deliveryNumber.doesNotContain=" + DEFAULT_DELIVERY_NUMBER);

        // Get all the deliveryList where deliveryNumber does not contain UPDATED_DELIVERY_NUMBER
        defaultDeliveryShouldBeFound("deliveryNumber.doesNotContain=" + UPDATED_DELIVERY_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where status equals to DEFAULT_STATUS
        defaultDeliveryShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the deliveryList where status equals to UPDATED_STATUS
        defaultDeliveryShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDeliveriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultDeliveryShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the deliveryList where status equals to UPDATED_STATUS
        defaultDeliveryShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDeliveriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where status is not null
        defaultDeliveryShouldBeFound("status.specified=true");

        // Get all the deliveryList where status is null
        defaultDeliveryShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByTrackingNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where trackingNumber equals to DEFAULT_TRACKING_NUMBER
        defaultDeliveryShouldBeFound("trackingNumber.equals=" + DEFAULT_TRACKING_NUMBER);

        // Get all the deliveryList where trackingNumber equals to UPDATED_TRACKING_NUMBER
        defaultDeliveryShouldNotBeFound("trackingNumber.equals=" + UPDATED_TRACKING_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTrackingNumberIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where trackingNumber in DEFAULT_TRACKING_NUMBER or UPDATED_TRACKING_NUMBER
        defaultDeliveryShouldBeFound("trackingNumber.in=" + DEFAULT_TRACKING_NUMBER + "," + UPDATED_TRACKING_NUMBER);

        // Get all the deliveryList where trackingNumber equals to UPDATED_TRACKING_NUMBER
        defaultDeliveryShouldNotBeFound("trackingNumber.in=" + UPDATED_TRACKING_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTrackingNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where trackingNumber is not null
        defaultDeliveryShouldBeFound("trackingNumber.specified=true");

        // Get all the deliveryList where trackingNumber is null
        defaultDeliveryShouldNotBeFound("trackingNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByTrackingNumberContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where trackingNumber contains DEFAULT_TRACKING_NUMBER
        defaultDeliveryShouldBeFound("trackingNumber.contains=" + DEFAULT_TRACKING_NUMBER);

        // Get all the deliveryList where trackingNumber contains UPDATED_TRACKING_NUMBER
        defaultDeliveryShouldNotBeFound("trackingNumber.contains=" + UPDATED_TRACKING_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByTrackingNumberNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where trackingNumber does not contain DEFAULT_TRACKING_NUMBER
        defaultDeliveryShouldNotBeFound("trackingNumber.doesNotContain=" + DEFAULT_TRACKING_NUMBER);

        // Get all the deliveryList where trackingNumber does not contain UPDATED_TRACKING_NUMBER
        defaultDeliveryShouldBeFound("trackingNumber.doesNotContain=" + UPDATED_TRACKING_NUMBER);
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt equals to DEFAULT_SHIPPED_AT
        defaultDeliveryShouldBeFound("shippedAt.equals=" + DEFAULT_SHIPPED_AT);

        // Get all the deliveryList where shippedAt equals to UPDATED_SHIPPED_AT
        defaultDeliveryShouldNotBeFound("shippedAt.equals=" + UPDATED_SHIPPED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt in DEFAULT_SHIPPED_AT or UPDATED_SHIPPED_AT
        defaultDeliveryShouldBeFound("shippedAt.in=" + DEFAULT_SHIPPED_AT + "," + UPDATED_SHIPPED_AT);

        // Get all the deliveryList where shippedAt equals to UPDATED_SHIPPED_AT
        defaultDeliveryShouldNotBeFound("shippedAt.in=" + UPDATED_SHIPPED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt is not null
        defaultDeliveryShouldBeFound("shippedAt.specified=true");

        // Get all the deliveryList where shippedAt is null
        defaultDeliveryShouldNotBeFound("shippedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt is greater than or equal to DEFAULT_SHIPPED_AT
        defaultDeliveryShouldBeFound("shippedAt.greaterThanOrEqual=" + DEFAULT_SHIPPED_AT);

        // Get all the deliveryList where shippedAt is greater than or equal to UPDATED_SHIPPED_AT
        defaultDeliveryShouldNotBeFound("shippedAt.greaterThanOrEqual=" + UPDATED_SHIPPED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt is less than or equal to DEFAULT_SHIPPED_AT
        defaultDeliveryShouldBeFound("shippedAt.lessThanOrEqual=" + DEFAULT_SHIPPED_AT);

        // Get all the deliveryList where shippedAt is less than or equal to SMALLER_SHIPPED_AT
        defaultDeliveryShouldNotBeFound("shippedAt.lessThanOrEqual=" + SMALLER_SHIPPED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt is less than DEFAULT_SHIPPED_AT
        defaultDeliveryShouldNotBeFound("shippedAt.lessThan=" + DEFAULT_SHIPPED_AT);

        // Get all the deliveryList where shippedAt is less than UPDATED_SHIPPED_AT
        defaultDeliveryShouldBeFound("shippedAt.lessThan=" + UPDATED_SHIPPED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByShippedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where shippedAt is greater than DEFAULT_SHIPPED_AT
        defaultDeliveryShouldNotBeFound("shippedAt.greaterThan=" + DEFAULT_SHIPPED_AT);

        // Get all the deliveryList where shippedAt is greater than SMALLER_SHIPPED_AT
        defaultDeliveryShouldBeFound("shippedAt.greaterThan=" + SMALLER_SHIPPED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt equals to DEFAULT_DELIVERED_AT
        defaultDeliveryShouldBeFound("deliveredAt.equals=" + DEFAULT_DELIVERED_AT);

        // Get all the deliveryList where deliveredAt equals to UPDATED_DELIVERED_AT
        defaultDeliveryShouldNotBeFound("deliveredAt.equals=" + UPDATED_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt in DEFAULT_DELIVERED_AT or UPDATED_DELIVERED_AT
        defaultDeliveryShouldBeFound("deliveredAt.in=" + DEFAULT_DELIVERED_AT + "," + UPDATED_DELIVERED_AT);

        // Get all the deliveryList where deliveredAt equals to UPDATED_DELIVERED_AT
        defaultDeliveryShouldNotBeFound("deliveredAt.in=" + UPDATED_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt is not null
        defaultDeliveryShouldBeFound("deliveredAt.specified=true");

        // Get all the deliveryList where deliveredAt is null
        defaultDeliveryShouldNotBeFound("deliveredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt is greater than or equal to DEFAULT_DELIVERED_AT
        defaultDeliveryShouldBeFound("deliveredAt.greaterThanOrEqual=" + DEFAULT_DELIVERED_AT);

        // Get all the deliveryList where deliveredAt is greater than or equal to UPDATED_DELIVERED_AT
        defaultDeliveryShouldNotBeFound("deliveredAt.greaterThanOrEqual=" + UPDATED_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt is less than or equal to DEFAULT_DELIVERED_AT
        defaultDeliveryShouldBeFound("deliveredAt.lessThanOrEqual=" + DEFAULT_DELIVERED_AT);

        // Get all the deliveryList where deliveredAt is less than or equal to SMALLER_DELIVERED_AT
        defaultDeliveryShouldNotBeFound("deliveredAt.lessThanOrEqual=" + SMALLER_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt is less than DEFAULT_DELIVERED_AT
        defaultDeliveryShouldNotBeFound("deliveredAt.lessThan=" + DEFAULT_DELIVERED_AT);

        // Get all the deliveryList where deliveredAt is less than UPDATED_DELIVERED_AT
        defaultDeliveryShouldBeFound("deliveredAt.lessThan=" + UPDATED_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveredAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveredAt is greater than DEFAULT_DELIVERED_AT
        defaultDeliveryShouldNotBeFound("deliveredAt.greaterThan=" + DEFAULT_DELIVERED_AT);

        // Get all the deliveryList where deliveredAt is greater than SMALLER_DELIVERED_AT
        defaultDeliveryShouldBeFound("deliveredAt.greaterThan=" + SMALLER_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt equals to DEFAULT_CONFIRMED_AT
        defaultDeliveryShouldBeFound("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT);

        // Get all the deliveryList where confirmedAt equals to UPDATED_CONFIRMED_AT
        defaultDeliveryShouldNotBeFound("confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt in DEFAULT_CONFIRMED_AT or UPDATED_CONFIRMED_AT
        defaultDeliveryShouldBeFound("confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT);

        // Get all the deliveryList where confirmedAt equals to UPDATED_CONFIRMED_AT
        defaultDeliveryShouldNotBeFound("confirmedAt.in=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt is not null
        defaultDeliveryShouldBeFound("confirmedAt.specified=true");

        // Get all the deliveryList where confirmedAt is null
        defaultDeliveryShouldNotBeFound("confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt is greater than or equal to DEFAULT_CONFIRMED_AT
        defaultDeliveryShouldBeFound("confirmedAt.greaterThanOrEqual=" + DEFAULT_CONFIRMED_AT);

        // Get all the deliveryList where confirmedAt is greater than or equal to UPDATED_CONFIRMED_AT
        defaultDeliveryShouldNotBeFound("confirmedAt.greaterThanOrEqual=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt is less than or equal to DEFAULT_CONFIRMED_AT
        defaultDeliveryShouldBeFound("confirmedAt.lessThanOrEqual=" + DEFAULT_CONFIRMED_AT);

        // Get all the deliveryList where confirmedAt is less than or equal to SMALLER_CONFIRMED_AT
        defaultDeliveryShouldNotBeFound("confirmedAt.lessThanOrEqual=" + SMALLER_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt is less than DEFAULT_CONFIRMED_AT
        defaultDeliveryShouldNotBeFound("confirmedAt.lessThan=" + DEFAULT_CONFIRMED_AT);

        // Get all the deliveryList where confirmedAt is less than UPDATED_CONFIRMED_AT
        defaultDeliveryShouldBeFound("confirmedAt.lessThan=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByConfirmedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where confirmedAt is greater than DEFAULT_CONFIRMED_AT
        defaultDeliveryShouldNotBeFound("confirmedAt.greaterThan=" + DEFAULT_CONFIRMED_AT);

        // Get all the deliveryList where confirmedAt is greater than SMALLER_CONFIRMED_AT
        defaultDeliveryShouldBeFound("confirmedAt.greaterThan=" + SMALLER_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where notes equals to DEFAULT_NOTES
        defaultDeliveryShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the deliveryList where notes equals to UPDATED_NOTES
        defaultDeliveryShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDeliveriesByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultDeliveryShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the deliveryList where notes equals to UPDATED_NOTES
        defaultDeliveryShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDeliveriesByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where notes is not null
        defaultDeliveryShouldBeFound("notes.specified=true");

        // Get all the deliveryList where notes is null
        defaultDeliveryShouldNotBeFound("notes.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByNotesContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where notes contains DEFAULT_NOTES
        defaultDeliveryShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the deliveryList where notes contains UPDATED_NOTES
        defaultDeliveryShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDeliveriesByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where notes does not contain DEFAULT_NOTES
        defaultDeliveryShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the deliveryList where notes does not contain UPDATED_NOTES
        defaultDeliveryShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt equals to DEFAULT_CREATED_AT
        defaultDeliveryShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the deliveryList where createdAt equals to UPDATED_CREATED_AT
        defaultDeliveryShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultDeliveryShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the deliveryList where createdAt equals to UPDATED_CREATED_AT
        defaultDeliveryShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is not null
        defaultDeliveryShouldBeFound("createdAt.specified=true");

        // Get all the deliveryList where createdAt is null
        defaultDeliveryShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultDeliveryShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the deliveryList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultDeliveryShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultDeliveryShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the deliveryList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultDeliveryShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is less than DEFAULT_CREATED_AT
        defaultDeliveryShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the deliveryList where createdAt is less than UPDATED_CREATED_AT
        defaultDeliveryShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is greater than DEFAULT_CREATED_AT
        defaultDeliveryShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the deliveryList where createdAt is greater than SMALLER_CREATED_AT
        defaultDeliveryShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDeliveriesByOrderIsEqualToSomething() throws Exception {
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            deliveryRepository.saveAndFlush(delivery);
            order = OrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        delivery.setOrder(order);
        deliveryRepository.saveAndFlush(delivery);
        Long orderId = order.getId();

        // Get all the deliveryList where order equals to orderId
        defaultDeliveryShouldBeFound("orderId.equals=" + orderId);

        // Get all the deliveryList where order equals to (orderId + 1)
        defaultDeliveryShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeliveryShouldBeFound(String filter) throws Exception {
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].deliveryNumber").value(hasItem(DEFAULT_DELIVERY_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].trackingNumber").value(hasItem(DEFAULT_TRACKING_NUMBER)))
            .andExpect(jsonPath("$.[*].shippedAt").value(hasItem(sameInstant(DEFAULT_SHIPPED_AT))))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(sameInstant(DEFAULT_DELIVERED_AT))))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(sameInstant(DEFAULT_CONFIRMED_AT))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

        // Check, that the count call also returns 1
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeliveryShouldNotBeFound(String filter) throws Exception {
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDelivery() throws Exception {
        // Get the delivery
        restDeliveryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDelivery() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();

        // Update the delivery
        Delivery updatedDelivery = deliveryRepository.findById(delivery.getId()).get();
        // Disconnect from session so that the updates on updatedDelivery are not directly saved in db
        em.detach(updatedDelivery);
        updatedDelivery
            .tenantId(UPDATED_TENANT_ID)
            .deliveryNumber(UPDATED_DELIVERY_NUMBER)
            .status(UPDATED_STATUS)
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .shippedAt(UPDATED_SHIPPED_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(updatedDelivery);

        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deliveryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
        Delivery testDelivery = deliveryList.get(deliveryList.size() - 1);
        assertThat(testDelivery.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testDelivery.getDeliveryNumber()).isEqualTo(UPDATED_DELIVERY_NUMBER);
        assertThat(testDelivery.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDelivery.getTrackingNumber()).isEqualTo(UPDATED_TRACKING_NUMBER);
        assertThat(testDelivery.getShippedAt()).isEqualTo(UPDATED_SHIPPED_AT);
        assertThat(testDelivery.getDeliveredAt()).isEqualTo(UPDATED_DELIVERED_AT);
        assertThat(testDelivery.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
        assertThat(testDelivery.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testDelivery.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingDelivery() throws Exception {
        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();
        delivery.setId(count.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDelivery() throws Exception {
        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();
        delivery.setId(count.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDelivery() throws Exception {
        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();
        delivery.setId(count.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery
            .tenantId(UPDATED_TENANT_ID)
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .shippedAt(UPDATED_SHIPPED_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
        Delivery testDelivery = deliveryList.get(deliveryList.size() - 1);
        assertThat(testDelivery.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testDelivery.getDeliveryNumber()).isEqualTo(DEFAULT_DELIVERY_NUMBER);
        assertThat(testDelivery.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDelivery.getTrackingNumber()).isEqualTo(UPDATED_TRACKING_NUMBER);
        assertThat(testDelivery.getShippedAt()).isEqualTo(UPDATED_SHIPPED_AT);
        assertThat(testDelivery.getDeliveredAt()).isEqualTo(UPDATED_DELIVERED_AT);
        assertThat(testDelivery.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
        assertThat(testDelivery.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testDelivery.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery
            .tenantId(UPDATED_TENANT_ID)
            .deliveryNumber(UPDATED_DELIVERY_NUMBER)
            .status(UPDATED_STATUS)
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .shippedAt(UPDATED_SHIPPED_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
        Delivery testDelivery = deliveryList.get(deliveryList.size() - 1);
        assertThat(testDelivery.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testDelivery.getDeliveryNumber()).isEqualTo(UPDATED_DELIVERY_NUMBER);
        assertThat(testDelivery.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDelivery.getTrackingNumber()).isEqualTo(UPDATED_TRACKING_NUMBER);
        assertThat(testDelivery.getShippedAt()).isEqualTo(UPDATED_SHIPPED_AT);
        assertThat(testDelivery.getDeliveredAt()).isEqualTo(UPDATED_DELIVERED_AT);
        assertThat(testDelivery.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
        assertThat(testDelivery.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testDelivery.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingDelivery() throws Exception {
        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();
        delivery.setId(count.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDelivery() throws Exception {
        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();
        delivery.setId(count.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDelivery() throws Exception {
        int databaseSizeBeforeUpdate = deliveryRepository.findAll().size();
        delivery.setId(count.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(deliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDelivery() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        int databaseSizeBeforeDelete = deliveryRepository.findAll().size();

        // Delete the delivery
        restDeliveryMockMvc
            .perform(delete(ENTITY_API_URL_ID, delivery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Delivery> deliveryList = deliveryRepository.findAll();
        assertThat(deliveryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
