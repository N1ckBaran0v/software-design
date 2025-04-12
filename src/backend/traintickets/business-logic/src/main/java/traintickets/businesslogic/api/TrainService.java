package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;

import java.util.Date;
import java.util.List;

public interface TrainService {
    void addTrain(String sessionId, Train train);
    Train getTrain(String sessionId, TrainId trainId);
    List<Train> getTrains(String sessionId, Date start, Date end);
}
