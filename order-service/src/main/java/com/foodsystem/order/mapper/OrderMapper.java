package com.foodsystem.order.mapper;

import com.foodsystem.order.entity.Order;
import com.foodsystem.order.dto.OrderResponse;
import com.foodsystem.order.dto.OrderCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "id", target = "orderId")
    OrderResponse toResponse(Order order);

    Order toEntity(OrderCreateRequest request);
}
