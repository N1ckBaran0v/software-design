package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.Railcar;
import ru.traintickets.businesslogic.model.TrainId;

public interface RailcarRepository {
    void addRailcar(Railcar railcar);
    Iterable<Railcar> getRailcars(String type);
    Iterable<Railcar> getRailcars(TrainId trainId);
}
