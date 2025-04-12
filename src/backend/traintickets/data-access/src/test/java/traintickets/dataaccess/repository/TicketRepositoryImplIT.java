package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.PaymentException;
import traintickets.businesslogic.exception.PlaceAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.payment.PaymentManager;
import traintickets.businesslogic.repository.TicketRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketRepositoryImplIT extends PostgresIT {
    private TicketRepository ticketRepository;

    @Mock
    private PaymentManager paymentManager;

    @Mock
    private PaymentData paymentData;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        ticketRepository = new TicketRepositoryImpl(jdbcTemplate, paymentManager);
    }

    @Override
    protected void insertData() {
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "insert into trains (train_class) values ('фирменный'); " +
                            "insert into races (train_id, finished) values " +
                            "(1, true), (1, false); " +
                            "insert into schedule (race_id, station_name, arrival, departure, multiplier) values " +
                            "(1, 'first', null, '2025-04-01 10:10:00+03', 0), " +
                            "(1, 'second', '2025-04-01 11:40:00+03', null, 5), " +
                            "(2, 'first', null, '2025-04-01 11:00:00+03', 0), " +
                            "(2, 'second', '2025-04-01 12:00:00+03', null, 5); " +
                            "insert into railcars (railcar_model, railcar_type) values (1, 'сидячий'); " +
                            "insert into railcarsintrains (train_id, railcar_id) values (1, 1); " +
                            "insert into places (railcar_id, place_number, description, purpose, place_cost) " +
                            "values (1, 1, '', 'universal', 100), (1, 2, '', 'universal', 100); " +
                            "insert into users (user_name, pass_word, real_name, user_role, is_active) values " +
                            "('first_user', 'qwerty123', 'Иванов Иван Иванович', 'user', true), " +
                            "('second_user', 'qwerty123', 'Петров Петр Петрович', 'user', true); " +
                            "insert into tickets (user_id, passenger, race_id, railcar, place_id, departure, destination, ticket_cost) values " +
                            "(1, 'adult', 1, 1, 1, 1, 2, 500), (2, 'adult', 1, 1, 2, 1, 2, 500), (1, 'adult', 2, 1, 1, 3, 4, 500);"
            )) {
                statement.execute();
            }
        });
    }

    @Test
    void addTickets_positive_added() {
        var ticket = new Ticket(null, new UserId("2"), "adult", new RaceId("2"), 1,
                new Place(new PlaceId("2"), 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        ticketRepository.addTickets(roleName, List.of(ticket), paymentData);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE id = 4;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(ticket.owner().id(), String.valueOf(resultSet.getLong("user_id")));
                    assertEquals(ticket.passenger(), resultSet.getString("passenger"));
                    assertEquals(ticket.race().id(), String.valueOf(resultSet.getLong("race_id")));
                    assertEquals(ticket.railcar(), resultSet.getInt("railcar"));
                    assertEquals(ticket.place().id().id(), String.valueOf(resultSet.getLong("place_id")));
                    assertEquals(ticket.start().id().id(), String.valueOf(resultSet.getLong("departure")));
                    assertEquals(ticket.end().id().id(), String.valueOf(resultSet.getLong("destination")));
                    assertEquals(ticket.cost(), resultSet.getBigDecimal("ticket_cost"));
                    assertFalse(resultSet.next());
                }
            }
        });
        verify(paymentManager).pay(paymentData);
    }

    @Test
    void addTickets_negative_finished() {
        var ticket = new Ticket(null, new UserId("2"), "adult", new RaceId("1"), 1,
                new Place(new PlaceId("1"), 1, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        assertThrows(InvalidEntityException.class,
                () -> ticketRepository.addTickets(roleName, List.of(ticket), paymentData));
        verify(paymentManager, never()).pay(any());
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE id = 4;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void addTickets_negative_reserved() {
        var ticket = new Ticket(null, new UserId("2"), "adult", new RaceId("2"), 1,
                new Place(new PlaceId("1"), 1, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        assertThrows(PlaceAlreadyReservedException.class,
                () -> ticketRepository.addTickets(roleName, List.of(ticket), paymentData));
        verify(paymentManager, never()).pay(any());
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE id = 4;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void addTickets_negative_paymentRejected() {
        var ticket = new Ticket(null, new UserId("2"), "adult", new RaceId("2"), 1,
                new Place(new PlaceId("2"), 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        willThrow(PaymentException.class).given(paymentManager).pay(paymentData);
        assertThrows(PaymentException.class,
                () -> ticketRepository.addTickets(roleName, List.of(ticket), paymentData));
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE id = 4;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void getTicketsByUser_positive_got() {
        var userId = new UserId("1");
        var place = new Place(new PlaceId("1"), 1, "", "universal", BigDecimal.valueOf(100));
        var ticket1 = new Ticket(new TicketId("1"), userId, "adult", new RaceId("1"), 1, place,
                new Schedule(new ScheduleId("1"), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                new Schedule(new ScheduleId("2"), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                BigDecimal.valueOf(500));
        var ticket2 = new Ticket(new TicketId("3"), userId, "adult", new RaceId("2"), 1, place,
                new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        var result = ticketRepository.getTicketsByUser(roleName, userId);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(ticket1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(ticket2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getTicketsByUser_positive_empty() {
        var result = ticketRepository.getTicketsByUser(roleName, new UserId("3"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void getTicketsByRace_positive_got() {
        var raceId = new RaceId("1");
        var ticket1 = new Ticket(new TicketId("1"), new UserId("1"), "adult", raceId, 1,
                new Place(new PlaceId("1"), 1, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(new ScheduleId("1"), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                new Schedule(new ScheduleId("2"), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                BigDecimal.valueOf(500));
        var ticket2 = new Ticket(new TicketId("2"), new UserId("2"), "adult", raceId, 1,
                new Place(new PlaceId("2"), 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(new ScheduleId("1"), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                new Schedule(new ScheduleId("2"), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                BigDecimal.valueOf(500));
        var result = ticketRepository.getTicketsByRace(roleName, raceId);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(ticket1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(ticket2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getTicketsByRace_positive_empty() {
        var result = ticketRepository.getTicketsByRace(roleName, new RaceId("3"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }
}