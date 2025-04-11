package traintickets.ui.api;

import java.util.Map;
import java.util.UUID;

public interface HttpContext {
    Map<String, String> getPathParameters();
    UUID getSessionId();
    <T> T getRequestBody(Class<T> clazz);
}
