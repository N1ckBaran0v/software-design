package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.TrainId;

public interface RailcarRepository {
    void addRailcar(Railcar railcar);
    Iterable<Railcar> getRailcarsByType(String type);
    Iterable<Railcar> getRailcarsByTrain(TrainId trainId);
}
