package traintickets.ui.api;

import java.util.Objects;

public abstract class AbstractRequestHandler implements RequestHandler {
    private final String httpMethod;
    private final String path;

    public AbstractRequestHandler(String httpMethod, String path) {
        this.httpMethod = Objects.requireNonNull(httpMethod);
        this.path = Objects.requireNonNull(path);
    }

    public String getRequestType() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }
}
