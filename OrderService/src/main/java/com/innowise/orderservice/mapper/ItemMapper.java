package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.request.ItemRequestDto;
import com.innowise.orderservice.dto.response.ItemResponseDto;
import com.innowise.orderservice.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    Item toEntity(ItemRequestDto dto);

    ItemResponseDto toDto(Item entity);
}