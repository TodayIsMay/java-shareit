package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.ItemIsNotAvailableException;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.util.List;
import java.util.NoSuchElementException;

public interface BookingService {
    Booking getBookingById(long bookingId, long userId) throws AccessDeniedException;

    Booking addBooking(BookingRequest bookingRequest, long userId)
            throws NoSuchElementException, ItemIsNotAvailableException, IllegalArgumentException, AccessException;

    Booking setApproved(long bookingId, long userId, boolean isApproved) throws AccessDeniedException;

    List<Booking> getAllForUser(long userId, State state) throws NoSuchElementException;

    List<Booking> getBookingsByOwner(Long userId, State state);

    List<Booking> checkBookingsForItem(long itemId, long userId);
}
