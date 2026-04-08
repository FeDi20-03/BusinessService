package com.tunisales.business.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.business.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderLineItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderLineItemDTO.class);
        OrderLineItemDTO orderLineItemDTO1 = new OrderLineItemDTO();
        orderLineItemDTO1.setId(1L);
        OrderLineItemDTO orderLineItemDTO2 = new OrderLineItemDTO();
        assertThat(orderLineItemDTO1).isNotEqualTo(orderLineItemDTO2);
        orderLineItemDTO2.setId(orderLineItemDTO1.getId());
        assertThat(orderLineItemDTO1).isEqualTo(orderLineItemDTO2);
        orderLineItemDTO2.setId(2L);
        assertThat(orderLineItemDTO1).isNotEqualTo(orderLineItemDTO2);
        orderLineItemDTO1.setId(null);
        assertThat(orderLineItemDTO1).isNotEqualTo(orderLineItemDTO2);
    }
}
