package traintickets.ui.javalin;

import io.javalin.Javalin;
import traintickets.ui.api.Server;

import java.util.Objects;

public final class JavalinServer implements Server {
    private final Javalin javalin;

    public JavalinServer(Javalin javalin) {
        this.javalin = Objects.requireNonNull(javalin);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
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
