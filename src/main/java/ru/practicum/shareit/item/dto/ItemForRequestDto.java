package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemForRequestDto {
    @EqualsAndHashCode.Include
    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;
}
