package ru.practicum.shareit.item.dto;

import lombok.*;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDto {
    @EqualsAndHashCode.Include
    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;
}
