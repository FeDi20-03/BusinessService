package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Invoice;
import com.tunisales.business.domain.Order;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.dto.InvoiceDTO;
import com.tunisales.business.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientName")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderOrderNumber")
    InvoiceDTO toDto(Invoice s);

    @Named("clientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ClientDTO toDtoClientName(Client client);

    @Named("orderOrderNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    OrderDTO toDtoOrderOrderNumber(Order order);
}
