package com.tunisales.business.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderLineMapperTest {

    private OrderLineMapper orderLineMapper;

    @BeforeEach
    public void setUp() {
        orderLineMapper = new OrderLineMapperImpl();
    }
}
