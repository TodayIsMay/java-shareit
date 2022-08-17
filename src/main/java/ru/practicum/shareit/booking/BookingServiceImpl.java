package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.BookingUnsupportedTypeException;
import ru.practicum.shareit.exeptions.ItemIsNotAvailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

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
        Booking booking = new Booking();
        isValidRequest(bookingRequest);
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
        isValidBooking(booking, userId);
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public List<BookingDto> getAllForUser(long userId, String stringState)
            throws NoSuchElementException, IllegalArgumentException, BookingUnsupportedTypeException {
        User booker = userService.getUserById(userId);

        State state;

        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new BookingUnsupportedTypeException("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (state) {
            case PAST:
                return BookingMapper.toBookingDtos(bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(booker,
                        LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toBookingDtos(bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(booker,
                        LocalDateTime.now()));
            case WAITING:
                return BookingMapper.toBookingDtos(bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(booker,
                        Status.WAITING));
            case REJECTED:
                return BookingMapper.toBookingDtos(bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(booker,
                        Status.REJECTED));
            case CURRENT:
                return BookingMapper.toBookingDtos(bookingRepository.getByCurrentStatus(booker.getId()));
            default:
                return BookingMapper.toBookingDtos(bookingRepository.findAllByBookerOrderByStartDesc(booker));

        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String stringState) throws BookingUnsupportedTypeException {
        User owner = userService.getUserById(userId);

        State state;

        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new BookingUnsupportedTypeException("Unknown state: UNSUPPORTED_STATUS");
        }

        List<Booking> bookings = bookingRepository.findForOwner(owner.getId());

        if (bookings.isEmpty()) {
            throw new NoSuchElementException("У пользователя с таким ID нет ни одной брони.");
        }

        switch (state) {
            case PAST:
                bookings = bookingRepository.findForOwnerPast(owner.getId());
                break;
            case FUTURE:
                bookings = bookingRepository.findForOwnerFuture(owner.getId());
                break;
            case WAITING:
                bookings = bookingRepository.findForOwnerByStatus(owner.getId(), Status.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findForOwnerByStatus(owner.getId(), Status.REJECTED.toString());
                break;
            case CURRENT:
                bookings = bookingRepository.findForOwnerCurrent(owner.getId());
                break;
        }

        return BookingMapper.toBookingDtos(bookings);
    }

    @Override
    public List<Booking> checkBookingsForItem(long itemId, long userId) {
        User booker = userService.getUserById(userId);
        return bookingRepository.findAllByBookerAndItemIdAndStatus(booker, itemId, Status.APPROVED);
    }

    private void isValidRequest(BookingRequest bookingRequest) throws IllegalArgumentException {
        if (bookingRequest.getStart().isAfter(bookingRequest.getEnd())) {
            throw new IllegalArgumentException("Время начала бронирования должно быть раньше времени окончания!");
        }
        if (bookingRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Время окончания бронирования не может быть в прошлом!");
        }
        if (bookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Время начала бронирования не может быть в прошлом!");
        }
    }

    private void isValidBooking(Booking booking, long userId)
            throws AccessDeniedException, IllegalArgumentException {
        if (booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException("Бронирование может быть подтверждено только владельцем вещи!");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new IllegalArgumentException("Бронирование уже подтверждено!");
        }
    }
}
