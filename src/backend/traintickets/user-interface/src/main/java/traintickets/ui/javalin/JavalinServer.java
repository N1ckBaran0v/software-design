package traintickets.ui.javalin;

import io.javalin.Javalin;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.ui.api.Server;

import java.util.Objects;

public final class JavalinServer implements Server {
    private final Javalin javalin;
    private final UniLogger logger;

    public JavalinServer(Javalin javalin, UniLoggerFactory loggerFactory) {
        this.javalin = Objects.requireNonNull(javalin);
        this.logger = loggerFactory.getLogger(JavalinServer.class);
    }

    @Override
    public void start() {
        javalin.start();
        logger.info("Javalin server started at %s:%d", javalin.jettyServer().server().getURI(), javalin.port());
    }

    @Override
    public void stop() {
        javalin.stop();
        logger.info("Javalin server stopped");
    }
}
