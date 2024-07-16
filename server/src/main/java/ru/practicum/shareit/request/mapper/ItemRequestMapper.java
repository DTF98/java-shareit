package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AdvancedRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toModel(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto toDto(ItemRequest itemRequest);

    AdvancedRequestItemDto toAdvancedItemRequestDto(ItemRequest requests, List<ItemForRequestDto> items);

    ItemForRequestDto toItemForRequestDto(Item item);

    default List<AdvancedRequestItemDto> toListAdvancedItemRequestDto(List<ItemRequest> requests, Map<Long, List<Item>> items) {
        LinkedList<AdvancedRequestItemDto> result = new LinkedList<>();
        for (ItemRequest request : requests) {
            List<ItemForRequestDto> itemRequestDtos;
            if (items.containsKey(request.getId())) {
                itemRequestDtos = items.get(request.getId()).stream().map(this::toItemForRequestDto)
                        .collect(Collectors.toList());
            } else {
                itemRequestDtos = List.of();
            }
            result.add(toAdvancedItemRequestDto(request, itemRequestDtos));
        }
        return result;
    }
}
