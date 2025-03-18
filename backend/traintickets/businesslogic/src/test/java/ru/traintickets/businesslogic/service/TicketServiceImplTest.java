package ru.traintickets.businesslogic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.traintickets.businesslogic.model.*;
import ru.traintickets.businesslogic.repository.TicketRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private List<Ticket> tickets;

    @BeforeEach
    void setUp() {
        var owner = new UserId("random_username");
        var race = new RaceId(228);
        var start = new Schedule("station1", null, Timestamp.valueOf("2025-07-11 13:34:00"), 2);
        var end = new Schedule("station2", Timestamp.valueOf("2025-07-20 12:45:00"), null, 7);
        var cost = BigDecimal.valueOf((7 - 2) * 10);
        var ticket1 = new Ticket(owner, race, 3, new Place(1, null, "universal", BigDecimal.TEN), start, end, cost);
        var ticket2 = new Ticket(owner, race, 3, new Place(2, null, "invalids", BigDecimal.TEN), start, end, cost);
        tickets = List.of(ticket1, ticket2);
    }

    @Test
    void buyTickets_positive_saved() {
        ticketService.buyTickets(tickets);
        verify(ticketRepository).addTickets(tickets);
    }

    @Test
    void getTickets_positive_got() {
        var user = new UserId("random_username");
        given(ticketRepository.getTicketsByUser(user)).willReturn(tickets);
        var result = ticketService.getTickets(user);
        assertNotNull(result);
        assertEquals(tickets.size(), result.size());
        for (var i = 0; i < result.size(); ++i) {
            assertEquals(tickets.get(i), result.get(i));
        }
    }

    @Test
    void getTickets_positive_empty() {
        var user = new UserId("random_username");
        given(ticketRepository.getTicketsByUser(user)).willReturn(List.of());
        var result = ticketService.getTickets(user);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}