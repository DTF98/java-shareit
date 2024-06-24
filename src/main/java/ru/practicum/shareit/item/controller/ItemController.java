package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationGroup;

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

    @Validated({ValidationGroup.Update.class})
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto,
                                          @PathVariable Long itemId) {
        log.info("Обновление вещи пользователем по id = {}", userId);
        return ResponseEntity.ok(itemService.update(itemDto, itemId, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam("text") String text) {
        log.info("Поиск вещей по названию и описанию: {}, пользователем по id = {}", text, userId);
        return ResponseEntity.ok(itemService.searchItems(text.toLowerCase(), userId));
    }

    @Validated({ValidationGroup.Create.class})
    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавление вещи пользователем по id = {}", userId);
        return ResponseEntity.ok(itemService.add(itemDto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание отзыва comment={} на предмет itemId={} от пользователя userId={}", commentDto, itemId, userId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}
