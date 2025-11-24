package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.dto.response.OrderItemResponseDto;
import com.innowise.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "item", ignore = true)
    OrderItem toEntity(OrderItemRequestDto dto);

    OrderItemResponseDto toDto(OrderItem entity);
}
