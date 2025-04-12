package traintickets.ui.group;

import io.javalin.apibuilder.EndpointGroup;

import java.util.Objects;

public abstract class AbstractEndpointGroup implements EndpointGroup {
    private final String path;

    public AbstractEndpointGroup(String path) {
        this.path = Objects.requireNonNull(path);
    }

    public String getPath() {
        return path;
    }
}
