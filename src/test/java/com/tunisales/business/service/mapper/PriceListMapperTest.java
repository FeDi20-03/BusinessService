package com.tunisales.business.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceListMapperTest {

    private PriceListMapper priceListMapper;

    @BeforeEach
    public void setUp() {
        priceListMapper = new PriceListMapperImpl();
    }
}
