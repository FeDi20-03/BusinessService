package com.tunisales.business.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tunisales.business.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PriceListDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PriceListDTO.class);
        PriceListDTO priceListDTO1 = new PriceListDTO();
        priceListDTO1.setId(1L);
        PriceListDTO priceListDTO2 = new PriceListDTO();
        assertThat(priceListDTO1).isNotEqualTo(priceListDTO2);
        priceListDTO2.setId(priceListDTO1.getId());
        assertThat(priceListDTO1).isEqualTo(priceListDTO2);
        priceListDTO2.setId(2L);
        assertThat(priceListDTO1).isNotEqualTo(priceListDTO2);
        priceListDTO1.setId(null);
        assertThat(priceListDTO1).isNotEqualTo(priceListDTO2);
    }
}
