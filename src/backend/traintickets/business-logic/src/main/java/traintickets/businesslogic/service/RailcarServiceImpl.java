package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RailcarService;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.repository.RailcarRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class RailcarServiceImpl implements RailcarService {
    private final RailcarRepository railcarRepository;

    public RailcarServiceImpl(RailcarRepository railcarRepository) {
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
    }

    @Override
    public void addRailcar(Railcar railcar) {
        railcar.validate();
        railcarRepository.addRailcar(railcar);
    }

    @Override
    public List<Railcar> getRailcars(String type) {
        return StreamSupport.stream(railcarRepository.getRailcarsByType(type).spliterator(), false).toList();
    }
}
