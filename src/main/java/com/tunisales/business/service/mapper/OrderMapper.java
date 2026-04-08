package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Client;
import com.tunisales.business.domain.Order;
import com.tunisales.business.service.dto.ClientDTO;
import com.tunisales.business.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientName")
    OrderDTO toDto(Order s);

    @Named("clientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ClientDTO toDtoClientName(Client client);
}
