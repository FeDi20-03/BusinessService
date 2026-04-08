package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.PriceList;
import com.tunisales.business.domain.Product;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.dto.PriceListDTO;
import com.tunisales.business.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PriceList} and its DTO {@link PriceListDTO}.
 */
@Mapper(componentModel = "spring")
public interface PriceListMapper extends EntityMapper<PriceListDTO, PriceList> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "client", source = "client", qualifiedByName = "clientName")
    PriceListDTO toDto(PriceList s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("clientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ClientDTO toDtoClientName(Client client);
}
