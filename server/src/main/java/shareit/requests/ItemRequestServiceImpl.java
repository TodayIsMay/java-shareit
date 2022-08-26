package shareit.requests;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shareit.item.ItemRepository;
import shareit.item.model.Item;
import shareit.requests.dto.ItemRequestDto;
import shareit.requests.model.ItemRequest;
import shareit.user.UserService;
import shareit.user.model.User;
import shareit.utils.Validation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserService userService,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto postRequest(ItemRequestDto itemRequestDto, Long requesterId)
            throws NoSuchElementException, IllegalArgumentException {
        isValidRequest(itemRequestDto);
        User requester = userService.getUserById(requesterId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequests(long requesterId) throws NoSuchElementException {
        userService.getUserById(requesterId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId);
        for (ItemRequest request : requests) {
            List<Item> items = itemRepository.findAllByRequestId(request.getId());
            request.setItems(items);
        }
        return ItemRequestMapper.toItemRequestDtos(requests);
    }

    @Override
    public List<ItemRequestDto> getOthersRequests(long requesterId, Integer from, Integer size)
            throws NoSuchElementException {
        if (from == null | size == null) {
            return ItemRequestMapper.toItemRequestDtos(itemRequestRepository
                    .findOthersRequestsWithoutBorders(requesterId));
        }
        Validation.isValidBorders(from, size);
        userService.getUserById(requesterId);
        List<ItemRequest> requests = itemRequestRepository.findOthersRequests(from, size);
        return ItemRequestMapper.toItemRequestDtos(requests);
    }

    @Override
    public ItemRequestDto getItemRequest(long itemRequestId, long requesterId) throws NoSuchElementException {
        userService.getUserById(requesterId);
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(itemRequestId);
        if (optionalItemRequest.isEmpty()) {
            throw new NoSuchElementException("Запрос с таким ID не найден!");
        }
        return ItemRequestMapper.toItemRequestDto(optionalItemRequest.get());
    }

    @Override
    public ItemRequest getItemRequestById(long itemRequestId) throws NoSuchElementException {
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(itemRequestId);
        if (optionalItemRequest.isEmpty()) {
            throw new NoSuchElementException("Запрос с таким ID не найден!");
        }
        return optionalItemRequest.get();
    }

    @Override
    public void addResponse(Item item, long requestId) {
        ItemRequest request = getItemRequestById(requestId);
        List<Item> responses = request.getItems();
        responses.add(item);
        request.setItems(responses);
    }

    private void isValidRequest(ItemRequestDto itemRequestDto) throws IllegalArgumentException {
        if ((StringUtils.isEmpty(itemRequestDto.getDescription()))) {
            throw new IllegalArgumentException("Описание не может быть пустым!");
        }
    }
}
