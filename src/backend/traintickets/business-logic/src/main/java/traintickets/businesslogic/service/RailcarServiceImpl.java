package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RailcarService;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class RailcarServiceImpl implements RailcarService {
    private final RailcarRepository railcarRepository;

    public RailcarServiceImpl(RailcarRepository railcarRepository) {
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
    }

    @Override
    public void addRailcar(UserInfo userInfo, Railcar railcar) {
        railcar.validate();
        railcarRepository.addRailcar(userInfo.role(), railcar);
    }

    @Override
    public List<Railcar> getRailcars(UserInfo userInfo, String type) {
        var role = userInfo.role();
        return StreamSupport.stream(railcarRepository.getRailcarsByType(role, type).spliterator(), false).toList();
    }
}
