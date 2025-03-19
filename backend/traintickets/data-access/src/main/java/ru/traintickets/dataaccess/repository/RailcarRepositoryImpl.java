package ru.traintickets.dataaccess.repository;

import ru.traintickets.businesslogic.model.Railcar;
import ru.traintickets.businesslogic.model.TrainId;
import ru.traintickets.businesslogic.repository.RailcarRepository;

public final class RailcarRepositoryImpl implements RailcarRepository {
    @Override
    public void addRailcar(Railcar railcar) {
    }

    @Override
    public Iterable<Railcar> getRailcarsByType(String type) {
        return null;
    }

    @Override
    public Iterable<Railcar> getRailcarsByTrain(TrainId trainId) {
        return null;
    }
}
