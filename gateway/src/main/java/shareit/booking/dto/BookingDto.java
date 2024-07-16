package shareit.booking.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import shareit.booking.model.BookingStatus;
import shareit.item.dto.ItemDto;
import shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookingDto {
    @EqualsAndHashCode.Include
    Long id;

    ItemDto item;

    UserDto booker;

    BookingStatus status;

    LocalDateTime start;

    LocalDateTime end;
}
