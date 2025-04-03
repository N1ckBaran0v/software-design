package traintickets.businesslogic.service;

import traintickets.businesslogic.api.FilterService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class FilterServiceImpl implements FilterService {
//    private final FilterRepository filterRepository;
//
//    public FilterServiceImpl(FilterRepository filterRepository) {
//        this.filterRepository = Objects.requireNonNull(filterRepository);
//    }
//
//    @Override
//    public void addFilter(Filter filter) {
//        filter.validate();
//        filterRepository.addFilter(filter);
//    }
//
//    @Override
//    public Filter getFilter(UserId userId, String filterName) {
//        return filterRepository.getFilter(userId, filterName).orElseThrow(
//                () -> new EntityNotFoundException(String.format("Filter %s not found", filterName)));
//    }
//
//    @Override
//    public List<Filter> getFilters(UserId userId) {
//        return StreamSupport.stream(filterRepository.getFilters(userId).spliterator(), false).toList();
//    }
//
//    @Override
//    public void deleteFilter(UserId userId, String filterName) {
//        filterRepository.deleteFilter(userId, filterName);
//    }
}
