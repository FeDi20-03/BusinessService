package com.tunisales.business.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.business.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientContactDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientContactDTO.class);
        ClientContactDTO clientContactDTO1 = new ClientContactDTO();
        clientContactDTO1.setId(1L);
        ClientContactDTO clientContactDTO2 = new ClientContactDTO();
        assertThat(clientContactDTO1).isNotEqualTo(clientContactDTO2);
        clientContactDTO2.setId(clientContactDTO1.getId());
        assertThat(clientContactDTO1).isEqualTo(clientContactDTO2);
        clientContactDTO2.setId(2L);
        assertThat(clientContactDTO1).isNotEqualTo(clientContactDTO2);
        clientContactDTO1.setId(null);
        assertThat(clientContactDTO1).isNotEqualTo(clientContactDTO2);
    }
}
