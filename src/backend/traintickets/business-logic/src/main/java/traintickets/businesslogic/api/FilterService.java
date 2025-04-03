package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Filter;

import java.util.List;
import java.util.UUID;

public interface FilterService {
    void addFilter(UUID sessionId, Filter filter);
    Filter getFilter(UUID sessionId, String filterName);
    List<Filter> getFilters(UUID sessionId);
    void deleteFilter(UUID sessionId, String filterName);
}
