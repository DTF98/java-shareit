package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
public class ItemDto {
    @Null(groups = ValidationGroup.Create.class)
    private Long id;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 2, max = 30)
    private String name;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 2, max = 200)
    private String description;

    @NotNull(groups = ValidationGroup.Create.class)
    private Boolean available;
}
