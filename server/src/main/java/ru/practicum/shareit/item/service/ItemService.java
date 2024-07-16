package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemService {
    Collection<AdvancedItemDto> getAllByOwner(Long userId, int from, int size);

    AdvancedItemDto getById(Long id, Long userId);

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    Map<Long, List<Comment>> getItemCommentMapping(Set<Long> itemIds);
}
