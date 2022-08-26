package shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import shareit.booking.dto.BookingDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(LocalDate.of(2002, 07, 03),
                LocalTime.of(12, 00));
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2002, 07, 03),
                LocalTime.of(13, 00));

        BookingDto bookingDto = new BookingDto(
                1L,
                start,
                end,
                Status.APPROVED,
                new BookingDto.User(
                        1L,
                        "name"
                ),
                new BookingDto.Item(
                        1L,
                        "name"
                )
        );


        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2002-07-03T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2002-07-03T13:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
    }
}
