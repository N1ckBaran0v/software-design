package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;

public interface RailcarService {
    void addRailcar(UserInfo userInfo, Railcar railcar);
    List<Railcar> getRailcars(UserInfo userInfo, String type);
}
