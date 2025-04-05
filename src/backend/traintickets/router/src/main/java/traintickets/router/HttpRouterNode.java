package traintickets.router;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class HttpRouterNode<T> {
    private final Map<String, HttpRouterValue<T>> values = new ConcurrentHashMap<>();
    private final Map<String, HttpRouterNode<T>> children = new ConcurrentHashMap<>();
    private HttpRouterNode<T> unnamedChildren;
    private final String name;

    HttpRouterNode(String name) {
        this.name = name;
    }

    void register(String method, String fullPath, Iterator<String> pathIterator, T value) {
        if (pathIterator.hasNext()) {
            var path = pathIterator.next();
            var next = children.get(path);
            if (next == null) {
                if ("*".equals(path) || path.startsWith(":")) {
                    if (unnamedChildren == null) {
                        unnamedChildren = new HttpRouterNode<>(path);
                    }
                    next = unnamedChildren;
                } else {
                    next = new HttpRouterNode<>(path);
                    children.put(path, next);
                }
            }
            next.register(method, fullPath, pathIterator, value);
        } else {
            add(method, fullPath, value);
        }
    }

    private void add(String method, String path, T value) {
        if (values.containsKey(method)) {
            throw new ValueAlreadyRegisteredException(method);
        }
        values.put(method, new HttpRouterValue<>(path, method, value));
    }

    HttpRouterValue<T> get(String method, Iterator<String> pathIterator) {
        var answer = (HttpRouterValue<T>) null;
        if (pathIterator.hasNext()) {
            var path = pathIterator.next();
            var next = children.get(path);
            if (next == null) {
                if (unnamedChildren != null) {
                    answer = unnamedChildren.get(method, pathIterator);
                } else if ("*".equals(name)) {
                    answer = get(method, pathIterator);
                } else {
                    throw new PathNotFoundException();
                }

            } else {
                answer = next.get(method, pathIterator);
            }
        } else {
            if (!values.containsKey(method)) {
                if (values.isEmpty()) {
                    throw new PathNotFoundException();
                } else {
                    throw new MethodNotAllowedException(method);
                }
            }
            answer = values.get(method);
        }
        return answer;
    }
}
