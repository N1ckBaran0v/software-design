package traintickets.control;

import traintickets.control.configuration.ApplicationContextCreator;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.ui.api.Server;

public final class Main {
    public static void main(String[] args) {
        var ctx = ApplicationContextCreator.create();
        var server = ctx.getInstance(Server.class);
        var jdbcTemplate = ctx.getInstance(JdbcTemplate.class);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            jdbcTemplate.close();
        }));
        server.start();
    }
}
