package traintickets.businesslogic.service;

import traintickets.businesslogic.api.TrainService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class TrainServiceImpl implements TrainService {
    private final TrainRepository trainRepository;

    public TrainServiceImpl(TrainRepository trainRepository) {
        this.trainRepository = Objects.requireNonNull(trainRepository);
    }

    @Override
    public void addTrain(Train train) {
        train.validate();
        trainRepository.addTrain(train);
    }

    @Override
    public Train getTrain(TrainId trainId) {
        return trainRepository.getTrain(trainId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Train %s not found", trainId.id())));
    }

    @Override
    public List<Train> getTrains(Date start, Date end) {
        return StreamSupport.stream(trainRepository.getTrains(start, end).spliterator(), false).toList();
    }
}
