package com.tunisales.business.service;

import com.tunisales.business.domain.OrderLineItem;
import com.tunisales.business.repository.OrderLineItemRepository;
import com.tunisales.business.service.dto.OrderLineItemDTO;
import com.tunisales.business.service.mapper.OrderLineItemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderLineItem}.
 */
@Service
@Transactional
public class OrderLineItemService {

    private final Logger log = LoggerFactory.getLogger(OrderLineItemService.class);

    private final OrderLineItemRepository orderLineItemRepository;

    private final OrderLineItemMapper orderLineItemMapper;

    public OrderLineItemService(OrderLineItemRepository orderLineItemRepository, OrderLineItemMapper orderLineItemMapper) {
        this.orderLineItemRepository = orderLineItemRepository;
        this.orderLineItemMapper = orderLineItemMapper;
    }

    /**
     * Save a orderLineItem.
     *
     * @param orderLineItemDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderLineItemDTO save(OrderLineItemDTO orderLineItemDTO) {
        log.debug("Request to save OrderLineItem : {}", orderLineItemDTO);
        OrderLineItem orderLineItem = orderLineItemMapper.toEntity(orderLineItemDTO);
        orderLineItem = orderLineItemRepository.save(orderLineItem);
        return orderLineItemMapper.toDto(orderLineItem);
    }

    /**
     * Update a orderLineItem.
     *
     * @param orderLineItemDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderLineItemDTO update(OrderLineItemDTO orderLineItemDTO) {
        log.debug("Request to update OrderLineItem : {}", orderLineItemDTO);
        OrderLineItem orderLineItem = orderLineItemMapper.toEntity(orderLineItemDTO);
        orderLineItem = orderLineItemRepository.save(orderLineItem);
        return orderLineItemMapper.toDto(orderLineItem);
    }

    /**
     * Partially update a orderLineItem.
     *
     * @param orderLineItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderLineItemDTO> partialUpdate(OrderLineItemDTO orderLineItemDTO) {
        log.debug("Request to partially update OrderLineItem : {}", orderLineItemDTO);

        return orderLineItemRepository
            .findById(orderLineItemDTO.getId())
            .map(existingOrderLineItem -> {
                orderLineItemMapper.partialUpdate(existingOrderLineItem, orderLineItemDTO);

                return existingOrderLineItem;
            })
            .map(orderLineItemRepository::save)
            .map(orderLineItemMapper::toDto);
    }

    /**
     * Get all the orderLineItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderLineItemDTO> findAll() {
        log.debug("Request to get all OrderLineItems");
        return orderLineItemRepository.findAll().stream().map(orderLineItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one orderLineItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderLineItemDTO> findOne(Long id) {
        log.debug("Request to get OrderLineItem : {}", id);
        return orderLineItemRepository.findById(id).map(orderLineItemMapper::toDto);
    }

    /**
     * Delete the orderLineItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderLineItem : {}", id);
        orderLineItemRepository.deleteById(id);
    }
}
