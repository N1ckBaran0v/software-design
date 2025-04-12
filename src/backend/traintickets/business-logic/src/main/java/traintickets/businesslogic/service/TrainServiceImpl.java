package traintickets.businesslogic.service;

import traintickets.businesslogic.api.TrainService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.businesslogic.session.SessionManager;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class TrainServiceImpl implements TrainService {
    private final TrainRepository trainRepository;
    private final SessionManager sessionManager;

    public TrainServiceImpl(TrainRepository trainRepository, SessionManager sessionManager) {
        this.trainRepository = Objects.requireNonNull(trainRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void addTrain(String sessionId, Train train) {
        train.validate();
        trainRepository.addTrain(sessionManager.getUserInfo(sessionId).role(), train);
    }

    @Override
    public Train getTrain(String sessionId, TrainId trainId) {
        return trainRepository.getTrain(sessionManager.getUserInfo(sessionId).role(), trainId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Train %s not found", trainId.id())));
    }

    @Override
    public List<Train> getTrains(String sessionId, Date start, Date end) {
        var role = sessionManager.getUserInfo(sessionId).role();
        return StreamSupport.stream(trainRepository.getTrains(role, start, end).spliterator(), false).toList();
    }
}
