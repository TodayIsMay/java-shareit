package shareit.item;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shareit.booking.BookingRepository;
import shareit.booking.BookingService;
import shareit.booking.model.Booking;
import shareit.item.dto.CommentDto;
import shareit.item.dto.ItemDto;
import shareit.item.model.Comment;
import shareit.item.model.Item;
import shareit.requests.ItemRequestService;
import shareit.user.UserService;
import shareit.user.model.User;
import shareit.utils.Validation;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, BookingRepository bookingRepository,
                           BookingService bookingService, CommentRepository commentRepository,
                           ItemRequestService itemRequestService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
        this.itemRequestService = itemRequestService;
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> result = new ArrayList<>();
        for (Item item : itemRepository.findAll()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                if (item.getAvailable()) {
                    result.add(ItemMapper.toItemDto(item));
                }
            } else if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                if (item.getAvailable()) {
                    result.add(ItemMapper.toItemDto(item));
                }
            }
        }
        if (from == null | size == null) {
            return result;
        }
        List<ItemDto> borderedList = new ArrayList<>();
        for (int i = from; i <= size; i++) {
            borderedList.add(result.get(i));
        }
        return borderedList;
    }

    @Override
    public ItemDto get(long itemId, long userId) throws NoSuchElementException {
        Item item = getItemById(itemId);
        return setLastAndNextBooking(ItemMapper.toItemDto(item), userId);
    }

    @Override
    public List<ItemDto> getAll(long userId, Integer from, Integer size)
            throws NoSuchElementException, IllegalArgumentException {
        List<Item> items;
        List<ItemDto> itemDtos;
        User owner = userService.getUserById(userId);
        if (from == null | size == null) {
            items = itemRepository.findAllByOwnerOrderById(owner);
            itemDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
            return setLastAndNextBookingForList(itemDtos, userId);
        }
        Validation.isValidBorders(from, size);
        items = itemRepository.findAllByUserWithBorders(userId, from, size);
        itemDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return setLastAndNextBookingForList(itemDtos, userId);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) throws NoSuchElementException, IllegalArgumentException {
        User owner = userService.getUserById(userId);
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);
        item.setAvailable(true);
        if (itemDto.getRequestId() != 0) {
            itemRequestService.addResponse(item, itemDto.getRequestId());
            item.setRequest(itemRequestService.getItemRequestById(itemDto.getRequestId()));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Item editItem(long itemId, long userId, ItemDto itemDto) throws AccessDeniedException, NoSuchElementException {
        User user = userService.getUserById(userId);
        if (getItemById(itemId).getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("Редактировать предмет может только его владелец!");
        }
        Item item = getItemById(itemId);
        item.setName(itemDto.getName() == null ? item.getName() : itemDto.getName());
        item.setDescription(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable());

        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(long itemId) throws NoSuchElementException {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new NoSuchElementException("Элемент не найден!");
        }
        return itemOptional.get();
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto comment, Long userId)
            throws NoSuchElementException, IllegalArgumentException {
        Comment resultComment = new Comment();
        isValidComment(comment);
        Item item = getItemById(itemId);

        User user = userService.getUserById(userId);
        var bookings = bookingService.checkBookingsForItem(itemId, userId);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("У пользователя нет ни одной брони на данную вещь");
        }

        var passedBookings = bookingRepository.findPassedBookings(userId, itemId);
        if (passedBookings.isEmpty()) {
            throw new IllegalArgumentException("Нельзя оставлять комментарии к будущим бронированиям!");
        }

        resultComment.setItem(item);
        resultComment.setAuthor(user);
        resultComment.setText(comment.getText());
        resultComment.setCreated(LocalDateTime.now());
        commentRepository.save(resultComment);
        return CommentMapper.toDto(resultComment);
    }

    private ItemDto setLastAndNextBooking(ItemDto itemDto, Long userId) {
        Booking lastBooking;
        Booking nextBooking;

        if (!itemDto.getOwner().getId().equals(userId)) {
            nextBooking = null;
            lastBooking = null;
        } else {
            lastBooking = bookingRepository.findLastBooking(itemDto.getOwner().getId(), itemDto.getId());
            nextBooking = bookingRepository.findNextBooking(itemDto.getOwner().getId(), itemDto.getId());
        }

        itemDto.setLastBooking(lastBooking == null ? null :
                new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
        itemDto.setNextBooking(nextBooking == null ? null :
                new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));

        return itemDto;
    }

    private List<ItemDto> setLastAndNextBookingForList(List<ItemDto> items, Long userId) {
        return items.stream()
                .map(dto -> setLastAndNextBooking(dto, userId))
                .collect(Collectors.toList());
    }

    private void isValidComment(CommentDto comment) {
        if (StringUtils.isEmpty(comment.getText())) {
            throw new IllegalArgumentException("Комментарий не должен быть пустым");
        }
    }
}