package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;

import java.util.Date;
import java.util.Optional;

public final class TrainRepositoryImpl implements TrainRepository {
    @Override
    public void addTrain(Train train) {
    }

    @Override
    public Optional<Train> getTrain(TrainId trainId) {
        return Optional.empty();
    }

    @Override
    public Iterable<Train> getTrains(Date start, Date end) {
        return null;
    }
}
