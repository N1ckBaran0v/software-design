package traintickets.businesslogic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private PaymentData paymentData;

    private List<Ticket> tickets;

    @BeforeEach
    void setUp() {
        var owner = new UserId(1);
        var race = new RaceId(228);
        var start = new Schedule(new ScheduleId(1), "station1", null, Timestamp.valueOf("2025-07-11 13:34:00"), 2);
        var end = new Schedule(new ScheduleId(2), "station2", Timestamp.valueOf("2025-07-20 12:45:00"), null, 7);
        var cost = BigDecimal.valueOf((7 - 2) * 10);
        var ticket1 = new Ticket(new TicketId(1), owner, "adult", race, 3,
                new Place(new PlaceId(1), 1, "", "any_human", BigDecimal.TEN), start, end, cost);
        var ticket2 = new Ticket(new TicketId(2), owner, "child", race, 3,
                new Place(new PlaceId(2), 2, "", "invalids", BigDecimal.TEN), start, end, cost);
        tickets = List.of(ticket1, ticket2);
    }

    @Test
    void buyTickets_positive_saved() {
        var userInfo = new UserInfo(new UserId(1), "carrier_role");
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        ticketService.buyTickets(sessionId, tickets, paymentData);
        verify(ticketRepository).addTickets(userInfo.role(), tickets, paymentData);
    }

    @Test
    void getTickets_positive_got() {
        var user = new UserId(1);
        var userInfo = new UserInfo(new UserId(1), "carrier_role");
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        given(ticketRepository.getTicketsByUser(userInfo.role(), user)).willReturn(tickets);
        var result = ticketService.getTickets(sessionId, user);
        assertNotNull(result);
        assertEquals(tickets.size(), result.size());
        for (var i = 0; i < result.size(); ++i) {
            assertEquals(tickets.get(i), result.get(i));
        }
    }

    @Test
    void getTickets_positive_empty() {
        var user = new UserId(1);
        var userInfo = new UserInfo(new UserId(1), "carrier_role");
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        given(ticketRepository.getTicketsByUser(userInfo.role(), user)).willReturn(List.of());
        var result = ticketService.getTickets(sessionId, user);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}