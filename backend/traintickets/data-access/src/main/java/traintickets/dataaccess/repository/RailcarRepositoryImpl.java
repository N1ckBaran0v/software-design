package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.RailcarRepository;

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
