package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AdvancedRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        getUserById(userId);
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequestMapper.toModel(itemRequestDto, userId)));
    }

    public List<AdvancedRequestItemDto> getOwnRequests(Long userId, int from, int size) {
        getUserById(userId);
        Pageable page = Pagination.getPage(from, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findAllByUserId(userId, page);
        Set<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestItemMapping = getRequestItemMapping(requestIds);
        return itemRequestMapper.toListAdvancedItemRequestDto(requests, requestItemMapping);
    }

    public List<AdvancedRequestItemDto> getAllRequests(Long userId, int from, int size) {
        getUserById(userId);
        Pageable page = Pagination.getPage(from, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findAllByUserIdNot(userId, page);
        Set<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestItemMapping = getRequestItemMapping(requestIds);
        return itemRequestMapper.toListAdvancedItemRequestDto(requests, requestItemMapping);
    }

    public AdvancedRequestItemDto getRequestById(Long userId, Long requestId) {
        getUserById(userId);
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос по id = %s не найден!", requestId)));
        Map<Long, List<Item>> requestItemMapping = getRequestItemMapping(Set.of(request.getId()));
        return itemRequestMapper.toListAdvancedItemRequestDto(List.of(request), requestItemMapping)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Ошибка при мапинге RequestWithItemInfoDto"));
    }

    @Transactional(readOnly = true)
    private void getUserById(Long id) {
        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь по id = %s не найден!", id)));
    }

    @Transactional(readOnly = true)
    public Map<Long, List<Item>> getRequestItemMapping(Set<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds)
                .stream().collect(Collectors.groupingBy(Item::getRequestId));
    }
}
