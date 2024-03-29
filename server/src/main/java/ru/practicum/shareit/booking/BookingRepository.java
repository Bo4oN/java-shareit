package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(int bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int bookerId, Status status, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(int bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(int bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int bookerId,
                                                                              LocalDateTime dateTime,
                                                                              LocalDateTime dateTime2,
                                                                              Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllBookingForAllUserItems(int userId, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsForAllUserItems(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and ?2 > b.end " +
            "order by b.start desc")
    List<Booking> findPastBookingForAllUserItems(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and ?2 < b.start " +
            "order by b.start desc")
    List<Booking> findFutureBookingForAllUserItems(int userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findStatusBookingForAllUserItems(int userId, Status status, Pageable pageable);

    List<Booking> findAllByItemIdOrderByStart(int itemId);

    List<Booking> findByItemIdAndBookerIdAndEndIsBefore(int itemId, int bookerId, LocalDateTime dateTime);
}
