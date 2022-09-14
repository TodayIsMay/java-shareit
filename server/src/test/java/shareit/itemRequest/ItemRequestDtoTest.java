package shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import shareit.requests.dto.ItemRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(LocalDate.of(2002, 7, 3),
                LocalTime.of(12, 0));

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "description",
                created,
                new ArrayList<>()
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2002-07-03T12:00:00");
    }
}
