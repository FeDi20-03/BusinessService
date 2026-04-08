package com.tunisales.business.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.business.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientContact.class);
        ClientContact clientContact1 = new ClientContact();
        clientContact1.setId(1L);
        ClientContact clientContact2 = new ClientContact();
        clientContact2.setId(clientContact1.getId());
        assertThat(clientContact1).isEqualTo(clientContact2);
        clientContact2.setId(2L);
        assertThat(clientContact1).isNotEqualTo(clientContact2);
        clientContact1.setId(null);
        assertThat(clientContact1).isNotEqualTo(clientContact2);
    }
}
