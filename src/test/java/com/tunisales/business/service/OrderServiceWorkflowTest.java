package com.tunisales.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.enumeration.OrderStatus;
import com.tunisales.business.repository.OrderRepository;
import com.tunisales.business.service.PaymentEligibilityService.EligibilityResult;
import com.tunisales.business.service.dto.OrderDTO;
import com.tunisales.business.service.mapper.OrderMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Unit tests for the {@link OrderService} workflow transitions
 * (submit / validate / negotiate / reject).
 *
 * <p>The repository and mapper are mocked so that we test the state-machine
 * logic in isolation, without touching the database.</p>
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceWorkflowTest {

    private static final Long ORDER_ID = 42L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PaymentEligibilityService paymentEligibilityService;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order().id(ORDER_ID);
        // The mapper is invoked at the end of each successful transition; return an empty DTO.
        // lenient() because negative-path tests (404 / 409) never reach save+mapper.
        lenient().when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDTO());
        // save returns the same entity that was passed in.
        lenient().when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Default: payment is always eligible (CHECK logic tested in PaymentEligibilityServiceTest).
        lenient().when(paymentEligibilityService.assess(any(Order.class))).thenReturn(EligibilityResult.ok());
    }

    // ---------- submit ----------

    @Test
    void submit_movesDraftToSubmitted() {
        order.setStatus(OrderStatus.DRAFT);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        orderService.submit(ORDER_ID);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.SUBMITTED);
        assertThat(saved.getSubmittedAt()).isNotNull();
    }

    @Test
    void submit_rejects409WhenNotDraft() {
        order.setStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.submit(ORDER_ID))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(e -> ((ResponseStatusException) e).getStatus())
            .isEqualTo(HttpStatus.CONFLICT);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void submit_returns404WhenMissing() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.submit(ORDER_ID))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(e -> ((ResponseStatusException) e).getStatus())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ---------- validate ----------

    @Test
    void validate_movesSubmittedToValidated() {
        order.setStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        orderService.validate(ORDER_ID, "admin");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.VALIDATED);
        assertThat(saved.getValidatedAt()).isNotNull();
    }

    @Test
    void validate_rejects409WhenNotSubmitted() {
        order.setStatus(OrderStatus.DRAFT);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.validate(ORDER_ID, "admin"))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(e -> ((ResponseStatusException) e).getStatus())
            .isEqualTo(HttpStatus.CONFLICT);
    }

    // ---------- negotiate ----------

    @Test
    void negotiate_movesSubmittedToNegotiatingAndPersistsReason() {
        order.setStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        String reason = "Client wants better pricing";
        orderService.negotiate(ORDER_ID, reason);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.NEGOTIATING);
        assertThat(saved.getNegotiationReason()).isEqualTo(reason);
    }

    @Test
    void negotiate_rejects409WhenNotSubmitted() {
        order.setStatus(OrderStatus.VALIDATED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.negotiate(ORDER_ID, "any"))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(e -> ((ResponseStatusException) e).getStatus())
            .isEqualTo(HttpStatus.CONFLICT);
    }

    // ---------- reject ----------

    @Test
    void reject_movesSubmittedToRejectedAndPersistsReason() {
        order.setStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        String reason = "Insufficient credit";
        orderService.reject(ORDER_ID, reason);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.REJECTED);
        assertThat(saved.getRejectionReason()).isEqualTo(reason);
    }

    @Test
    void reject_rejects409WhenNotSubmitted() {
        order.setStatus(OrderStatus.NEGOTIATING);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.reject(ORDER_ID, "any"))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(e -> ((ResponseStatusException) e).getStatus())
            .isEqualTo(HttpStatus.CONFLICT);
    }

    // ---------- idempotence ----------

    @Test
    void submit_isNotIdempotent_secondCallReturns409() {
        order.setStatus(OrderStatus.DRAFT);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        orderService.submit(ORDER_ID);
        // After the first submit, status is SUBMITTED — a second call must yield 409.
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SUBMITTED);

        assertThatThrownBy(() -> orderService.submit(ORDER_ID))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(e -> ((ResponseStatusException) e).getStatus())
            .isEqualTo(HttpStatus.CONFLICT);
    }
}
