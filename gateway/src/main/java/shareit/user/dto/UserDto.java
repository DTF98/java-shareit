package shareit.user.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
    @EqualsAndHashCode.Include
    @Null
    Long id;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 2, max = 30, message = "Длина имени должно быть в диапазоне 2-30 символов.")
    String name;

    @NotNull(groups = ValidationGroup.Create.class)
    @Size(min = 1)
    @Email(message = "Неверный формат почты", regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    String email;
}
