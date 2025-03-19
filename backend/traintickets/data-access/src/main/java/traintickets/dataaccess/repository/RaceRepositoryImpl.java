package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.repository.RaceRepository;

import java.util.Optional;

public final class RaceRepositoryImpl implements RaceRepository {
    @Override
    public void addRace(Race race) throws TrainAlreadyReservedException {
    }

    @Override
    public Optional<Race> getRace(RaceId raceId) {
        return Optional.empty();
    }

    @Override
    public Iterable<Race> getRaces(Filter filter) {
        return null;
    }

    @Override
    public void updateRace(Race race) {
    }

    @Override
    public void deleteRace(RaceId raceId) {
    }
}
