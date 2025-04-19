package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.transport.UserInfo;

import java.util.Date;
import java.util.List;

public interface TrainService {
    void addTrain(UserInfo userInfo, Train train);
    Train getTrain(UserInfo userInfo, TrainId trainId);
    List<Train> getTrains(UserInfo userInfo, Date start, Date end);
}
