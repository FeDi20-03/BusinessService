package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.OrderLineItem;
import com.tunisales.business.service.dto.OrderLineDTO;
import com.tunisales.business.service.dto.OrderLineItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderLineItem} and its DTO {@link OrderLineItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderLineItemMapper extends EntityMapper<OrderLineItemDTO, OrderLineItem> {
    @Mapping(target = "orderLine", source = "orderLine", qualifiedByName = "orderLineId")
    OrderLineItemDTO toDto(OrderLineItem s);

    @Named("orderLineId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderLineDTO toDtoOrderLineId(OrderLine orderLine);
}
