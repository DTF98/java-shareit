package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    @EqualsAndHashCode.Include
    @Null
    Long id;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 2, max = 30, message = "Длина имени должно быть в диапазоне 2-30 символов.")
    String name;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 1)
    @Email
    String email;
}
