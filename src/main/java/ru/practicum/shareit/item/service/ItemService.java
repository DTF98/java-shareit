package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemDto> getAllByOwner(Long userId);

    ItemDto getById(Long id, Long userId);

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchItems(String text, Long userId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
