package traintickets.ui.javalin;

import io.javalin.Javalin;
import traintickets.ui.api.RestServer;

import java.util.Objects;

public final class JavalinServer implements RestServer {
    private final Javalin javalin;

    public JavalinServer(Javalin javalin) {
        this.javalin = Objects.requireNonNull(javalin);
    }

    @Override
    public void start() {
        javalin.start();
    }

    @Override
    public void stop() {
        javalin.stop();
    }
}
