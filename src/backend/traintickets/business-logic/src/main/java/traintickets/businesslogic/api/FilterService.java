package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;

public interface FilterService {
    void addFilter(UserInfo userInfo, Filter filter);
    Filter getFilter(UserInfo userInfo, String filterName);
    List<Filter> getFilters(UserInfo userInfo);
    void deleteFilter(UserInfo userInfo, String filterName);
}
