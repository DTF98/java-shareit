package ru.practicum.shareit.request.dto;

import lombok.*;
import java.time.LocalDateTime;

@Value
@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemRequestDto {
    @EqualsAndHashCode.Include
    Long id;

    String description;

    LocalDateTime created;
}
