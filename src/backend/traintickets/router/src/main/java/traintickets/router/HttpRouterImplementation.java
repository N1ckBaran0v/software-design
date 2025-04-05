package traintickets.router;

import java.util.ArrayList;
import java.util.regex.Pattern;

public final class HttpRouterImplementation<T> implements HttpRouter<T> {
    private final HttpRouterNode<T> root;
    private final Pattern PATH_DELIMITER = Pattern.compile("/");

    public HttpRouterImplementation() {
        this.root = new HttpRouterNode<>(null);
    }

    @Override
    public void register(String method, String path, T value) {
        var parsed = parsePath(path, true);
        root.register(path, method, parsed.iterator(), value);
    }

    @Override
    public HttpRouterValue<T> get(String httpMethod, String path) {
        var parsed = parsePath(path, false);
        return root.get(httpMethod, parsed.iterator());
    }

    private Iterable<String> parsePath(String path, boolean allowParameters) {
        var pattern = allowParameters ? PathPatterns.both : PathPatterns.nonParametrized;
        var result = new ArrayList<String>();
        var parts = PATH_DELIMITER.split(path);
        if (parts.length < 2) {
            throw new InvalidPathException(path);
        }
        if (!parts[0].isEmpty()) {
            throw new InvalidPathException(path);
        }
        for (int i = 1; i < parts.length; i++) {
            var part = parts[i];
            if (part.isEmpty() && i + 1 < parts.length) {
                throw new InvalidPathException(path);
            } else if (!part.isEmpty()) {
                if (pattern.matcher(part).matches()) {
                    result.add(part);
                } else {
                    throw new InvalidPathException(path);
                }
            }
        }
        return result;
    }
}
