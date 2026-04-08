package com.tunisales.business.service;

import com.tunisales.business.domain.PriceList;
import com.tunisales.business.repository.PriceListRepository;
import com.tunisales.business.service.dto.PriceListDTO;
import com.tunisales.business.service.mapper.PriceListMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PriceList}.
 */
@Service
@Transactional
public class PriceListService {

    private final Logger log = LoggerFactory.getLogger(PriceListService.class);

    private final PriceListRepository priceListRepository;

    private final PriceListMapper priceListMapper;

    public PriceListService(PriceListRepository priceListRepository, PriceListMapper priceListMapper) {
        this.priceListRepository = priceListRepository;
        this.priceListMapper = priceListMapper;
    }

    /**
     * Save a priceList.
     *
     * @param priceListDTO the entity to save.
     * @return the persisted entity.
     */
    public PriceListDTO save(PriceListDTO priceListDTO) {
        log.debug("Request to save PriceList : {}", priceListDTO);
        PriceList priceList = priceListMapper.toEntity(priceListDTO);
        priceList = priceListRepository.save(priceList);
        return priceListMapper.toDto(priceList);
    }

    /**
     * Update a priceList.
     *
     * @param priceListDTO the entity to save.
     * @return the persisted entity.
     */
    public PriceListDTO update(PriceListDTO priceListDTO) {
        log.debug("Request to update PriceList : {}", priceListDTO);
        PriceList priceList = priceListMapper.toEntity(priceListDTO);
        priceList = priceListRepository.save(priceList);
        return priceListMapper.toDto(priceList);
    }

    /**
     * Partially update a priceList.
     *
     * @param priceListDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PriceListDTO> partialUpdate(PriceListDTO priceListDTO) {
        log.debug("Request to partially update PriceList : {}", priceListDTO);

        return priceListRepository
            .findById(priceListDTO.getId())
            .map(existingPriceList -> {
                priceListMapper.partialUpdate(existingPriceList, priceListDTO);

                return existingPriceList;
            })
            .map(priceListRepository::save)
            .map(priceListMapper::toDto);
    }

    /**
     * Get all the priceLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PriceListDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PriceLists");
        return priceListRepository.findAll(pageable).map(priceListMapper::toDto);
    }

    /**
     * Get all the priceLists with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PriceListDTO> findAllWithEagerRelationships(Pageable pageable) {
        return priceListRepository.findAllWithEagerRelationships(pageable).map(priceListMapper::toDto);
    }

    /**
     * Get one priceList by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PriceListDTO> findOne(Long id) {
        log.debug("Request to get PriceList : {}", id);
        return priceListRepository.findOneWithEagerRelationships(id).map(priceListMapper::toDto);
    }

    /**
     * Delete the priceList by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PriceList : {}", id);
        priceListRepository.deleteById(id);
    }
}
