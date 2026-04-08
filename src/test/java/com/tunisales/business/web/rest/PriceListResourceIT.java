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
import com.tunisales.business.domain.PriceList;
import com.tunisales.business.domain.Product;
import com.tunisales.business.repository.PriceListRepository;
import com.tunisales.business.service.PriceListService;
import com.tunisales.business.service.criteria.PriceListCriteria;
import com.tunisales.business.service.dto.PriceListDTO;
import com.tunisales.business.service.mapper.PriceListMapper;
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
 * Integration tests for the {@link PriceListResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PriceListResourceIT {

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MAX_DISCOUNT_PCT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MAX_DISCOUNT_PCT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MAX_DISCOUNT_PCT = new BigDecimal(0 - 1);

    private static final ZonedDateTime DEFAULT_VALID_FROM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALID_FROM = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_VALID_FROM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_VALID_TO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALID_TO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_VALID_TO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/price-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PriceListRepository priceListRepository;

    @Mock
    private PriceListRepository priceListRepositoryMock;

    @Autowired
    private PriceListMapper priceListMapper;

    @Mock
    private PriceListService priceListServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPriceListMockMvc;

    private PriceList priceList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PriceList createEntity(EntityManager em) {
        PriceList priceList = new PriceList()
            .unitPrice(DEFAULT_UNIT_PRICE)
            .maxDiscountPct(DEFAULT_MAX_DISCOUNT_PCT)
            .validFrom(DEFAULT_VALID_FROM)
            .validTo(DEFAULT_VALID_TO)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        priceList.setProduct(product);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        priceList.setClient(client);
        return priceList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PriceList createUpdatedEntity(EntityManager em) {
        PriceList priceList = new PriceList()
            .unitPrice(UPDATED_UNIT_PRICE)
            .maxDiscountPct(UPDATED_MAX_DISCOUNT_PCT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        priceList.setProduct(product);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        priceList.setClient(client);
        return priceList;
    }

    @BeforeEach
    public void initTest() {
        priceList = createEntity(em);
    }

    @Test
    @Transactional
    void createPriceList() throws Exception {
        int databaseSizeBeforeCreate = priceListRepository.findAll().size();
        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);
        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isCreated());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeCreate + 1);
        PriceList testPriceList = priceListList.get(priceListList.size() - 1);
        assertThat(testPriceList.getUnitPrice()).isEqualByComparingTo(DEFAULT_UNIT_PRICE);
        assertThat(testPriceList.getMaxDiscountPct()).isEqualByComparingTo(DEFAULT_MAX_DISCOUNT_PCT);
        assertThat(testPriceList.getValidFrom()).isEqualTo(DEFAULT_VALID_FROM);
        assertThat(testPriceList.getValidTo()).isEqualTo(DEFAULT_VALID_TO);
        assertThat(testPriceList.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testPriceList.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createPriceListWithExistingId() throws Exception {
        // Create the PriceList with an existing ID
        priceList.setId(1L);
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        int databaseSizeBeforeCreate = priceListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceListRepository.findAll().size();
        // set the field null
        priceList.setUnitPrice(null);

        // Create the PriceList, which fails.
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isBadRequest());

        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidFromIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceListRepository.findAll().size();
        // set the field null
        priceList.setValidFrom(null);

        // Create the PriceList, which fails.
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isBadRequest());

        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidToIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceListRepository.findAll().size();
        // set the field null
        priceList.setValidTo(null);

        // Create the PriceList, which fails.
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isBadRequest());

        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceListRepository.findAll().size();
        // set the field null
        priceList.setIsActive(null);

        // Create the PriceList, which fails.
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isBadRequest());

        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceListRepository.findAll().size();
        // set the field null
        priceList.setCreatedAt(null);

        // Create the PriceList, which fails.
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        restPriceListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isBadRequest());

        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPriceLists() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList
        restPriceListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(priceList.getId().intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].maxDiscountPct").value(hasItem(sameNumber(DEFAULT_MAX_DISCOUNT_PCT))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(sameInstant(DEFAULT_VALID_FROM))))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(sameInstant(DEFAULT_VALID_TO))))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPriceListsWithEagerRelationshipsIsEnabled() throws Exception {
        when(priceListServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPriceListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(priceListServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPriceListsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(priceListServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPriceListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(priceListRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPriceList() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get the priceList
        restPriceListMockMvc
            .perform(get(ENTITY_API_URL_ID, priceList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(priceList.getId().intValue()))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.maxDiscountPct").value(sameNumber(DEFAULT_MAX_DISCOUNT_PCT)))
            .andExpect(jsonPath("$.validFrom").value(sameInstant(DEFAULT_VALID_FROM)))
            .andExpect(jsonPath("$.validTo").value(sameInstant(DEFAULT_VALID_TO)))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getPriceListsByIdFiltering() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        Long id = priceList.getId();

        defaultPriceListShouldBeFound("id.equals=" + id);
        defaultPriceListShouldNotBeFound("id.notEquals=" + id);

        defaultPriceListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPriceListShouldNotBeFound("id.greaterThan=" + id);

        defaultPriceListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPriceListShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice equals to DEFAULT_UNIT_PRICE
        defaultPriceListShouldBeFound("unitPrice.equals=" + DEFAULT_UNIT_PRICE);

        // Get all the priceListList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultPriceListShouldNotBeFound("unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice in DEFAULT_UNIT_PRICE or UPDATED_UNIT_PRICE
        defaultPriceListShouldBeFound("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE);

        // Get all the priceListList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultPriceListShouldNotBeFound("unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice is not null
        defaultPriceListShouldBeFound("unitPrice.specified=true");

        // Get all the priceListList where unitPrice is null
        defaultPriceListShouldNotBeFound("unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice is greater than or equal to DEFAULT_UNIT_PRICE
        defaultPriceListShouldBeFound("unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the priceListList where unitPrice is greater than or equal to UPDATED_UNIT_PRICE
        defaultPriceListShouldNotBeFound("unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice is less than or equal to DEFAULT_UNIT_PRICE
        defaultPriceListShouldBeFound("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the priceListList where unitPrice is less than or equal to SMALLER_UNIT_PRICE
        defaultPriceListShouldNotBeFound("unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice is less than DEFAULT_UNIT_PRICE
        defaultPriceListShouldNotBeFound("unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);

        // Get all the priceListList where unitPrice is less than UPDATED_UNIT_PRICE
        defaultPriceListShouldBeFound("unitPrice.lessThan=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPriceListsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where unitPrice is greater than DEFAULT_UNIT_PRICE
        defaultPriceListShouldNotBeFound("unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);

        // Get all the priceListList where unitPrice is greater than SMALLER_UNIT_PRICE
        defaultPriceListShouldBeFound("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct equals to DEFAULT_MAX_DISCOUNT_PCT
        defaultPriceListShouldBeFound("maxDiscountPct.equals=" + DEFAULT_MAX_DISCOUNT_PCT);

        // Get all the priceListList where maxDiscountPct equals to UPDATED_MAX_DISCOUNT_PCT
        defaultPriceListShouldNotBeFound("maxDiscountPct.equals=" + UPDATED_MAX_DISCOUNT_PCT);
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsInShouldWork() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct in DEFAULT_MAX_DISCOUNT_PCT or UPDATED_MAX_DISCOUNT_PCT
        defaultPriceListShouldBeFound("maxDiscountPct.in=" + DEFAULT_MAX_DISCOUNT_PCT + "," + UPDATED_MAX_DISCOUNT_PCT);

        // Get all the priceListList where maxDiscountPct equals to UPDATED_MAX_DISCOUNT_PCT
        defaultPriceListShouldNotBeFound("maxDiscountPct.in=" + UPDATED_MAX_DISCOUNT_PCT);
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsNullOrNotNull() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct is not null
        defaultPriceListShouldBeFound("maxDiscountPct.specified=true");

        // Get all the priceListList where maxDiscountPct is null
        defaultPriceListShouldNotBeFound("maxDiscountPct.specified=false");
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct is greater than or equal to DEFAULT_MAX_DISCOUNT_PCT
        defaultPriceListShouldBeFound("maxDiscountPct.greaterThanOrEqual=" + DEFAULT_MAX_DISCOUNT_PCT);

        // Get all the priceListList where maxDiscountPct is greater than or equal to (DEFAULT_MAX_DISCOUNT_PCT.add(BigDecimal.ONE))
        defaultPriceListShouldNotBeFound("maxDiscountPct.greaterThanOrEqual=" + (DEFAULT_MAX_DISCOUNT_PCT.add(BigDecimal.ONE)));
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct is less than or equal to DEFAULT_MAX_DISCOUNT_PCT
        defaultPriceListShouldBeFound("maxDiscountPct.lessThanOrEqual=" + DEFAULT_MAX_DISCOUNT_PCT);

        // Get all the priceListList where maxDiscountPct is less than or equal to SMALLER_MAX_DISCOUNT_PCT
        defaultPriceListShouldNotBeFound("maxDiscountPct.lessThanOrEqual=" + SMALLER_MAX_DISCOUNT_PCT);
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsLessThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct is less than DEFAULT_MAX_DISCOUNT_PCT
        defaultPriceListShouldNotBeFound("maxDiscountPct.lessThan=" + DEFAULT_MAX_DISCOUNT_PCT);

        // Get all the priceListList where maxDiscountPct is less than (DEFAULT_MAX_DISCOUNT_PCT.add(BigDecimal.ONE))
        defaultPriceListShouldBeFound("maxDiscountPct.lessThan=" + (DEFAULT_MAX_DISCOUNT_PCT.add(BigDecimal.ONE)));
    }

    @Test
    @Transactional
    void getAllPriceListsByMaxDiscountPctIsGreaterThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where maxDiscountPct is greater than DEFAULT_MAX_DISCOUNT_PCT
        defaultPriceListShouldNotBeFound("maxDiscountPct.greaterThan=" + DEFAULT_MAX_DISCOUNT_PCT);

        // Get all the priceListList where maxDiscountPct is greater than SMALLER_MAX_DISCOUNT_PCT
        defaultPriceListShouldBeFound("maxDiscountPct.greaterThan=" + SMALLER_MAX_DISCOUNT_PCT);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom equals to DEFAULT_VALID_FROM
        defaultPriceListShouldBeFound("validFrom.equals=" + DEFAULT_VALID_FROM);

        // Get all the priceListList where validFrom equals to UPDATED_VALID_FROM
        defaultPriceListShouldNotBeFound("validFrom.equals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsInShouldWork() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom in DEFAULT_VALID_FROM or UPDATED_VALID_FROM
        defaultPriceListShouldBeFound("validFrom.in=" + DEFAULT_VALID_FROM + "," + UPDATED_VALID_FROM);

        // Get all the priceListList where validFrom equals to UPDATED_VALID_FROM
        defaultPriceListShouldNotBeFound("validFrom.in=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom is not null
        defaultPriceListShouldBeFound("validFrom.specified=true");

        // Get all the priceListList where validFrom is null
        defaultPriceListShouldNotBeFound("validFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom is greater than or equal to DEFAULT_VALID_FROM
        defaultPriceListShouldBeFound("validFrom.greaterThanOrEqual=" + DEFAULT_VALID_FROM);

        // Get all the priceListList where validFrom is greater than or equal to UPDATED_VALID_FROM
        defaultPriceListShouldNotBeFound("validFrom.greaterThanOrEqual=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom is less than or equal to DEFAULT_VALID_FROM
        defaultPriceListShouldBeFound("validFrom.lessThanOrEqual=" + DEFAULT_VALID_FROM);

        // Get all the priceListList where validFrom is less than or equal to SMALLER_VALID_FROM
        defaultPriceListShouldNotBeFound("validFrom.lessThanOrEqual=" + SMALLER_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsLessThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom is less than DEFAULT_VALID_FROM
        defaultPriceListShouldNotBeFound("validFrom.lessThan=" + DEFAULT_VALID_FROM);

        // Get all the priceListList where validFrom is less than UPDATED_VALID_FROM
        defaultPriceListShouldBeFound("validFrom.lessThan=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidFromIsGreaterThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validFrom is greater than DEFAULT_VALID_FROM
        defaultPriceListShouldNotBeFound("validFrom.greaterThan=" + DEFAULT_VALID_FROM);

        // Get all the priceListList where validFrom is greater than SMALLER_VALID_FROM
        defaultPriceListShouldBeFound("validFrom.greaterThan=" + SMALLER_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo equals to DEFAULT_VALID_TO
        defaultPriceListShouldBeFound("validTo.equals=" + DEFAULT_VALID_TO);

        // Get all the priceListList where validTo equals to UPDATED_VALID_TO
        defaultPriceListShouldNotBeFound("validTo.equals=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsInShouldWork() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo in DEFAULT_VALID_TO or UPDATED_VALID_TO
        defaultPriceListShouldBeFound("validTo.in=" + DEFAULT_VALID_TO + "," + UPDATED_VALID_TO);

        // Get all the priceListList where validTo equals to UPDATED_VALID_TO
        defaultPriceListShouldNotBeFound("validTo.in=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsNullOrNotNull() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo is not null
        defaultPriceListShouldBeFound("validTo.specified=true");

        // Get all the priceListList where validTo is null
        defaultPriceListShouldNotBeFound("validTo.specified=false");
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo is greater than or equal to DEFAULT_VALID_TO
        defaultPriceListShouldBeFound("validTo.greaterThanOrEqual=" + DEFAULT_VALID_TO);

        // Get all the priceListList where validTo is greater than or equal to UPDATED_VALID_TO
        defaultPriceListShouldNotBeFound("validTo.greaterThanOrEqual=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo is less than or equal to DEFAULT_VALID_TO
        defaultPriceListShouldBeFound("validTo.lessThanOrEqual=" + DEFAULT_VALID_TO);

        // Get all the priceListList where validTo is less than or equal to SMALLER_VALID_TO
        defaultPriceListShouldNotBeFound("validTo.lessThanOrEqual=" + SMALLER_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsLessThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo is less than DEFAULT_VALID_TO
        defaultPriceListShouldNotBeFound("validTo.lessThan=" + DEFAULT_VALID_TO);

        // Get all the priceListList where validTo is less than UPDATED_VALID_TO
        defaultPriceListShouldBeFound("validTo.lessThan=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPriceListsByValidToIsGreaterThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where validTo is greater than DEFAULT_VALID_TO
        defaultPriceListShouldNotBeFound("validTo.greaterThan=" + DEFAULT_VALID_TO);

        // Get all the priceListList where validTo is greater than SMALLER_VALID_TO
        defaultPriceListShouldBeFound("validTo.greaterThan=" + SMALLER_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPriceListsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where isActive equals to DEFAULT_IS_ACTIVE
        defaultPriceListShouldBeFound("isActive.equals=" + DEFAULT_IS_ACTIVE);

        // Get all the priceListList where isActive equals to UPDATED_IS_ACTIVE
        defaultPriceListShouldNotBeFound("isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPriceListsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where isActive in DEFAULT_IS_ACTIVE or UPDATED_IS_ACTIVE
        defaultPriceListShouldBeFound("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE);

        // Get all the priceListList where isActive equals to UPDATED_IS_ACTIVE
        defaultPriceListShouldNotBeFound("isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPriceListsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where isActive is not null
        defaultPriceListShouldBeFound("isActive.specified=true");

        // Get all the priceListList where isActive is null
        defaultPriceListShouldNotBeFound("isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt equals to DEFAULT_CREATED_AT
        defaultPriceListShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the priceListList where createdAt equals to UPDATED_CREATED_AT
        defaultPriceListShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultPriceListShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the priceListList where createdAt equals to UPDATED_CREATED_AT
        defaultPriceListShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt is not null
        defaultPriceListShouldBeFound("createdAt.specified=true");

        // Get all the priceListList where createdAt is null
        defaultPriceListShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultPriceListShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the priceListList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultPriceListShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultPriceListShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the priceListList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultPriceListShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt is less than DEFAULT_CREATED_AT
        defaultPriceListShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the priceListList where createdAt is less than UPDATED_CREATED_AT
        defaultPriceListShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPriceListsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        // Get all the priceListList where createdAt is greater than DEFAULT_CREATED_AT
        defaultPriceListShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the priceListList where createdAt is greater than SMALLER_CREATED_AT
        defaultPriceListShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPriceListsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            priceListRepository.saveAndFlush(priceList);
            product = ProductResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        priceList.setProduct(product);
        priceListRepository.saveAndFlush(priceList);
        Long productId = product.getId();

        // Get all the priceListList where product equals to productId
        defaultPriceListShouldBeFound("productId.equals=" + productId);

        // Get all the priceListList where product equals to (productId + 1)
        defaultPriceListShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    @Test
    @Transactional
    void getAllPriceListsByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            priceListRepository.saveAndFlush(priceList);
            client = ClientResourceIT.createEntity(em);
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        priceList.setClient(client);
        priceListRepository.saveAndFlush(priceList);
        Long clientId = client.getId();

        // Get all the priceListList where client equals to clientId
        defaultPriceListShouldBeFound("clientId.equals=" + clientId);

        // Get all the priceListList where client equals to (clientId + 1)
        defaultPriceListShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPriceListShouldBeFound(String filter) throws Exception {
        restPriceListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(priceList.getId().intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].maxDiscountPct").value(hasItem(sameNumber(DEFAULT_MAX_DISCOUNT_PCT))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(sameInstant(DEFAULT_VALID_FROM))))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(sameInstant(DEFAULT_VALID_TO))))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

        // Check, that the count call also returns 1
        restPriceListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPriceListShouldNotBeFound(String filter) throws Exception {
        restPriceListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPriceListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPriceList() throws Exception {
        // Get the priceList
        restPriceListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPriceList() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();

        // Update the priceList
        PriceList updatedPriceList = priceListRepository.findById(priceList.getId()).get();
        // Disconnect from session so that the updates on updatedPriceList are not directly saved in db
        em.detach(updatedPriceList);
        updatedPriceList
            .unitPrice(UPDATED_UNIT_PRICE)
            .maxDiscountPct(UPDATED_MAX_DISCOUNT_PCT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
        PriceListDTO priceListDTO = priceListMapper.toDto(updatedPriceList);

        restPriceListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, priceListDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(priceListDTO))
            )
            .andExpect(status().isOk());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
        PriceList testPriceList = priceListList.get(priceListList.size() - 1);
        assertThat(testPriceList.getUnitPrice()).isEqualByComparingTo(UPDATED_UNIT_PRICE);
        assertThat(testPriceList.getMaxDiscountPct()).isEqualByComparingTo(UPDATED_MAX_DISCOUNT_PCT);
        assertThat(testPriceList.getValidFrom()).isEqualTo(UPDATED_VALID_FROM);
        assertThat(testPriceList.getValidTo()).isEqualTo(UPDATED_VALID_TO);
        assertThat(testPriceList.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testPriceList.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingPriceList() throws Exception {
        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();
        priceList.setId(count.incrementAndGet());

        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPriceListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, priceListDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(priceListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPriceList() throws Exception {
        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();
        priceList.setId(count.incrementAndGet());

        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(priceListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPriceList() throws Exception {
        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();
        priceList.setId(count.incrementAndGet());

        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(priceListDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePriceListWithPatch() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();

        // Update the priceList using partial update
        PriceList partialUpdatedPriceList = new PriceList();
        partialUpdatedPriceList.setId(priceList.getId());

        partialUpdatedPriceList.unitPrice(UPDATED_UNIT_PRICE).maxDiscountPct(UPDATED_MAX_DISCOUNT_PCT).validTo(UPDATED_VALID_TO);

        restPriceListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPriceList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPriceList))
            )
            .andExpect(status().isOk());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
        PriceList testPriceList = priceListList.get(priceListList.size() - 1);
        assertThat(testPriceList.getUnitPrice()).isEqualByComparingTo(UPDATED_UNIT_PRICE);
        assertThat(testPriceList.getMaxDiscountPct()).isEqualByComparingTo(UPDATED_MAX_DISCOUNT_PCT);
        assertThat(testPriceList.getValidFrom()).isEqualTo(DEFAULT_VALID_FROM);
        assertThat(testPriceList.getValidTo()).isEqualTo(UPDATED_VALID_TO);
        assertThat(testPriceList.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testPriceList.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdatePriceListWithPatch() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();

        // Update the priceList using partial update
        PriceList partialUpdatedPriceList = new PriceList();
        partialUpdatedPriceList.setId(priceList.getId());

        partialUpdatedPriceList
            .unitPrice(UPDATED_UNIT_PRICE)
            .maxDiscountPct(UPDATED_MAX_DISCOUNT_PCT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);

        restPriceListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPriceList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPriceList))
            )
            .andExpect(status().isOk());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
        PriceList testPriceList = priceListList.get(priceListList.size() - 1);
        assertThat(testPriceList.getUnitPrice()).isEqualByComparingTo(UPDATED_UNIT_PRICE);
        assertThat(testPriceList.getMaxDiscountPct()).isEqualByComparingTo(UPDATED_MAX_DISCOUNT_PCT);
        assertThat(testPriceList.getValidFrom()).isEqualTo(UPDATED_VALID_FROM);
        assertThat(testPriceList.getValidTo()).isEqualTo(UPDATED_VALID_TO);
        assertThat(testPriceList.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testPriceList.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingPriceList() throws Exception {
        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();
        priceList.setId(count.incrementAndGet());

        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPriceListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, priceListDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(priceListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPriceList() throws Exception {
        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();
        priceList.setId(count.incrementAndGet());

        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(priceListDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPriceList() throws Exception {
        int databaseSizeBeforeUpdate = priceListRepository.findAll().size();
        priceList.setId(count.incrementAndGet());

        // Create the PriceList
        PriceListDTO priceListDTO = priceListMapper.toDto(priceList);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceListMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(priceListDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PriceList in the database
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePriceList() throws Exception {
        // Initialize the database
        priceListRepository.saveAndFlush(priceList);

        int databaseSizeBeforeDelete = priceListRepository.findAll().size();

        // Delete the priceList
        restPriceListMockMvc
            .perform(delete(ENTITY_API_URL_ID, priceList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PriceList> priceListList = priceListRepository.findAll();
        assertThat(priceListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
