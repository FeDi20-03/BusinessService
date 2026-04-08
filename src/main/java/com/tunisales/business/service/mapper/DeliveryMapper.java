package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Delivery;
import com.tunisales.business.domain.Order;
import com.tunisales.business.service.dto.DeliveryDTO;
import com.tunisales.business.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Delivery} and its DTO {@link DeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper extends EntityMapper<DeliveryDTO, Delivery> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderOrderNumber")
    DeliveryDTO toDto(Delivery s);

    @Named("orderOrderNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    OrderDTO toDtoOrderOrderNumber(Order order);
}
