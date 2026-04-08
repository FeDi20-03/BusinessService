package com.tunisales.business.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientContactMapperTest {

    private ClientContactMapper clientContactMapper;

    @BeforeEach
    public void setUp() {
        clientContactMapper = new ClientContactMapperImpl();
    }
}
