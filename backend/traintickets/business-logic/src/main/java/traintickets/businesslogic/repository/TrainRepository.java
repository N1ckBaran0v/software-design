package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;

import java.util.Date;
import java.util.Optional;

public interface TrainRepository {
    void addTrain(Train train);
    Optional<Train> getTrain(TrainId trainId);
    Iterable<Train> getTrains(Date start, Date end);
}
