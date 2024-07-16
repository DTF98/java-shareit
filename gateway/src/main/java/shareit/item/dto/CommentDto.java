package shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CommentDto {
    @EqualsAndHashCode.Include
    @Null
    Long id;

    @JsonIgnore
    @Null
    Long itemId;

    @NotNull
    @Size(min = 1, max = 255, message = "Текст комментария должен быть в диапазоне 1-255 символов.")
    String text;

    @Null
    String authorName;

    @Null
    LocalDateTime created;
}