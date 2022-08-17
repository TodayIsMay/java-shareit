package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerAndItemIdAndStatus(User booker, long itemId, Status status);

    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    @Query(nativeQuery = true, value = "WITH temp AS (SELECT id, start_date, end_date, item_id, booker_id, status, " +
            "ROW_NUMBER() OVER (ORDER BY start_date) AS rownum FROM bookings) " +
            "SELECT * FROM temp WHERE booker_id = ? AND rownum = ?" +
            "LIMIT ?")
    List<Booking> findAllByBookerWithBorders(long bookerId, int from, int size);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime now);

    @Query(nativeQuery = true, value = "WITH temp AS (SELECT id, start_date, end_date, item_id, booker_id, status, " +
            "ROW_NUMBER() OVER (ORDER BY start_date) AS rownum FROM bookings) " +
            "SELECT * FROM temp WHERE end_date > NOW() AND booker_id = ? AND rownum = ?" +
            "LIMIT ?")
    List<Booking> findAllByBookerPastWithBorders(long bookerId, int from, int size);

    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime now);

    @Query(nativeQuery = true, value = "WITH temp AS (SELECT id, start_date, end_date, item_id, booker_id, status, " +
            "ROW_NUMBER() OVER (ORDER BY start_date) AS rownum FROM bookings) " +
            "SELECT * FROM temp WHERE start_date > NOW() AND booker_id = ? AND rownum = ?" +
            "LIMIT ?")
    List<Booking> findAllByBookerFutureWithBorders(long bookerId, int from, int size);

    List<Booking> findAllByBookerAndStatusIsOrderByStartDesc(User booker, Status status);

    @Query(nativeQuery = true, value = "WITH temp AS (SELECT id, start_date, end_date, item_id, booker_id, status, " +
            "ROW_NUMBER() OVER (ORDER BY start_date) AS rownum FROM bookings) " +
            "SELECT * FROM temp WHERE booker_id = ? AND status = ? AND rownum = ?" +
            "LIMIT ?")
    List<Booking> findAllByBookerAndStatusWithBorders(long bookerId, Status status, int from, int size);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings WHERE booker_id = ? " +
            "AND start_date < now() AND item_id = ?")
    List<Booking> findPassedBookings(Long userId, Long itemId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings as b " +
            "LEFT JOIN items AS i ON b.item_id = i.id " +
            "WHERE i.owner_id = ? " +
            "ORDER BY b.start_date DESC")
    List<Booking> findForOwner(Long ownerId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings as b " +
            "LEFT JOIN items AS i ON b.item_id = i.id " +
            "WHERE i.owner_id = ? and now() between start_date and end_date " +
            "ORDER BY b.start_date DESC")
    List<Booking> findForOwnerCurrent(Long ownerId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings as b " +
            "LEFT JOIN items AS i ON b.item_id = i.id " +
            "WHERE i.owner_id = ? AND b.end_date < now() " +
            "ORDER BY b.start_date DESC")
    List<Booking> findForOwnerPast(Long ownerId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings as b " +
            "LEFT JOIN items AS i ON b.item_id = i.id " +
            "WHERE i.owner_id = ? AND b.start_date > now()" +
            "ORDER BY b.start_date DESC")
    List<Booking> findForOwnerFuture(Long ownerId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings as b " +
            "LEFT JOIN items AS i ON b.item_id = i.id " +
            "WHERE i.owner_id = ? AND b.status = ? " +
            "ORDER BY b.start_date DESC")
    List<Booking> findForOwnerByStatus(Long ownerId, String status);

    @Query(value = "select * " +
            "from bookings " +
            "where booker_id = ? and now() between start_date and end_date", nativeQuery = true)
    List<Booking> getByCurrentStatus(Long bookerId);

    @Query(nativeQuery = true, value = "WITH temp AS (SELECT id, start_date, end_date, item_id, booker_id, status, " +
            "ROW_NUMBER() OVER (ORDER BY start_date) AS rownum FROM bookings) " +
            "SELECT * FROM temp WHERE now() BETWEEN start_date AND end_date AND booker_id = ? AND rownum = ?" +
            "LIMIT ?")
    List<Booking> findAllByBookerCurrentWithBorders(long bookerId, int from, int size);

    @Query(value = "select * from bookings as b\n" +
            "join items i on i.id = b.item_id\n" +
            "where i.owner_id = ? and i.id = ? and b.end_date < now() and b.status like 'APPROVED'\n" +
            "order by b.end_date desc\n" +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(Long ownerId, Long itemId);

    @Query(nativeQuery = true, value = "select * from bookings as b\n" +
            "                  join items i on i.id = b.item_id\n" +
            "where i.owner_id = ? and i.id = ? and b.start_date > now() and b.status like 'APPROVED'\n" +
            "order by b.end_date asc\n" +
            "limit 1")
    Booking findNextBooking(Long ownerId, Long itemId);
}
