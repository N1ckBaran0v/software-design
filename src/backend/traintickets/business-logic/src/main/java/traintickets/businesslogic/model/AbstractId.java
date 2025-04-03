package traintickets.businesslogic.model;

import java.util.Objects;

public abstract class AbstractId {
    private final Object id;

    protected AbstractId(Object id) {
        this.id = Objects.requireNonNull(id);
    }

    public final Object id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractId that = (AbstractId) o;
        return Objects.equals(id.toString(), that.id.toString());
    }
}
