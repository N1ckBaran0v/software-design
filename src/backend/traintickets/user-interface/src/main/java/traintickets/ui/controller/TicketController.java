package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.TicketService;
import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.payment.data.NoOpPaymentData;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Arrays;
import java.util.Objects;

public final class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = Objects.requireNonNull(ticketService);
    }

    public void addTickets(Context ctx) {
        var tickets = Arrays.asList(ctx.bodyAsClass(Ticket[].class));
        ticketService.buyTickets(ctx.cookie("sessionId"), tickets, new NoOpPaymentData());
    }

    public void getTickets(Context ctx) {
        var userId = ctx.queryParam("userId");
        if (userId == null) {
            throw new QueryParameterNotFoundException("userId");
        }
        ctx.json(ticketService.getTickets(ctx.cookie("sessionId"), new UserId(userId)));
    }
}
