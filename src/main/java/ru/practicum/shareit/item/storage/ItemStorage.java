package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemStorage {
    private final HashMap<Long, Item> storage = new HashMap<>();
    private Long id = 1L;

    public List<Item> getAllByOwner(Long userId) {
        return storage.values().stream()
                .filter(a -> Objects.equals(a.getOwner(), userId))
                .collect(Collectors.toList());
    }

    public Item getById(Long id) {
        return storage.get(id);
    }

    public Item add(Item item) {
        item.setId(id);
        id++;
        storage.put(item.getId(), item);
        return storage.get(item.getId());
    }

    public Item update(ItemDto itemDto) {
        Item updatedItem = storage.get(itemDto.getId());
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        storage.put(updatedItem.getId(), updatedItem);
        return storage.get(itemDto.getId());
    }

    public List<Item> searchItemsByNameAndDescription(String text) {
        return storage.values().stream()
                .filter((a) -> (a.getName().toLowerCase().contains(text) ||
                        a.getDescription().toLowerCase().contains(text)) && a.getAvailable())
                .collect(Collectors.toList());
    }
}
