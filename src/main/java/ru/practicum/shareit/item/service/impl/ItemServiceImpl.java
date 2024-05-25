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
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getAllByOwner(Long userId) {
        try {
            userStorage.getById(userId);
            return itemStorage.getAllByOwner(userId).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            log.error("Пользователь по id = {} не найден", userId);
            throw new NotFoundException(String.format("Не найден пользователь по id = %s", userId));
        }
    }

    public ItemDto getById(Long id, Long userId) {
        try {
            userStorage.getById(userId);
            return ItemMapper.toItemDto(itemStorage.getById(id));
        } catch (NullPointerException e) {
            log.error("Пользователь по id = {} или вещь по id = {} не найдены", userId, id);
            throw new NotFoundException(String.format("Не найден пользователь по id = %s или вещь по id = %s",
                    userId, id));
        }
    }

    public ItemDto add(Item item, Long userId) {
        try {
            userStorage.getById(userId);
            item.setOwner(userId);
            return ItemMapper.toItemDto(itemStorage.add(item));
        } catch (NullPointerException e) {
            log.error("Пользователь по id = {} не найден", userId);
            throw new NotFoundException(String.format("Не найден пользователь по id = %s", userId));
        }
    }

    public ItemDto update(ItemDto itemDto, Long userId) {
        try {
            userStorage.getById(userId);
            List<Item> items = itemStorage.getAllByOwner(userId);
            if (items.size() == 1) itemDto.setId(items.get(0).getId());
            Item itemUpdated = itemStorage.getById(itemDto.getId());
            if (!Objects.equals(itemUpdated.getOwner(), userId)) {
                log.error("Ошибка доступа, пользователь с id = {}, не может редактировать вещь по id = {}",
                        userId, itemUpdated.getId());
                throw new AccessException(String.format("Ошибка доступа, пользователь с id = %s, не может редактировать" +
                        " вещь по id = %s", userId, itemUpdated.getId()));
            }
            return ItemMapper.toItemDto(itemStorage.update(itemDto));
        } catch (NullPointerException e) {
            log.error("Пользователь по id = {} или вещь не найдены", userId);
            throw new NotFoundException(String.format("Не найден пользователь по id = %s или вещь", userId));
        }
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        try {
            userStorage.getById(userId);
            if (text.isEmpty()) return new ArrayList<>();
            return itemStorage.searchItemsByNameAndDescription(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            log.error("Пользователь по id = {} не найден", userId);
            throw new NotFoundException(String.format("Не найден пользователь по id = %s", userId));
        }
    }
}
