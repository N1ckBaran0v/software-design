package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Filter;

import java.util.List;

public interface FilterService {
    void addFilter(String sessionId, Filter filter);
    Filter getFilter(String sessionId, String filterName);
    List<Filter> getFilters(String sessionId);
    void deleteFilter(String sessionId, String filterName);
}
