package com.tunisales.business.service.mapper;

import com.tunisales.business.domain.Order;
import com.tunisales.business.domain.OrderLine;
import com.tunisales.business.domain.Product;
import com.tunisales.business.service.dto.OrderDTO;
import com.tunisales.business.service.dto.OrderLineDTO;
import com.tunisales.business.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderLine} and its DTO {@link OrderLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderLineMapper extends EntityMapper<OrderLineDTO, OrderLine> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderOrderNumber")
    OrderLineDTO toDto(OrderLine s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("orderOrderNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    OrderDTO toDtoOrderOrderNumber(Order order);
}
