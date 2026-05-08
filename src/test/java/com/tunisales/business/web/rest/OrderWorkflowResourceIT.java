package com.tunisales.business.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tunisales.business.IntegrationTest;
import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.enumeration.OrderStatus;
import com.tunisales.business.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
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
 * Integration tests for the Order workflow endpoints
 * (submit / validate / negotiate / reject) introduced in step 2.1.
 */
@IntegrationTest
@AutoConfigureMockMvc
class OrderWorkflowResourceIT {

    private static final String API_URL = "/api/orders";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc mockMvc;

    private Order order;

    @BeforeEach
    void initTest() {
        order = newOrder(OrderStatus.DRAFT);
    }

    /**
     * Builds a minimal valid Order in the requested initial status, persisted via the EntityManager
     * (bypassing the REST layer so we can pre-seed any state).
     */
    private Order newOrder(OrderStatus status) {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        Order o = new Order()
            .tenantId(1L)
            .orderNumber("WF" + System.nanoTime())
            .status(status)
            .subtotal(BigDecimal.ZERO)
            .totalAmount(BigDecimal.ZERO)
            .isDeleted(false)
            .createdAt(ZonedDateTime.now())
            .client(client);
        em.persist(o);
        em.flush();
        return o;
    }

    private static byte[] json(Map<String, Object> map) {
        try {
            return TestUtil.convertObjectToJsonBytes(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- submit ----------

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_USER" })
    void submit_movesDraftToSubmitted() throws Exception {
        mockMvc.perform(post(API_URL + "/{id}/submit", order.getId())).andExpect(status().isOk());

        Order reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.SUBMITTED);
        assertThat(reloaded.getSubmittedAt()).isNotNull();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_USER" })
    void submit_returns409WhenAlreadySubmitted() throws Exception {
        order.setStatus(OrderStatus.SUBMITTED);
        em.merge(order);
        em.flush();

        mockMvc.perform(post(API_URL + "/{id}/submit", order.getId())).andExpect(status().isConflict());
    }

    // ---------- validate ----------

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_ADMIN_COMMERCIAL" })
    void validate_movesSubmittedToValidated() throws Exception {
        order.setStatus(OrderStatus.SUBMITTED);
        em.merge(order);
        em.flush();

        Map<String, Object> body = new HashMap<>();
        body.put("adminLogin", "alice");

        mockMvc
            .perform(post(API_URL + "/{id}/validate", order.getId()).contentType(MediaType.APPLICATION_JSON).content(json(body)))
            .andExpect(status().isOk());

        Order reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(reloaded.getValidatedAt()).isNotNull();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_USER" })
    void validate_isForbiddenForNonAdmin() throws Exception {
        order.setStatus(OrderStatus.SUBMITTED);
        em.merge(order);
        em.flush();

        mockMvc
            .perform(post(API_URL + "/{id}/validate", order.getId()).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    // ---------- negotiate ----------

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_ADMIN_SYSTEME" })
    void negotiate_movesSubmittedToNegotiatingAndPersistsReason() throws Exception {
        order.setStatus(OrderStatus.SUBMITTED);
        em.merge(order);
        em.flush();

        Map<String, Object> body = new HashMap<>();
        body.put("reason", "Client demande une remise supplémentaire");

        mockMvc
            .perform(post(API_URL + "/{id}/negotiate", order.getId()).contentType(MediaType.APPLICATION_JSON).content(json(body)))
            .andExpect(status().isOk());

        Order reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.NEGOTIATING);
        assertThat(reloaded.getNegotiationReason()).isEqualTo("Client demande une remise supplémentaire");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_ADMIN_COMMERCIAL" })
    void negotiate_rejectsBlankReason() throws Exception {
        order.setStatus(OrderStatus.SUBMITTED);
        em.merge(order);
        em.flush();

        // Missing required "reason" field — Bean Validation must reject the body.
        mockMvc
            .perform(post(API_URL + "/{id}/negotiate", order.getId()).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }

    // ---------- reject ----------

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_ADMIN_COMMERCIAL" })
    void reject_movesSubmittedToRejectedAndPersistsReason() throws Exception {
        order.setStatus(OrderStatus.SUBMITTED);
        em.merge(order);
        em.flush();

        Map<String, Object> body = new HashMap<>();
        body.put("reason", "Crédit insuffisant");

        mockMvc
            .perform(post(API_URL + "/{id}/reject", order.getId()).contentType(MediaType.APPLICATION_JSON).content(json(body)))
            .andExpect(status().isOk());

        Order reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.REJECTED);
        assertThat(reloaded.getRejectionReason()).isEqualTo("Crédit insuffisant");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { "ROLE_ADMIN_COMMERCIAL" })
    void reject_returns409FromDraftState() throws Exception {
        // Order is created in DRAFT — reject() requires SUBMITTED, so 409 is expected.
        Map<String, Object> body = new HashMap<>();
        body.put("reason", "any");

        mockMvc
            .perform(post(API_URL + "/{id}/reject", order.getId()).contentType(MediaType.APPLICATION_JSON).content(json(body)))
            .andExpect(status().isConflict());
    }
}
