package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя по id = {}",userId);
        return ResponseEntity.ok(itemService.getAllByOwner(userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        log.info("Получение вещи по id = {} пользователем по id = {}",itemId, userId);
        return ResponseEntity.ok(itemService.getById(itemId, userId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody Item item) {
        log.info("Добавление вещи пользователем по id = {}", userId);
        return ResponseEntity.ok(itemService.add(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи пользователем по id = {}", userId);
        return ResponseEntity.ok(itemService.update(itemDto, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam("text") String text) {
        log.info("Поиск вещей по названию и описанию: {}, пользователем по id = {}", text, userId);
        return ResponseEntity.ok(itemService.searchItems(text.toLowerCase(), userId));
    }
}
