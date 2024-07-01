package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDto {
    @Null(groups = ValidationGroup.Create.class)
    Long id;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 2, max = 30)
    String name;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 2, max = 200)
    String description;

    @NotNull(groups = ValidationGroup.Create.class)
    Boolean available;

    Long requestId;
}
