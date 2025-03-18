package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.TrainService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.Railcar;
import ru.traintickets.businesslogic.model.Train;
import ru.traintickets.businesslogic.model.TrainId;
import ru.traintickets.businesslogic.repository.RailcarRepository;
import ru.traintickets.businesslogic.repository.TrainRepository;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class TrainServiceImpl implements TrainService {
    private final TrainRepository trainRepository;
    private final RailcarRepository railcarRepository;

    public TrainServiceImpl(TrainRepository trainRepository, RailcarRepository railcarRepository) {
        this.trainRepository = Objects.requireNonNull(trainRepository);
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
    }

    @Override
    public void addTrain(Train train) {
        trainRepository.addTrain(train);
    }

    @Override
    public Train getTrain(TrainId trainId) {
        return trainRepository.getTrain(trainId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Train %d not found", trainId.id())));
    }

    @Override
    public List<Train> getTrains(Date start, Date end) {
        return StreamSupport.stream(trainRepository.getTrains(start, end).spliterator(), false).toList();
    }

    @Override
    public void addRailcar(Railcar railcar) {
        railcarRepository.addRailcar(railcar);
    }

    @Override
    public List<Railcar> getRailcars(String type) {
        return StreamSupport.stream(railcarRepository.getRailcars(type).spliterator(), false).toList();
    }
}
