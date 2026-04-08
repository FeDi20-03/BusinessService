package com.tunisales.business.service;

import com.tunisales.business.domain.ClientContact;
import com.tunisales.business.repository.ClientContactRepository;
import com.tunisales.business.service.dto.ClientContactDTO;
import com.tunisales.business.service.mapper.ClientContactMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ClientContact}.
 */
@Service
@Transactional
public class ClientContactService {

    private final Logger log = LoggerFactory.getLogger(ClientContactService.class);

    private final ClientContactRepository clientContactRepository;

    private final ClientContactMapper clientContactMapper;

    public ClientContactService(ClientContactRepository clientContactRepository, ClientContactMapper clientContactMapper) {
        this.clientContactRepository = clientContactRepository;
        this.clientContactMapper = clientContactMapper;
    }

    /**
     * Save a clientContact.
     *
     * @param clientContactDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientContactDTO save(ClientContactDTO clientContactDTO) {
        log.debug("Request to save ClientContact : {}", clientContactDTO);
        ClientContact clientContact = clientContactMapper.toEntity(clientContactDTO);
        clientContact = clientContactRepository.save(clientContact);
        return clientContactMapper.toDto(clientContact);
    }

    /**
     * Update a clientContact.
     *
     * @param clientContactDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientContactDTO update(ClientContactDTO clientContactDTO) {
        log.debug("Request to update ClientContact : {}", clientContactDTO);
        ClientContact clientContact = clientContactMapper.toEntity(clientContactDTO);
        clientContact = clientContactRepository.save(clientContact);
        return clientContactMapper.toDto(clientContact);
    }

    /**
     * Partially update a clientContact.
     *
     * @param clientContactDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClientContactDTO> partialUpdate(ClientContactDTO clientContactDTO) {
        log.debug("Request to partially update ClientContact : {}", clientContactDTO);

        return clientContactRepository
            .findById(clientContactDTO.getId())
            .map(existingClientContact -> {
                clientContactMapper.partialUpdate(existingClientContact, clientContactDTO);

                return existingClientContact;
            })
            .map(clientContactRepository::save)
            .map(clientContactMapper::toDto);
    }

    /**
     * Get all the clientContacts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientContactDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ClientContacts");
        return clientContactRepository.findAll(pageable).map(clientContactMapper::toDto);
    }

    /**
     * Get all the clientContacts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ClientContactDTO> findAllWithEagerRelationships(Pageable pageable) {
        return clientContactRepository.findAllWithEagerRelationships(pageable).map(clientContactMapper::toDto);
    }

    /**
     * Get one clientContact by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClientContactDTO> findOne(Long id) {
        log.debug("Request to get ClientContact : {}", id);
        return clientContactRepository.findOneWithEagerRelationships(id).map(clientContactMapper::toDto);
    }

    /**
     * Delete the clientContact by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ClientContact : {}", id);
        clientContactRepository.deleteById(id);
    }
}
