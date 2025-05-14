package traintickets.businesslogic.service;

import traintickets.businesslogic.api.FilterService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class FilterServiceImpl implements FilterService {
    private final FilterRepository filterRepository;

    public FilterServiceImpl(FilterRepository filterRepository) {
        this.filterRepository = Objects.requireNonNull(filterRepository);
    }

    @Override
    public void addFilter(UserInfo userInfo, Filter filter) {
        filter.saveValidate();
        if (!userInfo.userId().equals(filter.user())) {
            throw new InvalidEntityException("Invalid userId");
        }
        filterRepository.addFilter(userInfo.role(), filter);
    }

    @Override
    public Filter getFilter(UserInfo userInfo, String filterName) {
        return filterRepository.getFilter(userInfo.role(), userInfo.userId(), filterName).orElseThrow(
                () -> new EntityNotFoundException(String.format("Filter %s not found", filterName)));
    }

    @Override
    public List<Filter> getFilters(UserInfo userInfo) {
        return StreamSupport.stream(filterRepository.getFilters(userInfo.role(), userInfo.userId()).spliterator(), false).toList();
    }

    @Override
    public void deleteFilter(UserInfo userInfo, String filterName) {
        filterRepository.deleteFilter(userInfo.role(), userInfo.userId(), filterName);
    }
}
