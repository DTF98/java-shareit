package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.dto.AdvancedBookingDto;

import java.util.List;

@Value
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdvancedItemDto {
    @EqualsAndHashCode.Include
    Long id;

    String name;

    String description;

    Boolean available;

    AdvancedBookingDto lastBooking;

    AdvancedBookingDto nextBooking;

    List<CommentDto> comments;
}
