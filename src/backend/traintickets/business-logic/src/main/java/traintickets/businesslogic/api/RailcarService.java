package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Railcar;

import java.util.List;

public interface RailcarService {
    void addRailcar(Railcar railcar);
    List<Railcar> getRailcars(String type);
}
