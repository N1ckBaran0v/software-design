package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;

import java.util.Optional;

public final class FilterRepositoryImpl implements FilterRepository {
    @Override
    public void addFilter(Filter filter) {
    }

    @Override
    public Optional<Filter> getFilter(UserId userId, String name) {
        return Optional.empty();
    }

    @Override
    public Iterable<Filter> getFilters(UserId userId) {
        return null;
    }

    @Override
    public void deleteFilter(UserId userId, String name) {
    }
}
