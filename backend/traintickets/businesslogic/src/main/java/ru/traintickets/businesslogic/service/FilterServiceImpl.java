package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.FilterService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.Filter;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.FilterRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class FilterServiceImpl implements FilterService {
    private final FilterRepository filterRepository;

    public FilterServiceImpl(FilterRepository filterRepository) {
        this.filterRepository = Objects.requireNonNull(filterRepository);
    }

    @Override
    public void addFilter(Filter filter) {
        filterRepository.addFilter(filter);
    }

    @Override
    public Filter getFilter(UserId userId, String filterName) {
        return filterRepository.getFilter(userId, filterName).orElseThrow(
                () -> new EntityNotFoundException(String.format("Filter %s not found", filterName)));
    }

    @Override
    public List<Filter> getFilters(UserId userId) {
        return StreamSupport.stream(filterRepository.getFilters(userId).spliterator(), false).toList();
    }

    @Override
    public void deleteFilter(UserId userId, String filterName) {
        filterRepository.deleteFilter(userId, filterName);
    }
}
