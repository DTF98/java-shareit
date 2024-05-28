package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getAllByOwner(Long userId) {
        getUserById(userId);
        return ItemMapper.toDto(itemStorage.getAllByOwner(userId));
    }

    public ItemDto getById(Long id, Long userId) {
        getUserById(userId);
        return ItemMapper.toDto(itemStorage.getById(id).orElseThrow(() ->
                new NotFoundException(String.format("Не найдена вещь по id = %s", id))));
    }

    public ItemDto add(Item item, Long userId) {
        getUserById(userId);
        item.setOwner(userId);
        return ItemMapper.toDto(itemStorage.add(item));
    }

    public ItemDto update(ItemDto itemDto, Long userId) {
        getUserById(userId);
        List<Item> items = new ArrayList<>();
        if (itemDto.getId() == null) {
            items = itemStorage.getAllByOwner(userId);
        }
        if (items.size() == 1) {
            itemDto.setId(items.get(0).getId());
        }
        Item itemUpdated = itemStorage.getById(itemDto.getId()).orElseThrow(() ->
                new NotFoundException(String.format("Не найдена вещь по id = %s", itemDto.getId())));
        if (!Objects.equals(itemUpdated.getOwner(), userId)) {
            log.error("Ошибка доступа, пользователь с id = {}, не может редактировать вещь по id = {}",
                    userId, itemUpdated.getId());
            throw new AccessException(String.format("Ошибка доступа, пользователь с id = %s, не может редактировать" +
                    " вещь по id = %s", userId, itemUpdated.getId()));
        }
        return ItemMapper.toDto(itemStorage.update(itemDto));
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        getUserById(userId);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return ItemMapper.toDto(itemStorage.searchItemsByNameOrDescription(text));
    }

    private User getUserById(Long id) {
        return userStorage.getById(id).orElseThrow(() ->
                new NotFoundException(String.format("Не найден пользователь по id = %s", id)));
    }
}
