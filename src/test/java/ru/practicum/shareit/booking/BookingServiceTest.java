package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exeptions.BookingUnsupportedTypeException;
import ru.practicum.shareit.exeptions.EntityIsAlreadyExistsException;
import ru.practicum.shareit.exeptions.ItemIsNotAvailableException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.rmi.AccessException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    @Rollback
    void getOwnerBookings() throws EntityIsAlreadyExistsException, AccessException, ItemIsNotAvailableException, BookingUnsupportedTypeException {
        User userOwnerOfItem = userService.addUser(new UserDto("name", "email@mail.ru"));
        User userBooker = userService.addUser(new UserDto("booker", "booker@mail.com"));

        ItemDto itemDto = itemService.addItem(new ItemDto(0L,
                        "name",
                        "description",
                        true,
                        0L,
                        null,
                        null,
                        null,
                        new ItemDto.User(1L, "name")),
                userOwnerOfItem.getId());

        BookingDto bookingDto = bookingService.addBooking(
                new BookingRequest(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1),
                        itemDto.getId()
                ),
                userBooker.getId()
        );

        List<BookingDto> bookings = bookingService.getBookingsByOwner(userOwnerOfItem.getId(), "ALL");

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookings.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookingDto.getBooker().getId()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
    }
}
