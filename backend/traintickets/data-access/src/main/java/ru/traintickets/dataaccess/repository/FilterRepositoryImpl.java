package ru.traintickets.dataaccess.repository;

import ru.traintickets.businesslogic.model.Filter;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.FilterRepository;

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
