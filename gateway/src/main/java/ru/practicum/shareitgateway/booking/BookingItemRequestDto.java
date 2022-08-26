package ru.practicum.shareitgateway.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingItemRequestDto {
    private long id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
