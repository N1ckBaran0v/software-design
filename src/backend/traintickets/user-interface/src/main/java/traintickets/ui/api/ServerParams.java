package traintickets.ui.api;

import java.util.Objects;

public record ServerParams(String host, int port) {
    public ServerParams(String host, int port) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
    }
}
