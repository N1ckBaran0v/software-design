package traintickets.businesslogic.service;

import traintickets.businesslogic.api.TrainService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.businesslogic.transport.UserInfo;

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
    public void addTrain(UserInfo userInfo, Train train) {
        train.validate();
        trainRepository.addTrain(userInfo.role(), train);
    }

    @Override
    public Train getTrain(UserInfo userInfo, TrainId trainId) {
        return trainRepository.getTrain(userInfo.role(), trainId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Train %s not found", trainId.id())));
    }

    @Override
    public List<Train> getTrains(UserInfo userInfo, Date start, Date end) {
        var role = userInfo.role();
        return StreamSupport.stream(trainRepository.getTrains(role, start, end).spliterator(), false).toList();
    }
}
