package traintickets.ui.group;

import traintickets.ui.controller.TicketController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class TicketGroup extends AbstractEndpointGroup {
    private final TicketController ticketController;

    public TicketGroup(TicketController ticketController) {
        super("/api/tickets");
        this.ticketController = Objects.requireNonNull(ticketController);
    }

    @Override
    public void addEndpoints() {
        post(ticketController::addTickets);
        get(ticketController::getTickets);
    }
}
