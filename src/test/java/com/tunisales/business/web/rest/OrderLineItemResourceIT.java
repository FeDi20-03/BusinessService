package com.tunisales.business.web.rest;

import static com.tunisales.business.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.OrderLineItem;
import com.tunisales.business.repository.OrderLineItemRepository;
import com.tunisales.business.service.dto.OrderLineItemDTO;
import com.tunisales.business.service.mapper.OrderLineItemMapper;
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
 * Integration tests for the {@link OrderLineItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderLineItemResourceIT {

    private static final Long DEFAULT_STOCK_ITEM_ID = 1L;
    private static final Long UPDATED_STOCK_ITEM_ID = 2L;

    private static final String DEFAULT_STOCK_ITEM_IMEI = "AAAAAAAAAA";
    private static final String UPDATED_STOCK_ITEM_IMEI = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_ASSIGNED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ASSIGNED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/order-line-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private OrderLineItemMapper orderLineItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderLineItemMockMvc;

    private OrderLineItem orderLineItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderLineItem createEntity(EntityManager em) {
        OrderLineItem orderLineItem = new OrderLineItem()
            .stockItemId(DEFAULT_STOCK_ITEM_ID)
            .stockItemImei(DEFAULT_STOCK_ITEM_IMEI)
            .assignedAt(DEFAULT_ASSIGNED_AT);
        // Add required entity
        OrderLine orderLine;
        if (TestUtil.findAll(em, OrderLine.class).isEmpty()) {
            orderLine = OrderLineResourceIT.createEntity(em);
            em.persist(orderLine);
            em.flush();
        } else {
            orderLine = TestUtil.findAll(em, OrderLine.class).get(0);
        }
        orderLineItem.setOrderLine(orderLine);
        return orderLineItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderLineItem createUpdatedEntity(EntityManager em) {
        OrderLineItem orderLineItem = new OrderLineItem()
            .stockItemId(UPDATED_STOCK_ITEM_ID)
            .stockItemImei(UPDATED_STOCK_ITEM_IMEI)
            .assignedAt(UPDATED_ASSIGNED_AT);
        // Add required entity
        OrderLine orderLine;
        if (TestUtil.findAll(em, OrderLine.class).isEmpty()) {
            orderLine = OrderLineResourceIT.createUpdatedEntity(em);
            em.persist(orderLine);
            em.flush();
        } else {
            orderLine = TestUtil.findAll(em, OrderLine.class).get(0);
        }
        orderLineItem.setOrderLine(orderLine);
        return orderLineItem;
    }

    @BeforeEach
    public void initTest() {
        orderLineItem = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderLineItem() throws Exception {
        int databaseSizeBeforeCreate = orderLineItemRepository.findAll().size();
        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);
        restOrderLineItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeCreate + 1);
        OrderLineItem testOrderLineItem = orderLineItemList.get(orderLineItemList.size() - 1);
        assertThat(testOrderLineItem.getStockItemId()).isEqualTo(DEFAULT_STOCK_ITEM_ID);
        assertThat(testOrderLineItem.getStockItemImei()).isEqualTo(DEFAULT_STOCK_ITEM_IMEI);
        assertThat(testOrderLineItem.getAssignedAt()).isEqualTo(DEFAULT_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void createOrderLineItemWithExistingId() throws Exception {
        // Create the OrderLineItem with an existing ID
        orderLineItem.setId(1L);
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        int databaseSizeBeforeCreate = orderLineItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderLineItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStockItemIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderLineItemRepository.findAll().size();
        // set the field null
        orderLineItem.setStockItemId(null);

        // Create the OrderLineItem, which fails.
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        restOrderLineItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssignedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderLineItemRepository.findAll().size();
        // set the field null
        orderLineItem.setAssignedAt(null);

        // Create the OrderLineItem, which fails.
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        restOrderLineItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderLineItems() throws Exception {
        // Initialize the database
        orderLineItemRepository.saveAndFlush(orderLineItem);

        // Get all the orderLineItemList
        restOrderLineItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderLineItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockItemId").value(hasItem(DEFAULT_STOCK_ITEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].stockItemImei").value(hasItem(DEFAULT_STOCK_ITEM_IMEI)))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(sameInstant(DEFAULT_ASSIGNED_AT))));
    }

    @Test
    @Transactional
    void getOrderLineItem() throws Exception {
        // Initialize the database
        orderLineItemRepository.saveAndFlush(orderLineItem);

        // Get the orderLineItem
        restOrderLineItemMockMvc
            .perform(get(ENTITY_API_URL_ID, orderLineItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderLineItem.getId().intValue()))
            .andExpect(jsonPath("$.stockItemId").value(DEFAULT_STOCK_ITEM_ID.intValue()))
            .andExpect(jsonPath("$.stockItemImei").value(DEFAULT_STOCK_ITEM_IMEI))
            .andExpect(jsonPath("$.assignedAt").value(sameInstant(DEFAULT_ASSIGNED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingOrderLineItem() throws Exception {
        // Get the orderLineItem
        restOrderLineItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderLineItem() throws Exception {
        // Initialize the database
        orderLineItemRepository.saveAndFlush(orderLineItem);

        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();

        // Update the orderLineItem
        OrderLineItem updatedOrderLineItem = orderLineItemRepository.findById(orderLineItem.getId()).get();
        // Disconnect from session so that the updates on updatedOrderLineItem are not directly saved in db
        em.detach(updatedOrderLineItem);
        updatedOrderLineItem.stockItemId(UPDATED_STOCK_ITEM_ID).stockItemImei(UPDATED_STOCK_ITEM_IMEI).assignedAt(UPDATED_ASSIGNED_AT);
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(updatedOrderLineItem);

        restOrderLineItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderLineItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
        OrderLineItem testOrderLineItem = orderLineItemList.get(orderLineItemList.size() - 1);
        assertThat(testOrderLineItem.getStockItemId()).isEqualTo(UPDATED_STOCK_ITEM_ID);
        assertThat(testOrderLineItem.getStockItemImei()).isEqualTo(UPDATED_STOCK_ITEM_IMEI);
        assertThat(testOrderLineItem.getAssignedAt()).isEqualTo(UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void putNonExistingOrderLineItem() throws Exception {
        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();
        orderLineItem.setId(count.incrementAndGet());

        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderLineItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderLineItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderLineItem() throws Exception {
        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();
        orderLineItem.setId(count.incrementAndGet());

        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderLineItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderLineItem() throws Exception {
        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();
        orderLineItem.setId(count.incrementAndGet());

        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderLineItemMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderLineItemWithPatch() throws Exception {
        // Initialize the database
        orderLineItemRepository.saveAndFlush(orderLineItem);

        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();

        // Update the orderLineItem using partial update
        OrderLineItem partialUpdatedOrderLineItem = new OrderLineItem();
        partialUpdatedOrderLineItem.setId(orderLineItem.getId());

        partialUpdatedOrderLineItem.assignedAt(UPDATED_ASSIGNED_AT);

        restOrderLineItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderLineItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderLineItem))
            )
            .andExpect(status().isOk());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
        OrderLineItem testOrderLineItem = orderLineItemList.get(orderLineItemList.size() - 1);
        assertThat(testOrderLineItem.getStockItemId()).isEqualTo(DEFAULT_STOCK_ITEM_ID);
        assertThat(testOrderLineItem.getStockItemImei()).isEqualTo(DEFAULT_STOCK_ITEM_IMEI);
        assertThat(testOrderLineItem.getAssignedAt()).isEqualTo(UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void fullUpdateOrderLineItemWithPatch() throws Exception {
        // Initialize the database
        orderLineItemRepository.saveAndFlush(orderLineItem);

        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();

        // Update the orderLineItem using partial update
        OrderLineItem partialUpdatedOrderLineItem = new OrderLineItem();
        partialUpdatedOrderLineItem.setId(orderLineItem.getId());

        partialUpdatedOrderLineItem
            .stockItemId(UPDATED_STOCK_ITEM_ID)
            .stockItemImei(UPDATED_STOCK_ITEM_IMEI)
            .assignedAt(UPDATED_ASSIGNED_AT);

        restOrderLineItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderLineItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderLineItem))
            )
            .andExpect(status().isOk());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
        OrderLineItem testOrderLineItem = orderLineItemList.get(orderLineItemList.size() - 1);
        assertThat(testOrderLineItem.getStockItemId()).isEqualTo(UPDATED_STOCK_ITEM_ID);
        assertThat(testOrderLineItem.getStockItemImei()).isEqualTo(UPDATED_STOCK_ITEM_IMEI);
        assertThat(testOrderLineItem.getAssignedAt()).isEqualTo(UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingOrderLineItem() throws Exception {
        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();
        orderLineItem.setId(count.incrementAndGet());

        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderLineItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderLineItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderLineItem() throws Exception {
        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();
        orderLineItem.setId(count.incrementAndGet());

        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderLineItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderLineItem() throws Exception {
        int databaseSizeBeforeUpdate = orderLineItemRepository.findAll().size();
        orderLineItem.setId(count.incrementAndGet());

        // Create the OrderLineItem
        OrderLineItemDTO orderLineItemDTO = orderLineItemMapper.toDto(orderLineItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderLineItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderLineItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderLineItem in the database
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderLineItem() throws Exception {
        // Initialize the database
        orderLineItemRepository.saveAndFlush(orderLineItem);

        int databaseSizeBeforeDelete = orderLineItemRepository.findAll().size();

        // Delete the orderLineItem
        restOrderLineItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderLineItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderLineItem> orderLineItemList = orderLineItemRepository.findAll();
        assertThat(orderLineItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
