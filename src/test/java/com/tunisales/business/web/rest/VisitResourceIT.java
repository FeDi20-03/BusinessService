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
import com.tunisales.business.domain.Mission;
import com.tunisales.business.domain.Visit;
import com.tunisales.business.domain.enumeration.VisitObjective;
import com.tunisales.business.domain.enumeration.VisitStatus;
import com.tunisales.business.repository.VisitRepository;
import com.tunisales.business.service.VisitService;
import com.tunisales.business.service.criteria.VisitCriteria;
import com.tunisales.business.service.dto.VisitDTO;
import com.tunisales.business.service.mapper.VisitMapper;
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
 * Integration tests for the {@link VisitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VisitResourceIT {

    private static final Integer DEFAULT_VISIT_ORDER = 1;
    private static final Integer UPDATED_VISIT_ORDER = 2;
    private static final Integer SMALLER_VISIT_ORDER = 1 - 1;

    private static final VisitObjective DEFAULT_OBJECTIVE = VisitObjective.SALE;
    private static final VisitObjective UPDATED_OBJECTIVE = VisitObjective.PROSPECTING;

    private static final VisitStatus DEFAULT_STATUS = VisitStatus.PLANNED;
    private static final VisitStatus UPDATED_STATUS = VisitStatus.IN_PROGRESS;

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LATITUDE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LONGITUDE = new BigDecimal(1 - 1);

    private static final ZonedDateTime DEFAULT_CHECKIN_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CHECKIN_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CHECKIN_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_CHECKOUT_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CHECKOUT_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CHECKOUT_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/visits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VisitRepository visitRepository;

    @Mock
    private VisitRepository visitRepositoryMock;

    @Autowired
    private VisitMapper visitMapper;

    @Mock
    private VisitService visitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitMockMvc;

    private Visit visit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createEntity(EntityManager em) {
        Visit visit = new Visit()
            .visitOrder(DEFAULT_VISIT_ORDER)
            .objective(DEFAULT_OBJECTIVE)
            .status(DEFAULT_STATUS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .checkinAt(DEFAULT_CHECKIN_AT)
            .checkoutAt(DEFAULT_CHECKOUT_AT)
            .notes(DEFAULT_NOTES)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        visit.setClient(client);
        // Add required entity
        Mission mission;
        if (TestUtil.findAll(em, Mission.class).isEmpty()) {
            mission = MissionResourceIT.createEntity(em);
            em.persist(mission);
            em.flush();
        } else {
            mission = TestUtil.findAll(em, Mission.class).get(0);
        }
        visit.setMission(mission);
        return visit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createUpdatedEntity(EntityManager em) {
        Visit visit = new Visit()
            .visitOrder(UPDATED_VISIT_ORDER)
            .objective(UPDATED_OBJECTIVE)
            .status(UPDATED_STATUS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .checkinAt(UPDATED_CHECKIN_AT)
            .checkoutAt(UPDATED_CHECKOUT_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        visit.setClient(client);
        // Add required entity
        Mission mission;
        if (TestUtil.findAll(em, Mission.class).isEmpty()) {
            mission = MissionResourceIT.createUpdatedEntity(em);
            em.persist(mission);
            em.flush();
        } else {
            mission = TestUtil.findAll(em, Mission.class).get(0);
        }
        visit.setMission(mission);
        return visit;
    }

    @BeforeEach
    public void initTest() {
        visit = createEntity(em);
    }

    @Test
    @Transactional
    void createVisit() throws Exception {
        int databaseSizeBeforeCreate = visitRepository.findAll().size();
        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);
        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isCreated());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeCreate + 1);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getVisitOrder()).isEqualTo(DEFAULT_VISIT_ORDER);
        assertThat(testVisit.getObjective()).isEqualTo(DEFAULT_OBJECTIVE);
        assertThat(testVisit.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testVisit.getLatitude()).isEqualByComparingTo(DEFAULT_LATITUDE);
        assertThat(testVisit.getLongitude()).isEqualByComparingTo(DEFAULT_LONGITUDE);
        assertThat(testVisit.getCheckinAt()).isEqualTo(DEFAULT_CHECKIN_AT);
        assertThat(testVisit.getCheckoutAt()).isEqualTo(DEFAULT_CHECKOUT_AT);
        assertThat(testVisit.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testVisit.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createVisitWithExistingId() throws Exception {
        // Create the Visit with an existing ID
        visit.setId(1L);
        VisitDTO visitDTO = visitMapper.toDto(visit);

        int databaseSizeBeforeCreate = visitRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVisitOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().size();
        // set the field null
        visit.setVisitOrder(null);

        // Create the Visit, which fails.
        VisitDTO visitDTO = visitMapper.toDto(visit);

        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkObjectiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().size();
        // set the field null
        visit.setObjective(null);

        // Create the Visit, which fails.
        VisitDTO visitDTO = visitMapper.toDto(visit);

        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().size();
        // set the field null
        visit.setStatus(null);

        // Create the Visit, which fails.
        VisitDTO visitDTO = visitMapper.toDto(visit);

        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().size();
        // set the field null
        visit.setCreatedAt(null);

        // Create the Visit, which fails.
        VisitDTO visitDTO = visitMapper.toDto(visit);

        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVisits() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visit.getId().intValue())))
            .andExpect(jsonPath("$.[*].visitOrder").value(hasItem(DEFAULT_VISIT_ORDER)))
            .andExpect(jsonPath("$.[*].objective").value(hasItem(DEFAULT_OBJECTIVE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].checkinAt").value(hasItem(sameInstant(DEFAULT_CHECKIN_AT))))
            .andExpect(jsonPath("$.[*].checkoutAt").value(hasItem(sameInstant(DEFAULT_CHECKOUT_AT))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(visitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(visitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(visitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(visitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVisit() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get the visit
        restVisitMockMvc
            .perform(get(ENTITY_API_URL_ID, visit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visit.getId().intValue()))
            .andExpect(jsonPath("$.visitOrder").value(DEFAULT_VISIT_ORDER))
            .andExpect(jsonPath("$.objective").value(DEFAULT_OBJECTIVE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.latitude").value(sameNumber(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.longitude").value(sameNumber(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.checkinAt").value(sameInstant(DEFAULT_CHECKIN_AT)))
            .andExpect(jsonPath("$.checkoutAt").value(sameInstant(DEFAULT_CHECKOUT_AT)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getVisitsByIdFiltering() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        Long id = visit.getId();

        defaultVisitShouldBeFound("id.equals=" + id);
        defaultVisitShouldNotBeFound("id.notEquals=" + id);

        defaultVisitShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVisitShouldNotBeFound("id.greaterThan=" + id);

        defaultVisitShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVisitShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder equals to DEFAULT_VISIT_ORDER
        defaultVisitShouldBeFound("visitOrder.equals=" + DEFAULT_VISIT_ORDER);

        // Get all the visitList where visitOrder equals to UPDATED_VISIT_ORDER
        defaultVisitShouldNotBeFound("visitOrder.equals=" + UPDATED_VISIT_ORDER);
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder in DEFAULT_VISIT_ORDER or UPDATED_VISIT_ORDER
        defaultVisitShouldBeFound("visitOrder.in=" + DEFAULT_VISIT_ORDER + "," + UPDATED_VISIT_ORDER);

        // Get all the visitList where visitOrder equals to UPDATED_VISIT_ORDER
        defaultVisitShouldNotBeFound("visitOrder.in=" + UPDATED_VISIT_ORDER);
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder is not null
        defaultVisitShouldBeFound("visitOrder.specified=true");

        // Get all the visitList where visitOrder is null
        defaultVisitShouldNotBeFound("visitOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder is greater than or equal to DEFAULT_VISIT_ORDER
        defaultVisitShouldBeFound("visitOrder.greaterThanOrEqual=" + DEFAULT_VISIT_ORDER);

        // Get all the visitList where visitOrder is greater than or equal to UPDATED_VISIT_ORDER
        defaultVisitShouldNotBeFound("visitOrder.greaterThanOrEqual=" + UPDATED_VISIT_ORDER);
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder is less than or equal to DEFAULT_VISIT_ORDER
        defaultVisitShouldBeFound("visitOrder.lessThanOrEqual=" + DEFAULT_VISIT_ORDER);

        // Get all the visitList where visitOrder is less than or equal to SMALLER_VISIT_ORDER
        defaultVisitShouldNotBeFound("visitOrder.lessThanOrEqual=" + SMALLER_VISIT_ORDER);
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder is less than DEFAULT_VISIT_ORDER
        defaultVisitShouldNotBeFound("visitOrder.lessThan=" + DEFAULT_VISIT_ORDER);

        // Get all the visitList where visitOrder is less than UPDATED_VISIT_ORDER
        defaultVisitShouldBeFound("visitOrder.lessThan=" + UPDATED_VISIT_ORDER);
    }

    @Test
    @Transactional
    void getAllVisitsByVisitOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where visitOrder is greater than DEFAULT_VISIT_ORDER
        defaultVisitShouldNotBeFound("visitOrder.greaterThan=" + DEFAULT_VISIT_ORDER);

        // Get all the visitList where visitOrder is greater than SMALLER_VISIT_ORDER
        defaultVisitShouldBeFound("visitOrder.greaterThan=" + SMALLER_VISIT_ORDER);
    }

    @Test
    @Transactional
    void getAllVisitsByObjectiveIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where objective equals to DEFAULT_OBJECTIVE
        defaultVisitShouldBeFound("objective.equals=" + DEFAULT_OBJECTIVE);

        // Get all the visitList where objective equals to UPDATED_OBJECTIVE
        defaultVisitShouldNotBeFound("objective.equals=" + UPDATED_OBJECTIVE);
    }

    @Test
    @Transactional
    void getAllVisitsByObjectiveIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where objective in DEFAULT_OBJECTIVE or UPDATED_OBJECTIVE
        defaultVisitShouldBeFound("objective.in=" + DEFAULT_OBJECTIVE + "," + UPDATED_OBJECTIVE);

        // Get all the visitList where objective equals to UPDATED_OBJECTIVE
        defaultVisitShouldNotBeFound("objective.in=" + UPDATED_OBJECTIVE);
    }

    @Test
    @Transactional
    void getAllVisitsByObjectiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where objective is not null
        defaultVisitShouldBeFound("objective.specified=true");

        // Get all the visitList where objective is null
        defaultVisitShouldNotBeFound("objective.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where status equals to DEFAULT_STATUS
        defaultVisitShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the visitList where status equals to UPDATED_STATUS
        defaultVisitShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVisitsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultVisitShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the visitList where status equals to UPDATED_STATUS
        defaultVisitShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVisitsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where status is not null
        defaultVisitShouldBeFound("status.specified=true");

        // Get all the visitList where status is null
        defaultVisitShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude equals to DEFAULT_LATITUDE
        defaultVisitShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the visitList where latitude equals to UPDATED_LATITUDE
        defaultVisitShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultVisitShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the visitList where latitude equals to UPDATED_LATITUDE
        defaultVisitShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude is not null
        defaultVisitShouldBeFound("latitude.specified=true");

        // Get all the visitList where latitude is null
        defaultVisitShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultVisitShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the visitList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultVisitShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultVisitShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the visitList where latitude is less than or equal to SMALLER_LATITUDE
        defaultVisitShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude is less than DEFAULT_LATITUDE
        defaultVisitShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the visitList where latitude is less than UPDATED_LATITUDE
        defaultVisitShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where latitude is greater than DEFAULT_LATITUDE
        defaultVisitShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the visitList where latitude is greater than SMALLER_LATITUDE
        defaultVisitShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude equals to DEFAULT_LONGITUDE
        defaultVisitShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the visitList where longitude equals to UPDATED_LONGITUDE
        defaultVisitShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultVisitShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the visitList where longitude equals to UPDATED_LONGITUDE
        defaultVisitShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude is not null
        defaultVisitShouldBeFound("longitude.specified=true");

        // Get all the visitList where longitude is null
        defaultVisitShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultVisitShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the visitList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultVisitShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultVisitShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the visitList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultVisitShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude is less than DEFAULT_LONGITUDE
        defaultVisitShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the visitList where longitude is less than UPDATED_LONGITUDE
        defaultVisitShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where longitude is greater than DEFAULT_LONGITUDE
        defaultVisitShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the visitList where longitude is greater than SMALLER_LONGITUDE
        defaultVisitShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt equals to DEFAULT_CHECKIN_AT
        defaultVisitShouldBeFound("checkinAt.equals=" + DEFAULT_CHECKIN_AT);

        // Get all the visitList where checkinAt equals to UPDATED_CHECKIN_AT
        defaultVisitShouldNotBeFound("checkinAt.equals=" + UPDATED_CHECKIN_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt in DEFAULT_CHECKIN_AT or UPDATED_CHECKIN_AT
        defaultVisitShouldBeFound("checkinAt.in=" + DEFAULT_CHECKIN_AT + "," + UPDATED_CHECKIN_AT);

        // Get all the visitList where checkinAt equals to UPDATED_CHECKIN_AT
        defaultVisitShouldNotBeFound("checkinAt.in=" + UPDATED_CHECKIN_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt is not null
        defaultVisitShouldBeFound("checkinAt.specified=true");

        // Get all the visitList where checkinAt is null
        defaultVisitShouldNotBeFound("checkinAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt is greater than or equal to DEFAULT_CHECKIN_AT
        defaultVisitShouldBeFound("checkinAt.greaterThanOrEqual=" + DEFAULT_CHECKIN_AT);

        // Get all the visitList where checkinAt is greater than or equal to UPDATED_CHECKIN_AT
        defaultVisitShouldNotBeFound("checkinAt.greaterThanOrEqual=" + UPDATED_CHECKIN_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt is less than or equal to DEFAULT_CHECKIN_AT
        defaultVisitShouldBeFound("checkinAt.lessThanOrEqual=" + DEFAULT_CHECKIN_AT);

        // Get all the visitList where checkinAt is less than or equal to SMALLER_CHECKIN_AT
        defaultVisitShouldNotBeFound("checkinAt.lessThanOrEqual=" + SMALLER_CHECKIN_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt is less than DEFAULT_CHECKIN_AT
        defaultVisitShouldNotBeFound("checkinAt.lessThan=" + DEFAULT_CHECKIN_AT);

        // Get all the visitList where checkinAt is less than UPDATED_CHECKIN_AT
        defaultVisitShouldBeFound("checkinAt.lessThan=" + UPDATED_CHECKIN_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckinAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkinAt is greater than DEFAULT_CHECKIN_AT
        defaultVisitShouldNotBeFound("checkinAt.greaterThan=" + DEFAULT_CHECKIN_AT);

        // Get all the visitList where checkinAt is greater than SMALLER_CHECKIN_AT
        defaultVisitShouldBeFound("checkinAt.greaterThan=" + SMALLER_CHECKIN_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt equals to DEFAULT_CHECKOUT_AT
        defaultVisitShouldBeFound("checkoutAt.equals=" + DEFAULT_CHECKOUT_AT);

        // Get all the visitList where checkoutAt equals to UPDATED_CHECKOUT_AT
        defaultVisitShouldNotBeFound("checkoutAt.equals=" + UPDATED_CHECKOUT_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt in DEFAULT_CHECKOUT_AT or UPDATED_CHECKOUT_AT
        defaultVisitShouldBeFound("checkoutAt.in=" + DEFAULT_CHECKOUT_AT + "," + UPDATED_CHECKOUT_AT);

        // Get all the visitList where checkoutAt equals to UPDATED_CHECKOUT_AT
        defaultVisitShouldNotBeFound("checkoutAt.in=" + UPDATED_CHECKOUT_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt is not null
        defaultVisitShouldBeFound("checkoutAt.specified=true");

        // Get all the visitList where checkoutAt is null
        defaultVisitShouldNotBeFound("checkoutAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt is greater than or equal to DEFAULT_CHECKOUT_AT
        defaultVisitShouldBeFound("checkoutAt.greaterThanOrEqual=" + DEFAULT_CHECKOUT_AT);

        // Get all the visitList where checkoutAt is greater than or equal to UPDATED_CHECKOUT_AT
        defaultVisitShouldNotBeFound("checkoutAt.greaterThanOrEqual=" + UPDATED_CHECKOUT_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt is less than or equal to DEFAULT_CHECKOUT_AT
        defaultVisitShouldBeFound("checkoutAt.lessThanOrEqual=" + DEFAULT_CHECKOUT_AT);

        // Get all the visitList where checkoutAt is less than or equal to SMALLER_CHECKOUT_AT
        defaultVisitShouldNotBeFound("checkoutAt.lessThanOrEqual=" + SMALLER_CHECKOUT_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt is less than DEFAULT_CHECKOUT_AT
        defaultVisitShouldNotBeFound("checkoutAt.lessThan=" + DEFAULT_CHECKOUT_AT);

        // Get all the visitList where checkoutAt is less than UPDATED_CHECKOUT_AT
        defaultVisitShouldBeFound("checkoutAt.lessThan=" + UPDATED_CHECKOUT_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCheckoutAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where checkoutAt is greater than DEFAULT_CHECKOUT_AT
        defaultVisitShouldNotBeFound("checkoutAt.greaterThan=" + DEFAULT_CHECKOUT_AT);

        // Get all the visitList where checkoutAt is greater than SMALLER_CHECKOUT_AT
        defaultVisitShouldBeFound("checkoutAt.greaterThan=" + SMALLER_CHECKOUT_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where notes equals to DEFAULT_NOTES
        defaultVisitShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the visitList where notes equals to UPDATED_NOTES
        defaultVisitShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllVisitsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultVisitShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the visitList where notes equals to UPDATED_NOTES
        defaultVisitShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllVisitsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where notes is not null
        defaultVisitShouldBeFound("notes.specified=true");

        // Get all the visitList where notes is null
        defaultVisitShouldNotBeFound("notes.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByNotesContainsSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where notes contains DEFAULT_NOTES
        defaultVisitShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the visitList where notes contains UPDATED_NOTES
        defaultVisitShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllVisitsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where notes does not contain DEFAULT_NOTES
        defaultVisitShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the visitList where notes does not contain UPDATED_NOTES
        defaultVisitShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt equals to DEFAULT_CREATED_AT
        defaultVisitShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the visitList where createdAt equals to UPDATED_CREATED_AT
        defaultVisitShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultVisitShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the visitList where createdAt equals to UPDATED_CREATED_AT
        defaultVisitShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt is not null
        defaultVisitShouldBeFound("createdAt.specified=true");

        // Get all the visitList where createdAt is null
        defaultVisitShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultVisitShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the visitList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultVisitShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultVisitShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the visitList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultVisitShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt is less than DEFAULT_CREATED_AT
        defaultVisitShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the visitList where createdAt is less than UPDATED_CREATED_AT
        defaultVisitShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where createdAt is greater than DEFAULT_CREATED_AT
        defaultVisitShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the visitList where createdAt is greater than SMALLER_CREATED_AT
        defaultVisitShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVisitsByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            visitRepository.saveAndFlush(visit);
            client = ClientResourceIT.createEntity(em);
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        visit.setClient(client);
        visitRepository.saveAndFlush(visit);
        Long clientId = client.getId();

        // Get all the visitList where client equals to clientId
        defaultVisitShouldBeFound("clientId.equals=" + clientId);

        // Get all the visitList where client equals to (clientId + 1)
        defaultVisitShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllVisitsByMissionIsEqualToSomething() throws Exception {
        Mission mission;
        if (TestUtil.findAll(em, Mission.class).isEmpty()) {
            visitRepository.saveAndFlush(visit);
            mission = MissionResourceIT.createEntity(em);
        } else {
            mission = TestUtil.findAll(em, Mission.class).get(0);
        }
        em.persist(mission);
        em.flush();
        visit.setMission(mission);
        visitRepository.saveAndFlush(visit);
        Long missionId = mission.getId();

        // Get all the visitList where mission equals to missionId
        defaultVisitShouldBeFound("missionId.equals=" + missionId);

        // Get all the visitList where mission equals to (missionId + 1)
        defaultVisitShouldNotBeFound("missionId.equals=" + (missionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVisitShouldBeFound(String filter) throws Exception {
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visit.getId().intValue())))
            .andExpect(jsonPath("$.[*].visitOrder").value(hasItem(DEFAULT_VISIT_ORDER)))
            .andExpect(jsonPath("$.[*].objective").value(hasItem(DEFAULT_OBJECTIVE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].checkinAt").value(hasItem(sameInstant(DEFAULT_CHECKIN_AT))))
            .andExpect(jsonPath("$.[*].checkoutAt").value(hasItem(sameInstant(DEFAULT_CHECKOUT_AT))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

        // Check, that the count call also returns 1
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVisitShouldNotBeFound(String filter) throws Exception {
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVisit() throws Exception {
        // Get the visit
        restVisitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVisit() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeUpdate = visitRepository.findAll().size();

        // Update the visit
        Visit updatedVisit = visitRepository.findById(visit.getId()).get();
        // Disconnect from session so that the updates on updatedVisit are not directly saved in db
        em.detach(updatedVisit);
        updatedVisit
            .visitOrder(UPDATED_VISIT_ORDER)
            .objective(UPDATED_OBJECTIVE)
            .status(UPDATED_STATUS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .checkinAt(UPDATED_CHECKIN_AT)
            .checkoutAt(UPDATED_CHECKOUT_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);
        VisitDTO visitDTO = visitMapper.toDto(updatedVisit);

        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getVisitOrder()).isEqualTo(UPDATED_VISIT_ORDER);
        assertThat(testVisit.getObjective()).isEqualTo(UPDATED_OBJECTIVE);
        assertThat(testVisit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVisit.getLatitude()).isEqualByComparingTo(UPDATED_LATITUDE);
        assertThat(testVisit.getLongitude()).isEqualByComparingTo(UPDATED_LONGITUDE);
        assertThat(testVisit.getCheckinAt()).isEqualTo(UPDATED_CHECKIN_AT);
        assertThat(testVisit.getCheckoutAt()).isEqualTo(UPDATED_CHECKOUT_AT);
        assertThat(testVisit.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testVisit.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeUpdate = visitRepository.findAll().size();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit
            .objective(UPDATED_OBJECTIVE)
            .status(UPDATED_STATUS)
            .checkinAt(UPDATED_CHECKIN_AT)
            .checkoutAt(UPDATED_CHECKOUT_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);

        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisit))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getVisitOrder()).isEqualTo(DEFAULT_VISIT_ORDER);
        assertThat(testVisit.getObjective()).isEqualTo(UPDATED_OBJECTIVE);
        assertThat(testVisit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVisit.getLatitude()).isEqualByComparingTo(DEFAULT_LATITUDE);
        assertThat(testVisit.getLongitude()).isEqualByComparingTo(DEFAULT_LONGITUDE);
        assertThat(testVisit.getCheckinAt()).isEqualTo(UPDATED_CHECKIN_AT);
        assertThat(testVisit.getCheckoutAt()).isEqualTo(UPDATED_CHECKOUT_AT);
        assertThat(testVisit.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testVisit.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeUpdate = visitRepository.findAll().size();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit
            .visitOrder(UPDATED_VISIT_ORDER)
            .objective(UPDATED_OBJECTIVE)
            .status(UPDATED_STATUS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .checkinAt(UPDATED_CHECKIN_AT)
            .checkoutAt(UPDATED_CHECKOUT_AT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);

        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisit))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getVisitOrder()).isEqualTo(UPDATED_VISIT_ORDER);
        assertThat(testVisit.getObjective()).isEqualTo(UPDATED_OBJECTIVE);
        assertThat(testVisit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVisit.getLatitude()).isEqualByComparingTo(UPDATED_LATITUDE);
        assertThat(testVisit.getLongitude()).isEqualByComparingTo(UPDATED_LONGITUDE);
        assertThat(testVisit.getCheckinAt()).isEqualTo(UPDATED_CHECKIN_AT);
        assertThat(testVisit.getCheckoutAt()).isEqualTo(UPDATED_CHECKOUT_AT);
        assertThat(testVisit.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testVisit.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisit() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeDelete = visitRepository.findAll().size();

        // Delete the visit
        restVisitMockMvc
            .perform(delete(ENTITY_API_URL_ID, visit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
