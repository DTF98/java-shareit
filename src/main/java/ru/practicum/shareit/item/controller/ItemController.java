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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Collection<AdvancedItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение всех вещей пользователя по id = {}",userId);
        return ResponseEntity.ok(itemService.getAllByOwner(userId, from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<AdvancedItemDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
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
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поиск доступных предметов text={}", text);
        return ResponseEntity.ok(itemService.searchItems(text.toLowerCase(), from, size));
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
