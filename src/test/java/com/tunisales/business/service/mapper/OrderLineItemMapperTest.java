package com.tunisales.business.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderLineItemMapperTest {

    private OrderLineItemMapper orderLineItemMapper;

    @BeforeEach
    public void setUp() {
        orderLineItemMapper = new OrderLineItemMapperImpl();
    }
}
