package shareit.requests;

import shareit.item.model.Item;
import shareit.requests.dto.ItemRequestDto;
import shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.NoSuchElementException;

public interface ItemRequestService {
    ItemRequestDto postRequest(ItemRequestDto itemRequestDto, Long requesterId)
            throws NoSuchElementException, IllegalArgumentException;

    /**
     * Получить список своих запросов
     *
     * @return список запросов по id пользователя
     */
    List<ItemRequestDto> getRequests(long requesterId) throws NoSuchElementException;

    List<ItemRequestDto> getOthersRequests(long requesterId, Integer from, Integer size) throws NoSuchElementException;

    ItemRequestDto getItemRequest(long itemRequestId, long requesterId) throws NoSuchElementException;

    ItemRequest getItemRequestById(long itemRequestId) throws NoSuchElementException;

    void addResponse(Item item, long requestId);
}
