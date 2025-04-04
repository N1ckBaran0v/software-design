package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TrainService {
    void addTrain(UUID sessionId, Train train);
    Train getTrain(UUID sessionId, TrainId trainId);
    List<Train> getTrains(UUID sessionId, Date start, Date end);
    void addRailcar(UUID sessionId, Railcar railcar);
    List<Railcar> getRailcars(UUID sessionId, String type);
}
