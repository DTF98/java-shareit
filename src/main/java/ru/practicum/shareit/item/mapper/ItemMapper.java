package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingForAdvancedItemDto;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toModel(ItemDto itemDto, Long ownerId);

    ItemDto toDto(Item item);

    List<ItemDto> collectionToDto(List<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    void updateModel(@MappingTarget Item item, ItemDto updaterItemDto);

    @Mapping(target = "id", source = "item.id")
    AdvancedItemDto toItemBookingInfoDto(Item item, List<CommentDto> comments,
                                         BookingForAdvancedItemDto lastBooking, BookingForAdvancedItemDto nextBooking);
}
