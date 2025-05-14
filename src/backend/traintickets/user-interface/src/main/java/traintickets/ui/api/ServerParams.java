package traintickets.ui.api;

import java.util.Objects;

public record ServerParams(String host, int port, long timeout) {
    public ServerParams(String host, int port, long timeout) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.timeout = timeout;
    }
}
