package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;

import java.util.List;

public interface FilterService {
    void addFilter(Filter filter);
    Filter getFilter(UserId userId, String filterName);
    List<Filter> getFilters(UserId userId);
    void deleteFilter(UserId userId, String filterName);
}
