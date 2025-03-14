package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.Railcar;

public interface RailcarRepository {
    void addRailcar(Railcar railcar);
    Iterable<Railcar> getRailcars(String type);
}
