package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.AdvancedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItemModel(ItemDto itemDto, Long ownerId);

    ItemDto toItemDto(Item item);

    List<ItemDto> toListItemDto(List<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    void updateModel(@MappingTarget Item item, ItemDto updaterItemDto);

    @Mapping(target = "itemId", source = "booking.item.id")
    @Mapping(target = "bookerId", source = "booking.booker.id")
    AdvancedBookingDto toAdvancedBookingDto(Booking booking);

    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "commentDto.id")
    Comment toCommentModel(CommentDto commentDto, User author, Long itemId);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);

    default List<AdvancedItemDto> toExtendInfoDto(List<Item> items, Map<Long, List<Comment>> commentMapping,
                                                        Map<Long, List<Booking>> lastBookingMapping, Map<Long, List<Booking>> nextBookingMapping) {
        LinkedList<AdvancedItemDto> result = new LinkedList<>();
        for (Item item : items) {
            AdvancedItemDto.AdvancedItemDtoBuilder itemInfo = AdvancedItemDto.builder();
            itemInfo.id(item.getId());
            itemInfo.name(item.getName());
            itemInfo.description(item.getDescription());
            itemInfo.available(item.getAvailable());
            AdvancedBookingDto lastBooking = null;
            if (lastBookingMapping != null && lastBookingMapping.containsKey(item.getId())) {
                Optional<Booking> booking = lastBookingMapping.get(item.getId()).stream().findFirst();
                if (booking.isPresent()) {
                    lastBooking = toAdvancedBookingDto(booking.get());
                }
            }
            itemInfo.lastBooking(lastBooking);

            AdvancedBookingDto nextBooking = null;
            if (nextBookingMapping != null && nextBookingMapping.containsKey(item.getId())) {
                Optional<Booking> booking = nextBookingMapping.get(item.getId()).stream().findFirst();
                if (booking.isPresent()) {
                    nextBooking = toAdvancedBookingDto(booking.get());
                }
            }
            itemInfo.nextBooking(nextBooking);

            List<CommentDto> commentList = new ArrayList<>();
            if (commentMapping != null && commentMapping.containsKey(item.getId())) {
                commentList = commentMapping.get(item.getId()).stream().map(this::toCommentDto).collect(Collectors.toList());
            }
            itemInfo.comments(commentList);
            result.add(itemInfo.build());
        }
        return result;
    }
}
