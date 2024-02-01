package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResult;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResult> json;

    User user = new User(1, "name", "mail@ya.ru");
    Item item = new Item("title", "description", true, user);

    @Test
    public void testDeserialization() throws Exception {

        LocalDateTime start = LocalDateTime.of(2023, 11, 10, 00, 00);
        LocalDateTime end = LocalDateTime.of(2023, 12, 10, 00, 00);
        BookingResult bookingResult = new BookingResult(1, start, end, item, user, Status.WAITING);
        item.setId(1);

        JsonContent<BookingResult> result = json.write(bookingResult);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-11-10T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-10T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
    }
}
