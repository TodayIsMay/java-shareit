package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exeptions.ItemIsNotAvailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Booking getBookingById(long bookingId, long userId) throws AccessDeniedException {

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new NoSuchElementException("Бронирование не найдено!");
        }
        Booking booking = bookingOptional.get();
        boolean state = userId != (booking.getBooker().getId()) &&
                userId != (booking.getItem().getOwner().getId());

        if (state) {
            throw new AccessDeniedException("Пользователь с таким ID не является владельцем бронирования!");
        }

        return booking;
    }

    @Override
    public Booking addBooking(BookingRequest bookingRequest, long userId)
            throws NoSuchElementException, ItemIsNotAvailableException, IllegalArgumentException, AccessException {
        if (bookingRequest.getStart().isAfter(bookingRequest.getEnd())) {
            throw new IllegalArgumentException("Время начала бронирования должно быть раньше времени окончания!");
        }
        if (bookingRequest.getEnd().isBefore(LocalDateTime.now()) | bookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Время начала и/или окончания бронирования не может быть в прошлом!");
        }
        User user = userService.getUserById(userId);
        Status status = Status.WAITING;
        long itemId = bookingRequest.getItemId();
        Item item = itemRepository.findById(itemId).get();
        if (item.getOwner().getId() == userId) {
            throw new AccessException("Владелец вещи не может её забронировать сам у себя!");
        }
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Вещь уже занята!");
        }
        Booking booking = new Booking();
        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking setApproved(long bookingId, long userId, boolean isApproved) throws AccessDeniedException {
        Booking booking = getBookingById(bookingId, userId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException("Бронирование может быть подтверждено только владельцем вещи!");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new IllegalArgumentException("Бронирование уже подтверждено!");
        }
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public List<Booking> getAllForUser(long userId, State state) throws NoSuchElementException {
        User booker = userService.getUserById(userId);
        switch (state) {
            case PAST:
                bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(booker, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(booker, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(booker, Status.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(booker, Status.REJECTED);
            default:
                return bookingRepository.findAllByBookerOrderByStartDesc(booker);
        }
    }

    @Override
    public List<Booking> getBookingsByOwner(Long userId, State state) {
        User booker = userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.findForOwner(booker.getId());

        if (bookings.isEmpty()) {
            throw new NoSuchElementException("У пользователя с таким ID нет ни одной брони.");
        }
        return bookings;
    }

    @Override
    public List<Booking> checkBookingsForItem(long itemId, long userId) {
        User booker = userService.getUserById(userId);
        return bookingRepository.findAllByBookerAndItemIdAndStatus(booker, itemId, Status.APPROVED);
    }
}
