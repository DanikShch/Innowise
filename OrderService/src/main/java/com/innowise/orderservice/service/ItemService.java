package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.request.ItemRequestDto;
import com.innowise.orderservice.dto.response.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(ItemRequestDto itemRequestDto);
    ItemResponseDto getItemById(Long id);
    List<ItemResponseDto> getAllItems();
    ItemResponseDto updateItem(Long id, ItemRequestDto itemRequestDto);
    void deleteItem(Long id);
}