package com.innowise.orderservice.dto.mapper;

import com.innowise.orderservice.dto.request.OrderRequestDto;
import com.innowise.orderservice.dto.response.OrderResponseDto;
import com.innowise.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequestDto dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    OrderResponseDto toDto(Order entity);
}