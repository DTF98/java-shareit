package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AdvancedRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса requestDto={}", itemRequestDto);
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    Collection<AdvancedRequestItemDto> getOwnRequests(@RequestParam Integer from,
                                                      @RequestParam Integer size,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка своих запросов для пользователя userId={}, from={}, size={}, ", from, size, userId);
        return itemRequestService.getOwnRequests(userId, from, size);
    }

    @GetMapping("/all")
    Collection<AdvancedRequestItemDto> getAllRequests(@RequestParam Integer from,
                                                      @RequestParam Integer size,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка чужих запросов для пользователя userId={}, from={}, size={}", userId, from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    AdvancedRequestItemDto getRequest(@PathVariable Long requestId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запроса requestId={}, userId={}", requestId, userId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
