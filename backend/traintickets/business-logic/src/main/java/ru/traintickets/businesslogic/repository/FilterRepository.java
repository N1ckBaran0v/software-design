package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.Filter;
import ru.traintickets.businesslogic.model.UserId;

import java.util.Optional;

public interface FilterRepository {
    void addFilter(Filter filter);
    Optional<Filter> getFilter(UserId userId, String name);
    Iterable<Filter> getFilters(UserId userId);
    void deleteFilter(UserId userId, String name);
}
