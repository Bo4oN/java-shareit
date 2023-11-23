package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int bookerId, Status status);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(int bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(int bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int bookerId,
                                                                                 LocalDateTime dateTime,
                                                                                 LocalDateTime dateTime2);

    @Query("select b from Booking as b " +
            "where b.item in " +
            "(select i.id from Item as i " +
            "where i.ownerId = ?1) " +
            "order by b.start desc")
    List<Booking> findAllBookingForAllUserItems(int userId);

    @Query("select b from Booking as b " +
            "where b.item in " +
            "(select i.id from Item as i " +
            "where i.ownerId = ?1) " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsForAllUserItems(int userId, LocalDateTime dateTime);

    @Query("select b from Booking as b " +
            "where b.item in " +
            "(select i.id from Item as i " +
            "where i.ownerId = ?1) " +
            "and ?2 > b.end " +
            "order by b.start desc")
    List<Booking> findPastBookingForAllUserItems(int userId, LocalDateTime dateTime);

    @Query("select b from Booking as b " +
            "where b.item in " +
            "(select i.id from Item as i " +
            "where i.ownerId = ?1) " +
            "and ?2 < b.start " +
            "order by b.start desc")
    List<Booking> findFutureBookingForAllUserItems(int userId, LocalDateTime dateTime);

    @Query("select b from Booking as b " +
            "where b.item in " +
            "(select i.id from Item as i " +
            "where i.ownerId = ?1) " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findStatusBookingForAllUserItems(int userId, Status status);

    List<Booking> findAllByItemIdOrderByStart(int itemId);

    List<Booking> findByItemIdAndBookerIdAndEndIsBefore(int itemId, int bookerId, LocalDateTime dateTime);
}
