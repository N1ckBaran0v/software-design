package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.TicketService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.payment.data.NoOpPaymentData;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Arrays;
import java.util.Objects;

public final class TicketController {
    private final TicketService ticketService;
    private final UniLogger logger;

    public TicketController(TicketService ticketService, UniLoggerFactory loggerFactory) {
        this.ticketService = Objects.requireNonNull(ticketService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(TicketController.class);
    }

    public void addTickets(Context ctx) {
        var tickets = Arrays.asList(ctx.bodyAsClass(Ticket[].class));
        logger.debug("tickets: %s", tickets);
        ticketService.buyTickets(ctx.cookie("sessionId"), tickets, new NoOpPaymentData());
        ctx.status(HttpStatus.CREATED);
        logger.debug("tickets added");
    }

    public void getTickets(Context ctx) {
        var userId = ctx.queryParam("userId");
        logger.debug("userId: %s", userId);
        if (userId == null) {
            throw new QueryParameterNotFoundException("userId");
        }
        ctx.json(ticketService.getTickets(ctx.cookie("sessionId"), new UserId(userId)));
        logger.debug("tickets got");
    }
}
