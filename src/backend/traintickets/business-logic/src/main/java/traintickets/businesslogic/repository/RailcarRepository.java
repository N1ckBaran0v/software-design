package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.TrainId;

public interface RailcarRepository {
    void addRailcar(String role, Railcar railcar);
    Iterable<Railcar> getRailcarsByType(String role, String type);
    Iterable<Railcar> getRailcarsByTrain(String role, TrainId trainId);
}
