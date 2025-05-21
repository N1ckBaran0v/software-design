package traintickets.ui.group;

import traintickets.ui.controller.TicketController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class TicketGroup extends AbstractEndpointGroup {
    private final TicketController ticketController;
    private final SecurityConfiguration securityConfiguration;

    public TicketGroup(TicketController ticketController, SecurityConfiguration securityConfiguration) {
        super("/tickets");
        this.ticketController = Objects.requireNonNull(ticketController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before(securityConfiguration::forUser);
        post(ticketController::addTickets);
        get(ticketController::getTickets);
    }
}
