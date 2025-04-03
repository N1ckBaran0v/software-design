package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;

import java.util.Optional;

public interface FilterRepository {
    void addFilter(String role, Filter filter);
    Optional<Filter> getFilter(String role, UserId userId, String name);
    Iterable<Filter> getFilters(String role, UserId userId);
    void deleteFilter(String role, UserId userId, String name);
}
