package traintickets.di;

public final class CycleDetectedException extends RuntimeException {
    CycleDetectedException(Iterable<String> classes) {
        super(String.format("Cycle detected: %s", String.join(" -> ", classes)));
    }
}
