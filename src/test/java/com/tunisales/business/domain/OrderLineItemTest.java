package com.tunisales.business.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.business.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderLineItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderLineItem.class);
        OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setId(1L);
        OrderLineItem orderLineItem2 = new OrderLineItem();
        orderLineItem2.setId(orderLineItem1.getId());
        assertThat(orderLineItem1).isEqualTo(orderLineItem2);
        orderLineItem2.setId(2L);
        assertThat(orderLineItem1).isNotEqualTo(orderLineItem2);
        orderLineItem1.setId(null);
        assertThat(orderLineItem1).isNotEqualTo(orderLineItem2);
    }
}
