package com.tunisales.business.web.rest;

import static com.tunisales.business.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.ClientContact;
import com.tunisales.business.domain.enumeration.ContactRole;
import com.tunisales.business.repository.ClientContactRepository;
import com.tunisales.business.service.ClientContactService;
import com.tunisales.business.service.dto.ClientContactDTO;
import com.tunisales.business.service.mapper.ClientContactMapper;
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
 * Integration tests for the {@link ClientContactResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClientContactResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final ContactRole DEFAULT_ROLE = ContactRole.BUYER;
    private static final ContactRole UPDATED_ROLE = ContactRole.ACCOUNTING;

    private static final Boolean DEFAULT_IS_PRIMARY = false;
    private static final Boolean UPDATED_IS_PRIMARY = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/client-contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientContactRepository clientContactRepository;

    @Mock
    private ClientContactRepository clientContactRepositoryMock;

    @Autowired
    private ClientContactMapper clientContactMapper;

    @Mock
    private ClientContactService clientContactServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientContactMockMvc;

    private ClientContact clientContact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientContact createEntity(EntityManager em) {
        ClientContact clientContact = new ClientContact()
            .fullName(DEFAULT_FULL_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .role(DEFAULT_ROLE)
            .isPrimary(DEFAULT_IS_PRIMARY)
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
        clientContact.setClient(client);
        return clientContact;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientContact createUpdatedEntity(EntityManager em) {
        ClientContact clientContact = new ClientContact()
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .isPrimary(UPDATED_IS_PRIMARY)
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
        clientContact.setClient(client);
        return clientContact;
    }

    @BeforeEach
    public void initTest() {
        clientContact = createEntity(em);
    }

    @Test
    @Transactional
    void createClientContact() throws Exception {
        int databaseSizeBeforeCreate = clientContactRepository.findAll().size();
        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);
        restClientContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeCreate + 1);
        ClientContact testClientContact = clientContactList.get(clientContactList.size() - 1);
        assertThat(testClientContact.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testClientContact.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testClientContact.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testClientContact.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testClientContact.getIsPrimary()).isEqualTo(DEFAULT_IS_PRIMARY);
        assertThat(testClientContact.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createClientContactWithExistingId() throws Exception {
        // Create the ClientContact with an existing ID
        clientContact.setId(1L);
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        int databaseSizeBeforeCreate = clientContactRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientContactRepository.findAll().size();
        // set the field null
        clientContact.setFullName(null);

        // Create the ClientContact, which fails.
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        restClientContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPrimaryIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientContactRepository.findAll().size();
        // set the field null
        clientContact.setIsPrimary(null);

        // Create the ClientContact, which fails.
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        restClientContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientContactRepository.findAll().size();
        // set the field null
        clientContact.setCreatedAt(null);

        // Create the ClientContact, which fails.
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        restClientContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientContacts() throws Exception {
        // Initialize the database
        clientContactRepository.saveAndFlush(clientContact);

        // Get all the clientContactList
        restClientContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientContact.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].isPrimary").value(hasItem(DEFAULT_IS_PRIMARY.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientContactsWithEagerRelationshipsIsEnabled() throws Exception {
        when(clientContactServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientContactMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clientContactServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientContactsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clientContactServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientContactMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(clientContactRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClientContact() throws Exception {
        // Initialize the database
        clientContactRepository.saveAndFlush(clientContact);

        // Get the clientContact
        restClientContactMockMvc
            .perform(get(ENTITY_API_URL_ID, clientContact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientContact.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.isPrimary").value(DEFAULT_IS_PRIMARY.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingClientContact() throws Exception {
        // Get the clientContact
        restClientContactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientContact() throws Exception {
        // Initialize the database
        clientContactRepository.saveAndFlush(clientContact);

        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();

        // Update the clientContact
        ClientContact updatedClientContact = clientContactRepository.findById(clientContact.getId()).get();
        // Disconnect from session so that the updates on updatedClientContact are not directly saved in db
        em.detach(updatedClientContact);
        updatedClientContact
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .isPrimary(UPDATED_IS_PRIMARY)
            .createdAt(UPDATED_CREATED_AT);
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(updatedClientContact);

        restClientContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientContactDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
        ClientContact testClientContact = clientContactList.get(clientContactList.size() - 1);
        assertThat(testClientContact.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testClientContact.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testClientContact.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testClientContact.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testClientContact.getIsPrimary()).isEqualTo(UPDATED_IS_PRIMARY);
        assertThat(testClientContact.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingClientContact() throws Exception {
        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();
        clientContact.setId(count.incrementAndGet());

        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientContactDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientContact() throws Exception {
        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();
        clientContact.setId(count.incrementAndGet());

        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientContact() throws Exception {
        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();
        clientContact.setId(count.incrementAndGet());

        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientContactMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientContactWithPatch() throws Exception {
        // Initialize the database
        clientContactRepository.saveAndFlush(clientContact);

        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();

        // Update the clientContact using partial update
        ClientContact partialUpdatedClientContact = new ClientContact();
        partialUpdatedClientContact.setId(clientContact.getId());

        partialUpdatedClientContact.fullName(UPDATED_FULL_NAME).createdAt(UPDATED_CREATED_AT);

        restClientContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientContact))
            )
            .andExpect(status().isOk());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
        ClientContact testClientContact = clientContactList.get(clientContactList.size() - 1);
        assertThat(testClientContact.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testClientContact.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testClientContact.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testClientContact.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testClientContact.getIsPrimary()).isEqualTo(DEFAULT_IS_PRIMARY);
        assertThat(testClientContact.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateClientContactWithPatch() throws Exception {
        // Initialize the database
        clientContactRepository.saveAndFlush(clientContact);

        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();

        // Update the clientContact using partial update
        ClientContact partialUpdatedClientContact = new ClientContact();
        partialUpdatedClientContact.setId(clientContact.getId());

        partialUpdatedClientContact
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .role(UPDATED_ROLE)
            .isPrimary(UPDATED_IS_PRIMARY)
            .createdAt(UPDATED_CREATED_AT);

        restClientContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientContact))
            )
            .andExpect(status().isOk());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
        ClientContact testClientContact = clientContactList.get(clientContactList.size() - 1);
        assertThat(testClientContact.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testClientContact.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testClientContact.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testClientContact.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testClientContact.getIsPrimary()).isEqualTo(UPDATED_IS_PRIMARY);
        assertThat(testClientContact.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingClientContact() throws Exception {
        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();
        clientContact.setId(count.incrementAndGet());

        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientContactDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientContact() throws Exception {
        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();
        clientContact.setId(count.incrementAndGet());

        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientContact() throws Exception {
        int databaseSizeBeforeUpdate = clientContactRepository.findAll().size();
        clientContact.setId(count.incrementAndGet());

        // Create the ClientContact
        ClientContactDTO clientContactDTO = clientContactMapper.toDto(clientContact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientContactMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientContactDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientContact in the database
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientContact() throws Exception {
        // Initialize the database
        clientContactRepository.saveAndFlush(clientContact);

        int databaseSizeBeforeDelete = clientContactRepository.findAll().size();

        // Delete the clientContact
        restClientContactMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientContact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientContact> clientContactList = clientContactRepository.findAll();
        assertThat(clientContactList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
