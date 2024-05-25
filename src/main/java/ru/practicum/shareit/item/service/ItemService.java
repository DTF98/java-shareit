package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllByOwner(Long userId);

    ItemDto getById(Long id, Long userId);

    ItemDto add(Item item, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    List<ItemDto> searchItems(String text, Long userId);
}
