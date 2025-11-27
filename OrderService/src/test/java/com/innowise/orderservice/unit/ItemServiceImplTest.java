package com.innowise.orderservice.unit;

import com.innowise.orderservice.dto.request.ItemRequestDto;
import com.innowise.orderservice.dto.response.ItemResponseDto;
import com.innowise.orderservice.exception.ItemNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto();
    private final Item itemEntity = new Item();
    private final ItemResponseDto itemResponseDto = new ItemResponseDto();

    private void setupTestData() {
        itemRequestDto.setName("Test Item");
        itemRequestDto.setPrice(new BigDecimal("99.99"));

        itemEntity.setId(1L);
        itemEntity.setName("Test Item");
        itemEntity.setPrice(new BigDecimal("99.99"));

        itemResponseDto.setId(1L);
        itemResponseDto.setName("Test Item");
        itemResponseDto.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void createItem_Success() {
        setupTestData();

        when(itemMapper.toEntity(itemRequestDto)).thenReturn(itemEntity);
        when(itemRepository.save(itemEntity)).thenReturn(itemEntity);
        when(itemMapper.toDto(itemEntity)).thenReturn(itemResponseDto);

        ItemResponseDto result = itemService.createItem(itemRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());

        verify(itemMapper).toEntity(itemRequestDto);
        verify(itemRepository).save(itemEntity);
        verify(itemMapper).toDto(itemEntity);
    }

    @Test
    void getItemById_Success() {
        setupTestData();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemEntity));
        when(itemMapper.toDto(itemEntity)).thenReturn(itemResponseDto);

        ItemResponseDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());

        verify(itemRepository).findById(1L);
        verify(itemMapper).toDto(itemEntity);
    }

    @Test
    void getItemById_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1L));

        verify(itemRepository).findById(1L);
        verify(itemMapper, never()).toDto(any());
    }

    @Test
    void getAllItems_Success() {
        setupTestData();

        List<Item> items = List.of(itemEntity);
        when(itemRepository.findAll()).thenReturn(items);
        when(itemMapper.toDto(itemEntity)).thenReturn(itemResponseDto);

        List<ItemResponseDto> result = itemService.getAllItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Test Item", result.getFirst().getName());

        verify(itemRepository).findAll();
        verify(itemMapper).toDto(itemEntity);
    }

    @Test
    void getAllItems_Empty() {
        when(itemRepository.findAll()).thenReturn(List.of());

        List<ItemResponseDto> result = itemService.getAllItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository).findAll();
        verify(itemMapper, never()).toDto(any());
    }

    @Test
    void updateItem_Success() {
        setupTestData();

        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setName("Updated Item");
        updateDto.setPrice(new BigDecimal("149.99"));

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Item");
        updatedItem.setPrice(new BigDecimal("149.99"));

        ItemResponseDto updatedResponse = new ItemResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Item");
        updatedResponse.setPrice(new BigDecimal("149.99"));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemEntity));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.toDto(updatedItem)).thenReturn(updatedResponse);

        ItemResponseDto result = itemService.updateItem(1L, updateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Item", result.getName());
        assertEquals(new BigDecimal("149.99"), result.getPrice());

        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toDto(updatedItem);
    }

    @Test
    void updateItem_NotFound() {
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setName("Updated Item");
        updateDto.setPrice(new BigDecimal("149.99"));

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(1L, updateDto));

        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).save(any());
        verify(itemMapper, never()).toDto(any());
    }

    @Test
    void deleteItem_Success() {
        setupTestData();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemEntity));
        doNothing().when(itemRepository).delete(itemEntity);

        itemService.deleteItem(1L);

        verify(itemRepository).findById(1L);
        verify(itemRepository).delete(itemEntity);
    }

    @Test
    void deleteItem_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(1L));

        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).delete(any());
    }
}