package shareit.requests;

import shareit.item.model.Item;
import shareit.requests.dto.ItemRequestDto;
import shareit.requests.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemRequestDto.Item> responses = new ArrayList<>();
        for (Item item : itemRequest.getItems()) {
            responses.add(new ItemRequestDto.Item(item.getId(), item.getName(), item.getDescription(),
                    item.getAvailable(), item.getRequest().getId()));
        }
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), responses);
    }

    public static List<ItemRequestDto> toItemRequestDtos(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(toItemRequestDto(itemRequest));
        }
        return dtos;
    }
}
