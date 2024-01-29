package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(scripts = {"classpath:data_test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:data_test_deleted.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;
    Pageable pageable = PageRequest.of(0, 10);

    @Test
    public void findAllBookingForAllUserItems() {
        List<Booking> list = repository.findAllBookingForAllUserItems(1, pageable);
        assertEquals(2, list.size());
    }

    @Test
    public void findAllByItemIdOrderByStart() {
        List<Booking> list = repository.findAllByItemIdOrderByStart(2);
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).getId());
    }
}
