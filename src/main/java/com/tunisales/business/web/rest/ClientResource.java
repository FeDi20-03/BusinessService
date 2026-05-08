package com.tunisales.business.web.rest;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.enumeration.ClientGrade;
import com.tunisales.business.repository.ClientRepository;
import com.tunisales.business.service.ClientAssignmentService;
import com.tunisales.business.service.ClientQueryService;
import com.tunisales.business.service.ClientService;
import com.tunisales.business.service.criteria.ClientCriteria;
import com.tunisales.business.service.dto.ClientAssignmentDTO;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.mapper.ClientMapper;
import com.tunisales.business.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.business.domain.Client}.
 */
@RestController
@RequestMapping("/api")
public class ClientResource {

    private final Logger log = LoggerFactory.getLogger(ClientResource.class);

    private static final String ENTITY_NAME = "businessServiceClient";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientService clientService;

    private final ClientRepository clientRepository;

    private final ClientQueryService clientQueryService;

    private final ClientAssignmentService clientAssignmentService;

    private final ClientMapper clientMapper;

    public ClientResource(
        ClientService clientService,
        ClientRepository clientRepository,
        ClientQueryService clientQueryService,
        ClientAssignmentService clientAssignmentService,
        ClientMapper clientMapper
    ) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.clientQueryService = clientQueryService;
        this.clientAssignmentService = clientAssignmentService;
        this.clientMapper = clientMapper;
    }

    /**
     * {@code POST  /clients} : Create a new client.
     *
     * @param clientDTO the clientDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientDTO, or with status {@code 400 (Bad Request)} if the client has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clients")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) throws URISyntaxException {
        log.debug("REST request to save Client : {}", clientDTO);
        if (clientDTO.getId() != null) {
            throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientDTO result = clientService.save(clientDTO);
        return ResponseEntity
            .created(new URI("/api/clients/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clients/:id} : Updates an existing client.
     *
     * @param id the id of the clientDTO to save.
     * @param clientDTO the clientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientDTO,
     * or with status {@code 400 (Bad Request)} if the clientDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> updateClient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientDTO clientDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Client : {}, {}", id, clientDTO);
        if (clientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientDTO result = clientService.update(clientDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /clients/:id} : Partial updates given fields of an existing client, field will ignore if it is null
     *
     * @param id the id of the clientDTO to save.
     * @param clientDTO the clientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientDTO,
     * or with status {@code 400 (Bad Request)} if the clientDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clientDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clients/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientDTO> partialUpdateClient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientDTO clientDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Client partially : {}, {}", id, clientDTO);
        if (clientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientDTO> result = clientService.partialUpdate(clientDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /clients} : get all the clients.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clients in body.
     */
    @GetMapping("/clients")
    public ResponseEntity<List<ClientDTO>> getAllClients(
        ClientCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Clients by criteria: {}", criteria);
        Page<ClientDTO> page = clientQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /clients/count} : count all the clients.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/clients/count")
    public ResponseEntity<Long> countClients(ClientCriteria criteria) {
        log.debug("REST request to count Clients by criteria: {}", criteria);
        return ResponseEntity.ok().body(clientQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /clients/:id} : get the "id" client.
     *
     * @param id the id of the clientDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
        log.debug("REST request to get Client : {}", id);
        Optional<ClientDTO> clientDTO = clientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientDTO);
    }

    @GetMapping("/clients/tax-id/{taxId}")
    public Client getClientByTaxId(@PathVariable String taxId) {
        log.debug("REST request to get Client by taxId : {}", taxId);
        return clientService.findByTaxId(taxId);
    }

    @GetMapping("/clients/tax-id1")
    public Client getClientByTaxId1(@RequestParam(name = "taxId") String taxId) {
        log.debug("REST request to get Client by taxId : {}", taxId);
        return clientService.findByTaxId(taxId);
    }

    /**
     * {@code DELETE  /clients/:id} : delete the "id" client.
     *
     * @param id the id of the clientDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.debug("REST request to delete Client : {}", id);
        clientService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * Sub-step 2.8 — assign a commercial to a client within a zone.
     * Replaces any existing assignment for that client.
     */
    @PostMapping("/clients/{id}/assign")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<ClientAssignmentDTO> assign(@PathVariable("id") Long clientId, @Valid @RequestBody AssignRequest request) {
        log.debug("REST request to assign client {} to {} on zone {}", clientId, request.commercialLogin, request.zoneId);
        ClientAssignmentDTO result = clientAssignmentService.assign(clientId, request.commercialLogin, request.zoneId);
        return ResponseEntity.ok(result);
    }

    /**
     * Sub-step 2.8 — list all clients assigned to the given commercial login.
     */
    @GetMapping("/clients/by-commercial/{login}")
    public ResponseEntity<List<ClientDTO>> getByCommercial(@PathVariable("login") String login) {
        log.debug("REST request to list clients for commercial {}", login);
        List<Long> ids = clientAssignmentService.findClientIdsByCommercialLogin(login);
        List<ClientDTO> dtos = clientRepository.findAllById(ids).stream().map(clientMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Sub-step 2.11 — set the loyalty grade (A/B/C) of a client.
     */
    @PutMapping("/clients/{id}/grade")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<ClientDTO> setGrade(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        log.debug("REST request to set grade {} on client {}", request.grade, id);
        Client client = clientRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Client not found", ENTITY_NAME, "idnotfound"));
        client.setGrade(request.grade);
        Client saved = clientRepository.save(client);
        return ResponseEntity.ok(clientMapper.toDto(saved));
    }

    /** Request body for {@code POST /clients/{id}/assign}. */
    public static class AssignRequest {

        @NotNull
        @Size(max = 100)
        private String commercialLogin;

        @NotNull
        private Long zoneId;

        public String getCommercialLogin() {
            return commercialLogin;
        }

        public void setCommercialLogin(String commercialLogin) {
            this.commercialLogin = commercialLogin;
        }

        public Long getZoneId() {
            return zoneId;
        }

        public void setZoneId(Long zoneId) {
            this.zoneId = zoneId;
        }
    }

    /** Request body for {@code PUT /clients/{id}/grade}. */
    public static class GradeRequest {

        @NotNull
        private ClientGrade grade;

        public ClientGrade getGrade() {
            return grade;
        }

        public void setGrade(ClientGrade grade) {
            this.grade = grade;
        }
    }
}
