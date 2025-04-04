package traintickets.businesslogic.service;

import traintickets.businesslogic.api.TrainService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.businesslogic.session.SessionManager;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

public final class TrainServiceImpl implements TrainService {
    private final TrainRepository trainRepository;
    private final RailcarRepository railcarRepository;
    private final SessionManager sessionManager;

    public TrainServiceImpl(TrainRepository trainRepository,
                            RailcarRepository railcarRepository,
                            SessionManager sessionManager) {
        this.trainRepository = Objects.requireNonNull(trainRepository);
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void addTrain(UUID sessionId, Train train) {
        train.validate();
        trainRepository.addTrain(sessionManager.getUserInfo(sessionId).role(), train);
    }

    @Override
    public Train getTrain(UUID sessionId, TrainId trainId) {
        return trainRepository.getTrain(sessionManager.getUserInfo(sessionId).role(), trainId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Train %s not found", trainId.id())));
    }

    @Override
    public List<Train> getTrains(UUID sessionId, Date start, Date end) {
        var role = sessionManager.getUserInfo(sessionId).role();
        return StreamSupport.stream(trainRepository.getTrains(role, start, end).spliterator(), false).toList();
    }

    @Override
    public void addRailcar(UUID sessionId, Railcar railcar) {
        railcar.validate();
        railcarRepository.addRailcar(sessionManager.getUserInfo(sessionId).role(), railcar);
    }

    @Override
    public List<Railcar> getRailcars(UUID sessionId, String type) {
        var role = sessionManager.getUserInfo(sessionId).role();
        return StreamSupport.stream(railcarRepository.getRailcarsByType(role, type).spliterator(), false).toList();
    }
}
