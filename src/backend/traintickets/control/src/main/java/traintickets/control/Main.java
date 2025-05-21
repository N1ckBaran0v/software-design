package traintickets.control;

import traintickets.control.configuration.ApplicationContextCreator;
import traintickets.ui.api.Server;

public final class Main {
    public static void main(String[] args) {
        var ctx = ApplicationContextCreator.create();
        var server = ctx.getInstance(Server.class);
        server.start();
    }
}
