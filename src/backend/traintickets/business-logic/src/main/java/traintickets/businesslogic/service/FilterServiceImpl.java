package traintickets.businesslogic.service;

import traintickets.businesslogic.api.FilterService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.businesslogic.session.SessionManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class FilterServiceImpl implements FilterService {
    private final FilterRepository filterRepository;
    private final SessionManager sessionManager;

    public FilterServiceImpl(FilterRepository filterRepository, SessionManager sessionManager) {
        this.filterRepository = Objects.requireNonNull(filterRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void addFilter(String sessionId, Filter filter) {
        filter.saveValidate();
        var userInfo = sessionManager.getUserInfo(sessionId);
        if (!userInfo.userId().equals(filter.user())) {
            throw new InvalidEntityException("Invalid userId");
        }
        filterRepository.addFilter(userInfo.role(), filter);
    }

    @Override
    public Filter getFilter(String sessionId, String filterName) {
        var userInfo = sessionManager.getUserInfo(sessionId);
        return filterRepository.getFilter(userInfo.role(), userInfo.userId(), filterName).orElseThrow(
                () -> new EntityNotFoundException(String.format("Filter %s not found", filterName)));
    }

    @Override
    public List<Filter> getFilters(String sessionId) {
        var userInfo = sessionManager.getUserInfo(sessionId);
        return StreamSupport.stream(filterRepository.getFilters(userInfo.role(), userInfo.userId()).spliterator(), false).toList();
    }

    @Override
    public void deleteFilter(String sessionId, String filterName) {
        var userInfo = sessionManager.getUserInfo(sessionId);
        filterRepository.deleteFilter(userInfo.role(), userInfo.userId(), filterName);
    }
}
