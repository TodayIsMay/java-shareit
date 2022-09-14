package shareit.booking;

import shareit.booking.dto.BookingDto;
import shareit.booking.model.Booking;
import shareit.exeptions.BookingUnsupportedTypeException;
import shareit.exeptions.ItemIsNotAvailableException;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.util.List;
import java.util.NoSuchElementException;

public interface BookingService {
    Booking getBookingById(long bookingId, long userId) throws AccessDeniedException;

    BookingDto addBooking(BookingRequest bookingRequest, long userId)
            throws NoSuchElementException, ItemIsNotAvailableException, IllegalArgumentException, AccessException;

    Booking setApproved(long bookingId, long userId, boolean isApproved) throws AccessDeniedException;

    List<BookingDto> getAllForUser(long userId, String state, Integer from, Integer size)
            throws NoSuchElementException, BookingUnsupportedTypeException, IllegalArgumentException;

    List<BookingDto> getBookingsByOwner(Long userId, String state) throws BookingUnsupportedTypeException;

    List<Booking> checkBookingsForItem(long itemId, long userId);
}
