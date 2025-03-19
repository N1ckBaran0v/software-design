package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.model.Railcar;
import ru.traintickets.businesslogic.model.Train;
import ru.traintickets.businesslogic.model.TrainId;

import java.util.Date;
import java.util.List;

public interface TrainService {
    void addTrain(Train train);
    Train getTrain(TrainId trainId);
    List<Train> getTrains(Date start, Date end);
    void addRailcar(Railcar railcar);
    List<Railcar> getRailcars(String type);
}
