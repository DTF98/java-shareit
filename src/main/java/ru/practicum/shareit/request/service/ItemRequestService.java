package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AdvancedRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    List<AdvancedRequestItemDto> getOwnRequests(Long userId, int from, int size);

    List<AdvancedRequestItemDto> getAllRequests(Long userId, int from, int size);

    AdvancedRequestItemDto getRequestById(Long userId, Long requestId);

    Map<Long, List<Item>> getRequestItemMapping(Set<Long> requestIds);
}
