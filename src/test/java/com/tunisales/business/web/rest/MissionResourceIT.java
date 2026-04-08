package com.tunisales.business.web.rest;

import static com.tunisales.business.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.Mission;
import com.tunisales.business.domain.Visit;
import com.tunisales.business.domain.enumeration.MissionStatus;
import com.tunisales.business.repository.MissionRepository;
import com.tunisales.business.service.criteria.MissionCriteria;
import com.tunisales.business.service.dto.MissionDTO;
import com.tunisales.business.service.mapper.MissionMapper;
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
 * Integration tests for the {@link MissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MissionResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final String DEFAULT_ASSIGNED_TO_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNED_TO_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_MISSION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MISSION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_MISSION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final MissionStatus DEFAULT_STATUS = MissionStatus.PLANNED;
    private static final MissionStatus UPDATED_STATUS = MissionStatus.IN_PROGRESS;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/missions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMapper missionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMissionMockMvc;

    private Mission mission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createEntity(EntityManager em) {
        Mission mission = new Mission()
            .tenantId(DEFAULT_TENANT_ID)
            .assignedToLogin(DEFAULT_ASSIGNED_TO_LOGIN)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .missionDate(DEFAULT_MISSION_DATE)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return mission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createUpdatedEntity(EntityManager em) {
        Mission mission = new Mission()
            .tenantId(UPDATED_TENANT_ID)
            .assignedToLogin(UPDATED_ASSIGNED_TO_LOGIN)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .missionDate(UPDATED_MISSION_DATE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return mission;
    }

    @BeforeEach
    public void initTest() {
        mission = createEntity(em);
    }

    @Test
    @Transactional
    void createMission() throws Exception {
        int databaseSizeBeforeCreate = missionRepository.findAll().size();
        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);
        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isCreated());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeCreate + 1);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testMission.getAssignedToLogin()).isEqualTo(DEFAULT_ASSIGNED_TO_LOGIN);
        assertThat(testMission.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testMission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMission.getMissionDate()).isEqualTo(DEFAULT_MISSION_DATE);
        assertThat(testMission.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testMission.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testMission.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createMissionWithExistingId() throws Exception {
        // Create the Mission with an existing ID
        mission.setId(1L);
        MissionDTO missionDTO = missionMapper.toDto(mission);

        int databaseSizeBeforeCreate = missionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setTenantId(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssignedToLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setAssignedToLogin(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setTitle(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMissionDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setMissionDate(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setStatus(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setCreatedAt(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMissions() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mission.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].assignedToLogin").value(hasItem(DEFAULT_ASSIGNED_TO_LOGIN)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].missionDate").value(hasItem(sameInstant(DEFAULT_MISSION_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getMission() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get the mission
        restMissionMockMvc
            .perform(get(ENTITY_API_URL_ID, mission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mission.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.assignedToLogin").value(DEFAULT_ASSIGNED_TO_LOGIN))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.missionDate").value(sameInstant(DEFAULT_MISSION_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getMissionsByIdFiltering() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        Long id = mission.getId();

        defaultMissionShouldBeFound("id.equals=" + id);
        defaultMissionShouldNotBeFound("id.notEquals=" + id);

        defaultMissionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMissionShouldNotBeFound("id.greaterThan=" + id);

        defaultMissionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMissionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId equals to DEFAULT_TENANT_ID
        defaultMissionShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the missionList where tenantId equals to UPDATED_TENANT_ID
        defaultMissionShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultMissionShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the missionList where tenantId equals to UPDATED_TENANT_ID
        defaultMissionShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId is not null
        defaultMissionShouldBeFound("tenantId.specified=true");

        // Get all the missionList where tenantId is null
        defaultMissionShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultMissionShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the missionList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultMissionShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultMissionShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the missionList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultMissionShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId is less than DEFAULT_TENANT_ID
        defaultMissionShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the missionList where tenantId is less than UPDATED_TENANT_ID
        defaultMissionShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllMissionsByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where tenantId is greater than DEFAULT_TENANT_ID
        defaultMissionShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the missionList where tenantId is greater than SMALLER_TENANT_ID
        defaultMissionShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllMissionsByAssignedToLoginIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where assignedToLogin equals to DEFAULT_ASSIGNED_TO_LOGIN
        defaultMissionShouldBeFound("assignedToLogin.equals=" + DEFAULT_ASSIGNED_TO_LOGIN);

        // Get all the missionList where assignedToLogin equals to UPDATED_ASSIGNED_TO_LOGIN
        defaultMissionShouldNotBeFound("assignedToLogin.equals=" + UPDATED_ASSIGNED_TO_LOGIN);
    }

    @Test
    @Transactional
    void getAllMissionsByAssignedToLoginIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where assignedToLogin in DEFAULT_ASSIGNED_TO_LOGIN or UPDATED_ASSIGNED_TO_LOGIN
        defaultMissionShouldBeFound("assignedToLogin.in=" + DEFAULT_ASSIGNED_TO_LOGIN + "," + UPDATED_ASSIGNED_TO_LOGIN);

        // Get all the missionList where assignedToLogin equals to UPDATED_ASSIGNED_TO_LOGIN
        defaultMissionShouldNotBeFound("assignedToLogin.in=" + UPDATED_ASSIGNED_TO_LOGIN);
    }

    @Test
    @Transactional
    void getAllMissionsByAssignedToLoginIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where assignedToLogin is not null
        defaultMissionShouldBeFound("assignedToLogin.specified=true");

        // Get all the missionList where assignedToLogin is null
        defaultMissionShouldNotBeFound("assignedToLogin.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByAssignedToLoginContainsSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where assignedToLogin contains DEFAULT_ASSIGNED_TO_LOGIN
        defaultMissionShouldBeFound("assignedToLogin.contains=" + DEFAULT_ASSIGNED_TO_LOGIN);

        // Get all the missionList where assignedToLogin contains UPDATED_ASSIGNED_TO_LOGIN
        defaultMissionShouldNotBeFound("assignedToLogin.contains=" + UPDATED_ASSIGNED_TO_LOGIN);
    }

    @Test
    @Transactional
    void getAllMissionsByAssignedToLoginNotContainsSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where assignedToLogin does not contain DEFAULT_ASSIGNED_TO_LOGIN
        defaultMissionShouldNotBeFound("assignedToLogin.doesNotContain=" + DEFAULT_ASSIGNED_TO_LOGIN);

        // Get all the missionList where assignedToLogin does not contain UPDATED_ASSIGNED_TO_LOGIN
        defaultMissionShouldBeFound("assignedToLogin.doesNotContain=" + UPDATED_ASSIGNED_TO_LOGIN);
    }

    @Test
    @Transactional
    void getAllMissionsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where title equals to DEFAULT_TITLE
        defaultMissionShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the missionList where title equals to UPDATED_TITLE
        defaultMissionShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllMissionsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultMissionShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the missionList where title equals to UPDATED_TITLE
        defaultMissionShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllMissionsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where title is not null
        defaultMissionShouldBeFound("title.specified=true");

        // Get all the missionList where title is null
        defaultMissionShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByTitleContainsSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where title contains DEFAULT_TITLE
        defaultMissionShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the missionList where title contains UPDATED_TITLE
        defaultMissionShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllMissionsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where title does not contain DEFAULT_TITLE
        defaultMissionShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the missionList where title does not contain UPDATED_TITLE
        defaultMissionShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where description equals to DEFAULT_DESCRIPTION
        defaultMissionShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the missionList where description equals to UPDATED_DESCRIPTION
        defaultMissionShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultMissionShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the missionList where description equals to UPDATED_DESCRIPTION
        defaultMissionShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where description is not null
        defaultMissionShouldBeFound("description.specified=true");

        // Get all the missionList where description is null
        defaultMissionShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where description contains DEFAULT_DESCRIPTION
        defaultMissionShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the missionList where description contains UPDATED_DESCRIPTION
        defaultMissionShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where description does not contain DEFAULT_DESCRIPTION
        defaultMissionShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the missionList where description does not contain UPDATED_DESCRIPTION
        defaultMissionShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate equals to DEFAULT_MISSION_DATE
        defaultMissionShouldBeFound("missionDate.equals=" + DEFAULT_MISSION_DATE);

        // Get all the missionList where missionDate equals to UPDATED_MISSION_DATE
        defaultMissionShouldNotBeFound("missionDate.equals=" + UPDATED_MISSION_DATE);
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate in DEFAULT_MISSION_DATE or UPDATED_MISSION_DATE
        defaultMissionShouldBeFound("missionDate.in=" + DEFAULT_MISSION_DATE + "," + UPDATED_MISSION_DATE);

        // Get all the missionList where missionDate equals to UPDATED_MISSION_DATE
        defaultMissionShouldNotBeFound("missionDate.in=" + UPDATED_MISSION_DATE);
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate is not null
        defaultMissionShouldBeFound("missionDate.specified=true");

        // Get all the missionList where missionDate is null
        defaultMissionShouldNotBeFound("missionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate is greater than or equal to DEFAULT_MISSION_DATE
        defaultMissionShouldBeFound("missionDate.greaterThanOrEqual=" + DEFAULT_MISSION_DATE);

        // Get all the missionList where missionDate is greater than or equal to UPDATED_MISSION_DATE
        defaultMissionShouldNotBeFound("missionDate.greaterThanOrEqual=" + UPDATED_MISSION_DATE);
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate is less than or equal to DEFAULT_MISSION_DATE
        defaultMissionShouldBeFound("missionDate.lessThanOrEqual=" + DEFAULT_MISSION_DATE);

        // Get all the missionList where missionDate is less than or equal to SMALLER_MISSION_DATE
        defaultMissionShouldNotBeFound("missionDate.lessThanOrEqual=" + SMALLER_MISSION_DATE);
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate is less than DEFAULT_MISSION_DATE
        defaultMissionShouldNotBeFound("missionDate.lessThan=" + DEFAULT_MISSION_DATE);

        // Get all the missionList where missionDate is less than UPDATED_MISSION_DATE
        defaultMissionShouldBeFound("missionDate.lessThan=" + UPDATED_MISSION_DATE);
    }

    @Test
    @Transactional
    void getAllMissionsByMissionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where missionDate is greater than DEFAULT_MISSION_DATE
        defaultMissionShouldNotBeFound("missionDate.greaterThan=" + DEFAULT_MISSION_DATE);

        // Get all the missionList where missionDate is greater than SMALLER_MISSION_DATE
        defaultMissionShouldBeFound("missionDate.greaterThan=" + SMALLER_MISSION_DATE);
    }

    @Test
    @Transactional
    void getAllMissionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where status equals to DEFAULT_STATUS
        defaultMissionShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the missionList where status equals to UPDATED_STATUS
        defaultMissionShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMissionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultMissionShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the missionList where status equals to UPDATED_STATUS
        defaultMissionShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMissionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where status is not null
        defaultMissionShouldBeFound("status.specified=true");

        // Get all the missionList where status is null
        defaultMissionShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt equals to DEFAULT_CREATED_AT
        defaultMissionShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the missionList where createdAt equals to UPDATED_CREATED_AT
        defaultMissionShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultMissionShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the missionList where createdAt equals to UPDATED_CREATED_AT
        defaultMissionShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt is not null
        defaultMissionShouldBeFound("createdAt.specified=true");

        // Get all the missionList where createdAt is null
        defaultMissionShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultMissionShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the missionList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultMissionShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultMissionShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the missionList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultMissionShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt is less than DEFAULT_CREATED_AT
        defaultMissionShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the missionList where createdAt is less than UPDATED_CREATED_AT
        defaultMissionShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where createdAt is greater than DEFAULT_CREATED_AT
        defaultMissionShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the missionList where createdAt is greater than SMALLER_CREATED_AT
        defaultMissionShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultMissionShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the missionList where updatedAt equals to UPDATED_UPDATED_AT
        defaultMissionShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultMissionShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the missionList where updatedAt equals to UPDATED_UPDATED_AT
        defaultMissionShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt is not null
        defaultMissionShouldBeFound("updatedAt.specified=true");

        // Get all the missionList where updatedAt is null
        defaultMissionShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultMissionShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the missionList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultMissionShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultMissionShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the missionList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultMissionShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultMissionShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the missionList where updatedAt is less than UPDATED_UPDATED_AT
        defaultMissionShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultMissionShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the missionList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultMissionShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMissionsByVisitsIsEqualToSomething() throws Exception {
        Visit visits;
        if (TestUtil.findAll(em, Visit.class).isEmpty()) {
            missionRepository.saveAndFlush(mission);
            visits = VisitResourceIT.createEntity(em);
        } else {
            visits = TestUtil.findAll(em, Visit.class).get(0);
        }
        em.persist(visits);
        em.flush();
        mission.addVisits(visits);
        missionRepository.saveAndFlush(mission);
        Long visitsId = visits.getId();

        // Get all the missionList where visits equals to visitsId
        defaultMissionShouldBeFound("visitsId.equals=" + visitsId);

        // Get all the missionList where visits equals to (visitsId + 1)
        defaultMissionShouldNotBeFound("visitsId.equals=" + (visitsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMissionShouldBeFound(String filter) throws Exception {
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mission.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].assignedToLogin").value(hasItem(DEFAULT_ASSIGNED_TO_LOGIN)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].missionDate").value(hasItem(sameInstant(DEFAULT_MISSION_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMissionShouldNotBeFound(String filter) throws Exception {
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMission() throws Exception {
        // Get the mission
        restMissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMission() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeUpdate = missionRepository.findAll().size();

        // Update the mission
        Mission updatedMission = missionRepository.findById(mission.getId()).get();
        // Disconnect from session so that the updates on updatedMission are not directly saved in db
        em.detach(updatedMission);
        updatedMission
            .tenantId(UPDATED_TENANT_ID)
            .assignedToLogin(UPDATED_ASSIGNED_TO_LOGIN)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .missionDate(UPDATED_MISSION_DATE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        MissionDTO missionDTO = missionMapper.toDto(updatedMission);

        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, missionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(missionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testMission.getAssignedToLogin()).isEqualTo(UPDATED_ASSIGNED_TO_LOGIN);
        assertThat(testMission.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testMission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMission.getMissionDate()).isEqualTo(UPDATED_MISSION_DATE);
        assertThat(testMission.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMission.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testMission.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, missionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(missionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeUpdate = missionRepository.findAll().size();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission.assignedToLogin(UPDATED_ASSIGNED_TO_LOGIN).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testMission.getAssignedToLogin()).isEqualTo(UPDATED_ASSIGNED_TO_LOGIN);
        assertThat(testMission.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testMission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMission.getMissionDate()).isEqualTo(DEFAULT_MISSION_DATE);
        assertThat(testMission.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testMission.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testMission.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeUpdate = missionRepository.findAll().size();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission
            .tenantId(UPDATED_TENANT_ID)
            .assignedToLogin(UPDATED_ASSIGNED_TO_LOGIN)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .missionDate(UPDATED_MISSION_DATE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testMission.getAssignedToLogin()).isEqualTo(UPDATED_ASSIGNED_TO_LOGIN);
        assertThat(testMission.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testMission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMission.getMissionDate()).isEqualTo(UPDATED_MISSION_DATE);
        assertThat(testMission.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMission.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testMission.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, missionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(missionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMission() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeDelete = missionRepository.findAll().size();

        // Delete the mission
        restMissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, mission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
